/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/

package fr.systerel.editor.internal.documentModel;

import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;

/**
 *	Holds a annotation and a position for a marker.
 */
public class MarkerAnnotationPosition {
	
	private Position position;
	private SimpleMarkerAnnotation annotation;

	public MarkerAnnotationPosition(Position position,
			SimpleMarkerAnnotation annotation) {
		this.position = position;
		this.annotation = annotation;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public SimpleMarkerAnnotation getAnnotation() {
		return annotation;
	}
	

}
