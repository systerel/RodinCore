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
package fr.systerel.editor.internal.documentModel;

import static fr.systerel.editor.internal.editors.EditPos.newPosOffLen;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;

import fr.systerel.editor.internal.editors.EditPos;

/**
 * This class represents an item in the editor that can spread several intervals
 * in the text. There are EditorElements for LightElements and EditorSections
 * that group EditorElements from their IInternalElementType. An EditorSection
 * is for example spanning all variables EditorElements.
 */
public abstract class EditorItem {


	// this position is updated by the annotation model
	// => keep as is, do not use EditPos instead
	protected Position foldingPosition;
	// TODO: check if it is really necessary to save the annotations.
	protected ProjectionAnnotation foldingAnnotation;


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

	public void clearFolding() {
		foldingPosition = null;
		foldingAnnotation = null;
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

	protected int getOffset() {
		if (foldingPosition != null) {
			return foldingPosition.offset;
		}
		return -1;
	}

	protected int getLength() {
		if (foldingPosition != null) {
			return foldingPosition.getLength();
		}
		return -1;
	}

	public EditPos getPos() {
		return newPosOffLen(getOffset(), getLength(), false);
	}
	
	public boolean contains(int offset) {
		return getPos().includes(offset);
	}
}
