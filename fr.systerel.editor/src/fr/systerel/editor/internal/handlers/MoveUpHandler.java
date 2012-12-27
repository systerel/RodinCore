/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.handlers;

import java.util.List;

import org.rodinp.core.IElementType;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.EditorElement;
import fr.systerel.editor.internal.documentModel.ModelOperations.ModelPosition;

/**
 * @author Thomas Muller
 */
public class MoveUpHandler extends AbstractMoveHandler {

	@Override
	protected ModelPosition getMovementPosition(DocumentMapper mapper,
			ILElement[] selected, IElementType<?> siblingType) {
		final ILElement parent = getParent(selected);
		final ILElement smallest = findElementWithSmallestOffset(selected,
				mapper);
		final List<ILElement> sameType = parent.getChildrenOfType(smallest
				.getElementType());
		final ILElement previousSibling = getPreviousSibling(smallest, sameType);
		return new ModelPosition(parent, previousSibling);
	}

	/**
	 * @return returns the element with the smallest offset value from the given
	 *         elements, or <code>null</code> if no elements is given, or no
	 *         offset has been found
	 */
	private static ILElement findElementWithSmallestOffset(ILElement[] elems,
			DocumentMapper mapper) {
		int oo = -1;
		ILElement elem = null;
		for (ILElement e : elems) {
			final EditorElement eElement = mapper.findEditorElement(e);
			if (eElement == null)
				continue;
			final int o = eElement.getOffset();
			if (o < oo || oo == -1) {
				oo = o;
				elem = e;
			}
		}
		return elem;
	}

	@Override
	protected ILElement getSibling(ILElement element, List<ILElement> sameType) {
		return getPreviousSibling(element, sameType);
	}

}
