/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.editors;

import static fr.systerel.editor.editors.IRodinColorConstant.ATTRIBUTE;
import static fr.systerel.editor.editors.IRodinColorConstant.COMMENT;
import static fr.systerel.editor.editors.IRodinColorConstant.COMMENT_DEBUG_BG;
import static fr.systerel.editor.editors.IRodinColorConstant.COMMENT_HEADER;
import static fr.systerel.editor.editors.IRodinColorConstant.COMMENT_HEADER_DEBUG_BG;
import static fr.systerel.editor.editors.IRodinColorConstant.CONTENT;
import static fr.systerel.editor.editors.IRodinColorConstant.CONTENT_DEBUG_BG;
import static fr.systerel.editor.editors.IRodinColorConstant.DEFAULT;
import static fr.systerel.editor.editors.IRodinColorConstant.IDENTIFIER;
import static fr.systerel.editor.editors.IRodinColorConstant.IDENTIFIER_DEBUG_BG;
import static fr.systerel.editor.editors.IRodinColorConstant.IMPLICIT_ATTRIBUTE;
import static fr.systerel.editor.editors.IRodinColorConstant.IMPLICIT_COMMENT;
import static fr.systerel.editor.editors.IRodinColorConstant.IMPLICIT_CONTENT;
import static fr.systerel.editor.editors.IRodinColorConstant.IMPLICIT_IDENTIFIER;
import static fr.systerel.editor.editors.IRodinColorConstant.IMPLICIT_LABEL;
import static fr.systerel.editor.editors.IRodinColorConstant.KEYWORD_DEBUG_BG;
import static fr.systerel.editor.editors.IRodinColorConstant.LABEL;
import static fr.systerel.editor.editors.IRodinColorConstant.LABEL_DEBUG_BG;
import static fr.systerel.editor.editors.IRodinColorConstant.SECTION;
import static fr.systerel.editor.editors.IRodinColorConstant.SECTION_DEBUG_BG;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import fr.systerel.editor.documentModel.DocumentMapper;
import fr.systerel.editor.documentModel.RodinDamagerRepairer;

/**
 *
 */
public class RodinConfiguration extends SourceViewerConfiguration {

	public static class ContentType {

		private final String name;
		private final boolean isEditable;
		private final boolean isImplicit;
		private final RGB color;

		public ContentType(String contentName, boolean isEditable,
				boolean isImplicit, RGB color) {
			this.name = contentName;
			this.isEditable = isEditable;
			this.isImplicit = isImplicit;
			this.color = color;
		}

		public String getName() {
			return name;
		}

		public boolean isEditable() {
			return isEditable;
		}

		public RGB getColor() {
			return color;
		}
		
		public boolean isImplicit() {
			return isImplicit;
		}
	}

	// FIXME take care about attribute type extensions
	// TODO make contributions out of the following constants
	public static final ContentType PRESENTATION_TYPE = new ContentType(
			"__separator_", false, false, CONTENT);
	
	public static final ContentType IDENTIFIER_TYPE = new ContentType(
			"__identifier", true, false, IDENTIFIER);
	public static final ContentType IMPLICIT_IDENTIFIER_TYPE = new ContentType(
			"__implicit_identifier", false, true, IMPLICIT_IDENTIFIER);

	public static final ContentType CONTENT_TYPE = new ContentType("__content",
			true, false, CONTENT);
	public static final ContentType IMPLICIT_CONTENT_TYPE = new ContentType(
			"__implicit_content", false, true, IMPLICIT_CONTENT);

	public static final ContentType COMMENT_TYPE = new ContentType("__comment",
			true, false, COMMENT);
	public static final ContentType IMPLICIT_COMMENT_TYPE = new ContentType(
			"__implicit_comment", false, true, IMPLICIT_COMMENT);

	public static final ContentType LABEL_TYPE = new ContentType("__label",
			true, false, LABEL);
	public static final ContentType IMPLICIT_LABEL_TYPE = new ContentType(
			"__implicit_label", false, true, IMPLICIT_LABEL);

	public static final ContentType ATTRIBUTE_TYPE = new ContentType(
			"__attribute", true, false, ATTRIBUTE);
	public static final ContentType IMPLICIT_ATTRIBUTE_TYPE = new ContentType(
			"__implicit_attribute", false, true, IMPLICIT_ATTRIBUTE);

	public static final ContentType KEYWORD_TYPE = new ContentType("__keyword",
			false, false, DEFAULT);
	public static final ContentType SECTION_TYPE = new ContentType("__section",
			false, false, SECTION);
	public static final ContentType COMMENT_HEADER_TYPE = new ContentType(
			"__comment_header", false, false, COMMENT_HEADER);

	private static ContentType[] contentTypes = new ContentType[] {
		PRESENTATION_TYPE,
		IDENTIFIER_TYPE,
		IMPLICIT_IDENTIFIER_TYPE,
		CONTENT_TYPE,
		IMPLICIT_CONTENT_TYPE,
		COMMENT_TYPE,
		IMPLICIT_COMMENT_TYPE,
		LABEL_TYPE,
		IMPLICIT_LABEL_TYPE,
		ATTRIBUTE_TYPE,
		IMPLICIT_ATTRIBUTE_TYPE,
		KEYWORD_TYPE,
		SECTION_TYPE,
		COMMENT_HEADER_TYPE,
	};
	private static Map<String, ContentType> typesByName = new HashMap<String, ContentType>();
	static {
		for (ContentType contentType : contentTypes) {
			typesByName.put(contentType.getName(), contentType);
		}
	}
	
	public static ContentType getContentType(String name) {
		return typesByName.get(name);
	}
	
	private ColorManager colorManager;
	private DocumentMapper documentMapper;

	public RodinConfiguration(ColorManager colorManager,
			DocumentMapper documentMapper) {
		this.colorManager = colorManager;
		this.documentMapper = documentMapper;
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDENTIFIER_TYPE.getName(),
				COMMENT_TYPE.getName(), CONTENT_TYPE.getName() };
	}

	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		final PresentationReconciler reconciler = new PresentationReconciler();

		// FIXME temporary code
		// Do something better
		final boolean COLOR_DEBUG = false;

		Color bgColor = (COLOR_DEBUG) ? colorManager.getColor(COMMENT_DEBUG_BG)
				: null;
		RodinDamagerRepairer rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(COMMENT_TYPE.getColor()), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, COMMENT_TYPE.getName());
		reconciler.setRepairer(rdr, COMMENT_TYPE.getName());

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(COMMENT_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IMPLICIT_COMMENT_TYPE.getColor()), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, IMPLICIT_COMMENT_TYPE.getName());
		reconciler.setRepairer(rdr, IMPLICIT_COMMENT_TYPE.getName());

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(CONTENT_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(CONTENT_TYPE.getColor()), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, CONTENT_TYPE.getName());
		reconciler.setRepairer(rdr, CONTENT_TYPE.getName());

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(CONTENT_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IMPLICIT_CONTENT_TYPE.getColor()), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, IMPLICIT_CONTENT_TYPE.getName());
		reconciler.setRepairer(rdr, IMPLICIT_CONTENT_TYPE.getName());

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(IDENTIFIER_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IDENTIFIER_TYPE.getColor()), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, IDENTIFIER_TYPE.getName());
		reconciler.setRepairer(rdr, IDENTIFIER_TYPE.getName());

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(IDENTIFIER_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IMPLICIT_IDENTIFIER_TYPE.getColor()), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, IMPLICIT_IDENTIFIER_TYPE.getName());
		reconciler.setRepairer(rdr, IMPLICIT_IDENTIFIER_TYPE.getName());

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(CONTENT_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(ATTRIBUTE_TYPE.getColor()), bgColor,
				SWT.ITALIC));
		reconciler.setDamager(rdr, ATTRIBUTE_TYPE.getName());
		reconciler.setRepairer(rdr, ATTRIBUTE_TYPE.getName());

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(CONTENT_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IMPLICIT_ATTRIBUTE_TYPE.getColor()),
				bgColor, SWT.ITALIC));
		reconciler.setDamager(rdr, IMPLICIT_ATTRIBUTE_TYPE.getName());
		reconciler.setRepairer(rdr, IMPLICIT_ATTRIBUTE_TYPE.getName());

		bgColor = (COLOR_DEBUG) ? colorManager
				.getColor(IRodinColorConstant.LABEL_DEBUG_BG) : null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(LABEL_TYPE.getColor()), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, LABEL_TYPE.getName());
		reconciler.setRepairer(rdr, LABEL_TYPE.getName());

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(LABEL_DEBUG_BG) : null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IMPLICIT_LABEL_TYPE.getColor()), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, IMPLICIT_LABEL_TYPE.getName());
		reconciler.setRepairer(rdr, IMPLICIT_LABEL_TYPE.getName());

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(SECTION_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(LABEL_TYPE.getColor()), bgColor, SWT.BOLD));
		reconciler.setDamager(rdr, SECTION_TYPE.getName());
		reconciler.setRepairer(rdr, SECTION_TYPE.getName());

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(KEYWORD_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(LABEL_TYPE.getColor()), bgColor, SWT.BOLD | SWT.ITALIC));
		reconciler.setDamager(rdr, KEYWORD_TYPE.getName());
		reconciler.setRepairer(rdr, KEYWORD_TYPE.getName());

		bgColor = (COLOR_DEBUG) ? colorManager
				.getColor(COMMENT_HEADER_DEBUG_BG) : null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(COMMENT_HEADER_TYPE.getColor()), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, COMMENT_HEADER_TYPE.getName());
		reconciler.setRepairer(rdr, COMMENT_HEADER_TYPE.getName());

		return reconciler;
	}
}
