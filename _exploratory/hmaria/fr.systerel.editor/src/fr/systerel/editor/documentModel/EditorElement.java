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

package fr.systerel.editor.documentModel;

import java.util.ArrayList;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

/**
 * 	
 */
public class EditorElement {

	private IRodinElement element;
	private IInternalElementType<?> elementType;
	private ArrayList<Interval> intervals = new ArrayList<Interval>();
	
	private Position foldingPosition;
	//TODO: check if it is really necessary to save the annotations.
	private ProjectionAnnotation foldingAnnotation;
	
	public EditorElement(IRodinElement element) {
		this.element = element;
	}

	public EditorElement(IInternalElementType<?> elementType) {
		this.elementType = elementType;
	}
	
	public ArrayList<Interval> getIntervals() {
		return intervals;
	}

	public void addInterval(Interval interval) {
		intervals.add(interval);
	}

	/**
	 * 
	 * @return the element associated with this EditorElement. May be
	 *         <code>null</code>.
	 */
	public IRodinElement getElement() {
		return element;
	}

	public boolean isCollapsed() {
		return (foldingAnnotation != null) ? foldingAnnotation.isCollapsed() : false;
	}
	
	/**
	 * Sets the offset and length of the folding position to the given values.
	 * Creates a new position if none exists yet.
	 * 
	 * @param start
	 * @param length
	 */
	public void setFoldingPosition(int start, int length) {
		if (foldingPosition != null) {
			foldingPosition.setOffset(start);
			foldingPosition.setLength(length);
			foldingAnnotation.markDeleted(false);
		} else {
			foldingPosition = new Position(start,length);
			foldingAnnotation = new ProjectionAnnotation(false);
		}
	}

	/**
	 * 
	 * @return The elementType associated with the EditorElement. May be
	 *         <code>null</code>.
	 */
	public IInternalElementType<?> getElementType() {
		return elementType;
	}

	public Position getFoldingPosition() {
		return foldingPosition;
	}

	public ProjectionAnnotation getFoldingAnnotation() {
		return foldingAnnotation;
	}

	public void setFoldingAnnotation(ProjectionAnnotation foldingAnnotation) {
		this.foldingAnnotation = foldingAnnotation;
	}
	
	
	
}
