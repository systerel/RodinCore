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
package fr.systerel.editor.internal.handlers;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.rodinp.core.IElementType;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.ModelOperations.ModelPosition;

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
			final Point enclosingRange = mapper.getEnclosingPoint(e);
			if (enclosingRange == null)
				continue;
			final int o = enclosingRange.y + 1;
			if (o > oo || oo == -1) {
				oo = o;
			}
		}
		return oo;
	}

	@Override
	protected ILElement getSibling(ILElement element, List<ILElement> sameType) {
		return getNextSibling(element, sameType);
	}
	
}
