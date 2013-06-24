/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.documentModel;

import static fr.systerel.editor.internal.editors.EditPos.computeLength;

import java.util.ArrayList;
import java.util.List;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.presentation.RodinConfiguration.AttributeContentType;
import fr.systerel.editor.internal.presentation.RodinConfiguration.ContentType;

public class EditorElement extends EditorItem {

	private final ILElement element;
	private final List<Interval> intervals = new ArrayList<Interval>();

	public EditorElement(ILElement element) {
		this.element = element;
	}
	
	/**
	 * Returns the light element associated to this item.  
	 * 
	 * @return the element associated with this EditorItem.
	 */
	public ILElement getLightElement() {
		return element;
	}
	
	public IRodinElement getRodinElement() {
		return (IRodinElement) element.getElement();
	}
	
	/**
	 * Returns all the intervals of the item
	 * 
	 * @return the intervals
	 */
	public List<Interval> getIntervals() {
		return intervals;
	}

	/**
	 * The first interval found with the given type
	 * 
	 * @param type
	 *            the content type to search the interval for
	 * @return the first interval with the given content type
	 */
	public Interval getInterval(ContentType type) {
		for (Interval i : intervals) {
			if (i.getContentType().equals(type)) {
				return i;
			}
		}
		return null;
	}

	public Interval getInterval(IAttributeType attrType) {
		for (Interval i : intervals) {
			final ContentType contentType = i.getContentType();
			if (!(contentType instanceof AttributeContentType)) {
				continue;
			}
			final IAttributeType ctAttrType = ((AttributeContentType) contentType).getAttributeType();
			if (attrType.equals(ctAttrType)) {
				return i;
			}
		}
		return null;
	}
	
	public void addInterval(Interval interval) {
		// sorted insertion
		int index = intervals.size();
		for (Interval inter : intervals) {
			if (inter.getOffset() > interval.getOffset()) {
				index = intervals.indexOf(inter);
			}
		}
		intervals.add(index, interval);
	}
	
	@Override
	public int getOffset() {
		if (intervals.isEmpty()) {
			return -1;
		}
		return intervals.get(0).getOffset();
	}
	
	@Override
	public int getLength() {
		if (intervals.isEmpty()) {
			return -1;
		}
		return computeLength(getOffset(), getEnd());
	}

	private int getEnd() {
		if (intervals.isEmpty()) {
			return -1;
		}
		final Interval last = intervals.get(intervals.size() - 1);
		return last.getLastIndex();
	}

	public boolean isFoldable() {
		return isDirectChildOfRoot(element) && hasChildren(element);
	}

	private static boolean isDirectChildOfRoot(ILElement el) {
		return el.getRoot().equals(el.getParent());
	}
	
	private static boolean hasChildren(ILElement el) {
		return !el.getChildren().isEmpty();
	}

	public boolean explicitlyContains(int offset) {
		for (Interval inter : intervals) {
			if (inter.contains(offset)) {
				return true;
			}
		}
		return false;
	}
}
