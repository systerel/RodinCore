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

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

/**
 * 	
 */
public class EditorElement {

	private IRodinElement element;
	private IInternalElementType<?> elementType;
	private ArrayList<Interval> intervals = new ArrayList<Interval>();
	
	private FoldingPosition foldingPosition;
	
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
		return (foldingPosition != null) ? foldingPosition.isCollapsed() : false;
	}
	
	public void setCollapsed(boolean collapsed) {
		if (foldingPosition != null) {
			foldingPosition.setCollapsed(collapsed);
		}
	}
	
	public void setFoldingPosition(int start, int length) {
		if (foldingPosition != null) {
			foldingPosition.setOffset(start);
			foldingPosition.setLength(length);
		} else {
			foldingPosition = new FoldingPosition(start,length);
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

	public FoldingPosition getFoldingPosition() {
		return foldingPosition;
	}
	
	
	
}
