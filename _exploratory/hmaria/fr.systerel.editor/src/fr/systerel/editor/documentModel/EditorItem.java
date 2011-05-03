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
package fr.systerel.editor.documentModel;

import java.util.ArrayList;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;

import fr.systerel.editor.presentation.RodinConfiguration.ContentType;

/**
 * This class represents an item in the editor that can spread several intervals
 * in the text. There are EditorElements for LightElements and EditorSections
 * that group EditorElements from their IInternalElementType. An EditorSection
 * is for example spannig all variables EditorElements.
 */
public abstract class EditorItem {

	private ArrayList<Interval> intervals = new ArrayList<Interval>();

	private Position foldingPosition;
	// TODO: check if it is really necessary to save the annotations.
	private ProjectionAnnotation foldingAnnotation;

	/**
	 * Returns all the intervals of the item
	 * 
	 * @return the intervals
	 */
	public ArrayList<Interval> getIntervals() {
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

	public boolean isCollapsed() {
		return (foldingAnnotation != null) ? foldingAnnotation.isCollapsed()
				: false;
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
			foldingPosition = new Position(start, length);
			foldingAnnotation = new ProjectionAnnotation(false);
		}
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

	public int getOffset() {
		if (foldingPosition != null) {
			return foldingPosition.offset;
		} else if (intervals.size() > 0) {
			return intervals.get(0).getOffset();
		}
		return -1;
	}

	public int getLength() {
		if (foldingPosition != null) {
			return foldingPosition.getLength();
		} else if (intervals.size() > 0) {
			Interval last = intervals.get(intervals.size() - 1);
			return last.getLastIndex() - getOffset();
		}
		return -1;
	}

	public boolean contains(int offset) {
		final int thisOffset = getOffset();
		return thisOffset <= offset && offset <= thisOffset + getLength();
	}
}
