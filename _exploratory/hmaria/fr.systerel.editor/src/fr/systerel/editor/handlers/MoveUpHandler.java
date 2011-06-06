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

import java.util.List;

import org.rodinp.core.IElementType;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.documentModel.DocumentMapper;
import fr.systerel.editor.documentModel.EditorElement;
import fr.systerel.editor.documentModel.ModelOperations.ModelPosition;

/**
 * @author Thomas Muller
 */
public class MoveUpHandler extends AbstractMoveHandler {

	@Override
	protected ModelPosition getMovementPosition(DocumentMapper mapper,
			ILElement[] selected, IElementType<?> siblingType) {
		final ILElement parent = getParent(selected);
		final int offset = findSmallestOffset(selected, mapper);
		final ModelPosition pos = mapper.findModelPositionSiblingBefore(offset,
				parent, siblingType);
		return pos;
	}

	/**
	 * @return returns the smallest offset value of the given elements, or
	 *         <code>-1</code> if no elements is given, or no offset has been
	 *         found
	 */
	private static int findSmallestOffset(ILElement[] elems,
			DocumentMapper mapper) {
		int oo = -1;
		for (ILElement e : elems) {
			final EditorElement eElement = mapper.findEditorElement(e);
			if (eElement == null)
				continue;
			final int o = eElement.getOffset();
			if (o < oo || oo == -1) {
				oo = o;
			}
		}
		return oo;
	}

	@Override
	protected ILElement getSibling(ILElement element, List<ILElement> sameType) {
		return getPreviousSibling(element, sameType);
	}

}
