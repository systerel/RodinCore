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
package fr.systerel.editor.handlers;

import org.eclipse.swt.graphics.Point;
import org.rodinp.core.IElementType;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.documentModel.DocumentMapper;
import fr.systerel.editor.documentModel.EditorElement;
import fr.systerel.editor.documentModel.ModelOperations.ModelPosition;
import fr.systerel.editor.editors.RodinEditor;

/**
 * @author Thomas Muller
 */
public class MoveDownHandler extends AbstractMoveHandler {

	@Override
	protected ModelPosition getMovementPosition(DocumentMapper mapper,
			ILElement[] selected, IElementType<?> siblingType) {
		final ILElement parent = getParent(selected);
		final int offset = findBiggestOffset(selected, mapper);
		final ModelPosition pos = mapper.findModelPositionSiblingAfter(offset,
				parent, siblingType);
		return pos;
	}

	/**
	 * @return returns the biggest offset value of the given elements, or
	 *         <code>-1</code> if no elements is given, or no offset has been
	 *         found
	 */
	private static int findBiggestOffset(ILElement[] elems,
			DocumentMapper mapper) {
		int oo = -1;
		for (ILElement e : elems) {
			final EditorElement eElement = mapper.findEditorElement(e);
			if (eElement == null)
				continue;
			final Point enclosingRange = mapper.getEnclosingRange(eElement);
			final int o = enclosingRange.y + 1;
			if (o > oo || oo == -1) {
				oo = o;
			}
		}
		return oo;
	}

}
