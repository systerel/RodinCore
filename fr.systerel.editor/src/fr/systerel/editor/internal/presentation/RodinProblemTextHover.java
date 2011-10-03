/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.presentation;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * The text hover to display annotation over the problems in the Rodin Editor.
 * @author "Thomas Muller"
 */
public class RodinProblemTextHover extends DefaultTextHover implements ITextHoverExtension {

	private IInformationControlCreator controlCreator;

	public RodinProblemTextHover(ISourceViewer sourceViewer) {
		super(sourceViewer);
	}

	@Override
	protected boolean isIncluded(Annotation annotation) {
		return annotation instanceof RodinProblemAnnotation;
	}

	@Override
	public IInformationControlCreator getHoverControlCreator() {
		if (controlCreator == null)
			controlCreator = getInformationPresenterControlCreator();
		return controlCreator;
	}

	private static IInformationControlCreator getInformationPresenterControlCreator() {
		return new IInformationControlCreator() {
			@Override
			public IInformationControl createInformationControl(Shell shell) {
				return new DefaultInformationControl(shell, true);
			}
		};
	}
		
}
