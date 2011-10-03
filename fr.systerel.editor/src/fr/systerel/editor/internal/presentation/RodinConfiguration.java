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
package fr.systerel.editor.internal.presentation;

import static fr.systerel.editor.internal.presentation.IRodinColorConstant.ATTRIBUTE;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.COMMENT;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.COMMENT_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.COMMENT_HEADER;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.COMMENT_HEADER_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.CONTENT;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.CONTENT_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.DEFAULT;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.IDENTIFIER;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.IDENTIFIER_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.IMPLICIT_ATTRIBUTE;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.IMPLICIT_COMMENT;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.IMPLICIT_CONTENT;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.IMPLICIT_IDENTIFIER;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.IMPLICIT_LABEL;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.KEYWORD_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.LABEL;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.LABEL_DEBUG_BG;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.SECTION;
import static fr.systerel.editor.internal.presentation.IRodinColorConstant.SECTION_DEBUG_BG;
import static org.eventb.core.EventBAttributes.COMMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.rodinp.core.IAttributeType;

import fr.systerel.editor.internal.documentModel.DocumentMapper;

/**
 * A customization of the SourceViewerConfiguration
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
		
		public boolean isKindOfEditable() {
			return false;
		}
		
	}
	
	public static class AttributeContentType extends ContentType {

		// note: the attribute type may be null when unknown
		private final IAttributeType attributeType;

		public AttributeContentType(String contentName, boolean isImplicit,
				RGB color, IAttributeType attributeType) {
			super(contentName, !isImplicit, isImplicit, color);
			this.attributeType = attributeType;
		}

		/**
		 * Returns the attribute type, or <code>null</code> when it is unknown.
		 * 
		 * @return an attribute type, or <code>null</code>
		 */
		public IAttributeType getAttributeType() {
			return attributeType;
		}
		
		@Override
		public boolean isKindOfEditable() {
			return true;
		}
		
	}

	// FIXME take care about attribute type extensions
	// TODO make contributions out of the following constants
	public static final ContentType LEFT_PRESENTATION_TYPE = new ContentType(
			"__left_presentation_", false, false, CONTENT);
	
	public static final ContentType PRESENTATION_TYPE = new ContentType(
			"__presentation_", false, false, CONTENT);
	
	public static final ContentType IDENTIFIER_TYPE = new AttributeContentType(
			"__identifier", false, IDENTIFIER, IDENTIFIER_ATTRIBUTE);
	public static final ContentType IMPLICIT_IDENTIFIER_TYPE = new AttributeContentType(
			"__implicit_identifier", true, IMPLICIT_IDENTIFIER, IDENTIFIER_ATTRIBUTE);

	// TODO rename to FORMULA_TYPE
	public static final ContentType CONTENT_TYPE = new AttributeContentType("__content",
			false, CONTENT, null);
	public static final ContentType IMPLICIT_CONTENT_TYPE = new AttributeContentType(
			"__implicit_content", true, IMPLICIT_CONTENT, null);

	public static final ContentType COMMENT_TYPE = new AttributeContentType("__comment",
			false, COMMENT, COMMENT_ATTRIBUTE);
	public static final ContentType IMPLICIT_COMMENT_TYPE = new AttributeContentType(
			"__implicit_comment", true, IMPLICIT_COMMENT, COMMENT_ATTRIBUTE);

	public static final ContentType LABEL_TYPE = new AttributeContentType("__label",
			false, LABEL, LABEL_ATTRIBUTE);
	public static final ContentType IMPLICIT_LABEL_TYPE = new AttributeContentType(
			"__implicit_label", true, IMPLICIT_LABEL, LABEL_ATTRIBUTE);

	public static final ContentType BOLD_LABEL_TYPE = new AttributeContentType("__bold_label",
			false, LABEL, LABEL_ATTRIBUTE);
	public static final ContentType BOLD_IMPLICIT_LABEL_TYPE = new AttributeContentType(
			"__bold_implicit_label", true, IMPLICIT_LABEL, LABEL_ATTRIBUTE);
	
	public static final ContentType ATTRIBUTE_TYPE = new AttributeContentType(
			"__attribute", false, ATTRIBUTE, null);
	public static final ContentType IMPLICIT_ATTRIBUTE_TYPE = new AttributeContentType(
			"__implicit_attribute", true, IMPLICIT_ATTRIBUTE, null);

	public static final ContentType KEYWORD_TYPE = new ContentType("__keyword",
			false, false, DEFAULT);
	public static final ContentType SECTION_TYPE = new ContentType("__section",
			false, false, SECTION);
	public static final ContentType COMMENT_HEADER_TYPE = new ContentType(
			"__comment_header", false, false, COMMENT_HEADER);

	private static ContentType[] contentTypes = new ContentType[] {
		LEFT_PRESENTATION_TYPE,
		PRESENTATION_TYPE,
		IDENTIFIER_TYPE,
		IMPLICIT_IDENTIFIER_TYPE,
		CONTENT_TYPE,
		IMPLICIT_CONTENT_TYPE,
		COMMENT_TYPE,
		IMPLICIT_COMMENT_TYPE,
		LABEL_TYPE,
		IMPLICIT_LABEL_TYPE,
		BOLD_LABEL_TYPE,
		BOLD_IMPLICIT_LABEL_TYPE,
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
	
	public static ContentType getAttributeContentType(IAttributeType type) {
		return new AttributeContentType("__attribute", false, ATTRIBUTE, type);
	}
	
	public static ContentType getContentType(String name) {
		return typesByName.get(name);
	}
	
	private ColorManager colorManager;
	private DocumentMapper documentMapper;
	private IAnnotationHover annotationHover;
	private ITextHover textHover;

	public RodinConfiguration(ColorManager colorManager,
			DocumentMapper documentMapper) {
		this.colorManager = colorManager;
		this.documentMapper = documentMapper;
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDENTIFIER_TYPE.getName(), PRESENTATION_TYPE.getName(),
				COMMENT_TYPE.getName(), CONTENT_TYPE.getName() };
	}

	@Override
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
				colorManager.getColor(IMPLICIT_LABEL_TYPE.getColor()), bgColor,
				SWT.NONE));
		reconciler.setDamager(rdr, IMPLICIT_LABEL_TYPE.getName());
		reconciler.setRepairer(rdr, IMPLICIT_LABEL_TYPE.getName());
		
		bgColor = (COLOR_DEBUG) ? colorManager
				.getColor(IRodinColorConstant.LABEL_DEBUG_BG) : null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(BOLD_LABEL_TYPE.getColor()), bgColor, SWT.BOLD));
		reconciler.setDamager(rdr, BOLD_LABEL_TYPE.getName());
		reconciler.setRepairer(rdr, BOLD_LABEL_TYPE.getName());

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(LABEL_DEBUG_BG) : null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(BOLD_IMPLICIT_LABEL_TYPE.getColor()), bgColor,
				SWT.BOLD));
		reconciler.setDamager(rdr, BOLD_IMPLICIT_LABEL_TYPE.getName());
		reconciler.setRepairer(rdr,BOLD_IMPLICIT_LABEL_TYPE.getName());


		bgColor = (COLOR_DEBUG) ? colorManager.getColor(SECTION_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(SECTION_TYPE.getColor()), bgColor,
				SWT.ITALIC));
		reconciler.setDamager(rdr, SECTION_TYPE.getName());
		reconciler.setRepairer(rdr, SECTION_TYPE.getName());

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(KEYWORD_DEBUG_BG)
				: null;
		final TextAttribute att = new TextAttribute(
				colorManager.getColor(KEYWORD_TYPE.getColor()), bgColor,
				SWT.ITALIC);
		rdr = new RodinDamagerRepairer(att);

		reconciler.setDamager(rdr, KEYWORD_TYPE.getName());
		reconciler.setRepairer(rdr, KEYWORD_TYPE.getName());

		bgColor = (COLOR_DEBUG) ? colorManager
				.getColor(COMMENT_HEADER_DEBUG_BG) : null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(COMMENT_HEADER_TYPE.getColor()), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, COMMENT_HEADER_TYPE.getName());
		reconciler.setRepairer(rdr, COMMENT_HEADER_TYPE.getName());
		
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(PRESENTATION_TYPE.getColor()), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, PRESENTATION_TYPE.getName());
		reconciler.setRepairer(rdr, PRESENTATION_TYPE.getName());
		
		reconciler.setDamager(rdr, LEFT_PRESENTATION_TYPE.getName());
		reconciler.setRepairer(rdr, LEFT_PRESENTATION_TYPE.getName());
		return reconciler;
	}
	
	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		if (annotationHover == null)
			annotationHover = new RodinProblemAnnotationHover();
		return annotationHover;
	}
	
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer,
			String contentType) {
		if (textHover == null)
			textHover = new RodinProblemTextHover(sourceViewer);
		return textHover;
	}
	
}
