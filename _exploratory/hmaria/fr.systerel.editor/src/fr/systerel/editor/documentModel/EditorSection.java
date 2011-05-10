/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.documentModel;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

public class EditorSection extends EditorItem {

	private final IInternalElementType<? extends IInternalElement> type;

	public EditorSection(IInternalElementType<?> elementType) {
		this.type = elementType;
	}

	/**
	 * Returns the type of the (direct children) elements which are contained in
	 * this section.
	 * 
	 * @return the type of the direct children of the section
	 */
	public IInternalElementType<?> getElementType() {
		return type;
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

}
