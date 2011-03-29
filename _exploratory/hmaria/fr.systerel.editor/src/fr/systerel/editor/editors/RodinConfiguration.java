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

import static fr.systerel.editor.editors.IRodinColorConstant.COMMENT;
import static fr.systerel.editor.editors.IRodinColorConstant.COMMENT_DEBUG_BG;
import static fr.systerel.editor.editors.IRodinColorConstant.COMMENT_HEADER;
import static fr.systerel.editor.editors.IRodinColorConstant.COMMENT_HEADER_DEBUG_BG;
import static fr.systerel.editor.editors.IRodinColorConstant.CONTENT;
import static fr.systerel.editor.editors.IRodinColorConstant.CONTENT_DEBUG_BG;
import static fr.systerel.editor.editors.IRodinColorConstant.IDENTIFIER;
import static fr.systerel.editor.editors.IRodinColorConstant.IDENTIFIER_DEBUG_BG;
import static fr.systerel.editor.editors.IRodinColorConstant.IMPLICIT_COMMENT;
import static fr.systerel.editor.editors.IRodinColorConstant.IMPLICIT_CONTENT;
import static fr.systerel.editor.editors.IRodinColorConstant.IMPLICIT_IDENTIFIER;
import static fr.systerel.editor.editors.IRodinColorConstant.IMPLICIT_LABEL;
import static fr.systerel.editor.editors.IRodinColorConstant.KEYWORD_DEBUG_BG;
import static fr.systerel.editor.editors.IRodinColorConstant.LABEL;
import static fr.systerel.editor.editors.IRodinColorConstant.LABEL_DEBUG_BG;
import static fr.systerel.editor.editors.IRodinColorConstant.SECTION_DEBUG_BG;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import fr.systerel.editor.documentModel.DocumentMapper;
import fr.systerel.editor.documentModel.RodinDamagerRepairer;

/**
 *
 */
public class RodinConfiguration extends SourceViewerConfiguration {

	public static final String IDENTIFIER_TYPE = "__identifier";
	public static final String IMPLICIT_IDENTIFIER_TYPE = "__implicit_identifier";

	public static final String CONTENT_TYPE = "__content";
	public static final String IMPLICIT_CONTENT_TYPE = "__implicit_content";

	public static final String COMMENT_TYPE = "__comment";
	public static final String IMPLICIT_COMMENT_TYPE = "__implicit_comment";

	public static final String LABEL_TYPE = "__label";
	public static final String IMPLICIT_LABEL_TYPE = "__implicit_label";

	public static final String ATTRIBUTE_TYPE = "__attribute";
	public static final String IMPLICIT_ATTRIBUTE_TYPE = "__implicit_attribute";

	public static final String KEYWORD_TYPE = "__keyword";
	public static final String SECTION_TYPE = "__section";
	public static final String COMMENT_HEADER_TYPE = "__comment_header";

	private ColorManager colorManager;
	private DocumentMapper documentMapper;

	public RodinConfiguration(ColorManager colorManager,
			DocumentMapper documentMapper) {
		this.colorManager = colorManager;
		this.documentMapper = documentMapper;
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDENTIFIER_TYPE, COMMENT_TYPE, LABEL_TYPE,
				CONTENT_TYPE };
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
				colorManager.getColor(COMMENT), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, COMMENT_TYPE);
		reconciler.setRepairer(rdr, COMMENT_TYPE);

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(COMMENT_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IMPLICIT_COMMENT), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, IMPLICIT_COMMENT_TYPE);
		reconciler.setRepairer(rdr, IMPLICIT_COMMENT_TYPE);

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(CONTENT_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(CONTENT), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, CONTENT_TYPE);
		reconciler.setRepairer(rdr, CONTENT_TYPE);

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(CONTENT_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IMPLICIT_CONTENT), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, IMPLICIT_CONTENT_TYPE);
		reconciler.setRepairer(rdr, IMPLICIT_CONTENT_TYPE);

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(IDENTIFIER_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IDENTIFIER), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, IDENTIFIER_TYPE);
		reconciler.setRepairer(rdr, IDENTIFIER_TYPE);

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(IDENTIFIER_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IMPLICIT_IDENTIFIER), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, IMPLICIT_IDENTIFIER_TYPE);
		reconciler.setRepairer(rdr, IMPLICIT_IDENTIFIER_TYPE);

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(CONTENT_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IRodinColorConstant.ATTRIBUTE), bgColor,
				SWT.NONE));
		reconciler.setDamager(rdr, ATTRIBUTE_TYPE);
		reconciler.setRepairer(rdr, ATTRIBUTE_TYPE);

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(CONTENT_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IRodinColorConstant.IMPLICIT_ATTRIBUTE),
				bgColor, SWT.NONE));
		reconciler.setDamager(rdr, IMPLICIT_ATTRIBUTE_TYPE);
		reconciler.setRepairer(rdr, IMPLICIT_ATTRIBUTE_TYPE);

		bgColor = (COLOR_DEBUG) ? colorManager
				.getColor(IRodinColorConstant.LABEL_DEBUG_BG) : null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(LABEL), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, LABEL_TYPE);
		reconciler.setRepairer(rdr, LABEL_TYPE);

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(LABEL_DEBUG_BG) : null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IMPLICIT_LABEL), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, IMPLICIT_LABEL_TYPE);
		reconciler.setRepairer(rdr, IMPLICIT_LABEL_TYPE);

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(SECTION_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(LABEL), bgColor, SWT.BOLD));
		reconciler.setDamager(rdr, SECTION_TYPE);
		reconciler.setRepairer(rdr, SECTION_TYPE);

		bgColor = (COLOR_DEBUG) ? colorManager.getColor(KEYWORD_DEBUG_BG)
				: null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(LABEL), bgColor, SWT.BOLD | SWT.ITALIC));
		reconciler.setDamager(rdr, KEYWORD_TYPE);
		reconciler.setRepairer(rdr, KEYWORD_TYPE);

		bgColor = (COLOR_DEBUG) ? colorManager
				.getColor(COMMENT_HEADER_DEBUG_BG) : null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(COMMENT_HEADER), bgColor, SWT.NONE));
		reconciler.setDamager(rdr, COMMENT_HEADER_TYPE);
		reconciler.setRepairer(rdr, COMMENT_HEADER_TYPE);

		return reconciler;
	}
}
