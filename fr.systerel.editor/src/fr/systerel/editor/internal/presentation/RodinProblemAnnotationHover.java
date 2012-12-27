/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.presentation;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.DefaultAnnotationHover;

/**
 * The annotation hover to display Rodin problems in the Rodin Editor rulers.
 * @author "Thomas Muller"
 */
public class RodinProblemAnnotationHover extends DefaultAnnotationHover {
	
	@Override
	protected boolean isIncluded(Annotation annotation) {
		return annotation instanceof RodinProblemAnnotation;
	}
	
	
}
