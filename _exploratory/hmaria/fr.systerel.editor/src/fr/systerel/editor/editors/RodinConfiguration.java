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
	public static final String COMMENT_TYPE = "__comment";
	public static final String CONTENT_TYPE = "__content";
	public static final String LABEL_TYPE = "__label";
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

		final boolean COLOR_DEBUG = false;

		Color bgColor = (COLOR_DEBUG) ? colorManager
				.getColor(IRodinColorConstant.COMMENT_DEBUG_BG): null;
		RodinDamagerRepairer rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IRodinColorConstant.COMMENT), bgColor,
				SWT.NONE));
		reconciler.setDamager(rdr, COMMENT_TYPE);
		reconciler.setRepairer(rdr, COMMENT_TYPE);

		bgColor = (COLOR_DEBUG) ? colorManager
				.getColor(IRodinColorConstant.CONTENT_DEBUG_BG): null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IRodinColorConstant.CONTENT), bgColor,
				SWT.NONE));
		reconciler.setDamager(rdr, CONTENT_TYPE);
		reconciler.setRepairer(rdr, CONTENT_TYPE);

		bgColor = (COLOR_DEBUG) ?colorManager
				.getColor(IRodinColorConstant.IDENTIFIER_DEBUG_BG): null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IRodinColorConstant.IDENTIFIER), bgColor,
				SWT.NONE));
		reconciler.setDamager(rdr, IDENTIFIER_TYPE);
		reconciler.setRepairer(rdr, IDENTIFIER_TYPE);

		bgColor = (COLOR_DEBUG) ? colorManager
				.getColor(IRodinColorConstant.SECTION_DEBUG_BG): null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IRodinColorConstant.LABEL), bgColor,
				SWT.BOLD));
		reconciler.setDamager(rdr, SECTION_TYPE);
		reconciler.setRepairer(rdr, SECTION_TYPE);

		bgColor = (COLOR_DEBUG) ? colorManager
				.getColor(IRodinColorConstant.KEYWORD_DEBUG_BG): null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IRodinColorConstant.LABEL), bgColor,
				SWT.BOLD | SWT.ITALIC));
		reconciler.setDamager(rdr, KEYWORD_TYPE);
		reconciler.setRepairer(rdr, KEYWORD_TYPE);
		
		
		bgColor = (COLOR_DEBUG) ? colorManager
				.getColor(IRodinColorConstant.LABEL_DEBUG_BG): null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IRodinColorConstant.LABEL), bgColor,
				SWT.NONE));
		reconciler.setDamager(rdr, LABEL_TYPE);
		reconciler.setRepairer(rdr, LABEL_TYPE);

		bgColor = (COLOR_DEBUG) ? colorManager
				.getColor(IRodinColorConstant.COMMENT_HEADER_DEBUG_BG): null;
		rdr = new RodinDamagerRepairer(new TextAttribute(
				colorManager.getColor(IRodinColorConstant.COMMENT_HEADER), bgColor,
				SWT.NONE));
		reconciler.setDamager(rdr, COMMENT_HEADER_TYPE);
		reconciler.setRepairer(rdr, COMMENT_HEADER_TYPE);

		return reconciler;
	}
}
