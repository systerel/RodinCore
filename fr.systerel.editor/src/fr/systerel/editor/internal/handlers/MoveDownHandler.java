/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
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

import org.rodinp.core.IElementType;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.ModelOperations.ModelPosition;
import fr.systerel.editor.internal.editors.EditPos;

/**
 * @author Thomas Muller
 */
public class MoveDownHandler extends AbstractMoveHandler {

	@Override
	protected ModelPosition getMovementPosition(DocumentMapper mapper,
			ILElement[] selected, IElementType<?> siblingType) {
		final ILElement parent = getParent(selected);
		final ILElement lastElement = findElemWithBiggestOffset(selected,
				mapper);
		final List<ILElement> sameType = parent.getChildrenOfType(lastElement
				.getElementType());
		final ILElement nextSibling = getNextSibling(lastElement, sameType);
		ILElement nextNextSibling = null;
		if (nextSibling != null) {
			nextNextSibling = getNextSibling(nextSibling, sameType);
		}
		return new ModelPosition(parent, nextNextSibling);
	}

	/**
	 * @return returns the biggest offset value of the given elements, or
	 *         <code>null</code> if no elements is given, or no offset has been
	 *         found
	 */
	private static ILElement findElemWithBiggestOffset(ILElement[] elems,
			DocumentMapper mapper) {
		int oo = -1;
		ILElement elem = null;
		for (ILElement e : elems) {
			final EditPos enclosingPos = mapper.getEnclosingPosition(e);
			if (enclosingPos == null)
				continue;
			final int o = enclosingPos.getEnd() + 1;
			if (o > oo || oo == -1) {
				oo = o;
				elem = e;
			}
		}
		return elem;
	}

	@Override
	protected ILElement getSibling(ILElement element, List<ILElement> sameType) {
		return getNextSibling(element, sameType);
	}

}
