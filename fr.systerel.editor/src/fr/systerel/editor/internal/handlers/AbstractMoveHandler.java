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

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.List;

import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.ModelOperations.ModelPosition;
import fr.systerel.editor.internal.documentModel.ModelOperations.Move;
import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.editors.SelectionController;

/**
 * @author Thomas Muller
 */
public abstract class AbstractMoveHandler extends AbstractEditionHandler {

	@Override
	public String handleSelection(RodinEditor rEditor, int offset) {
		final SelectionController selController = rEditor
				.getSelectionController();
		if (!selController.hasSelectedElements()) {
			// TODO expand selection
			return "Nothing to move up"; // nothing to move up
		}
		final ILElement[] selected = selController.getSelectedElements();
		final IElementType<?> siblingType = checkAndGetSameType(selected);
		if (siblingType == null)
			return "";
		final DocumentMapper mapper = rEditor.getDocumentMapper();
		final ModelPosition pos = getMovementPosition(mapper, selected,
				siblingType);
		if (pos == null) {
			return "";
		}
		final boolean success = new Move(pos).perform(asList(selected));
		return (success)?"Move successful":"Move operation failed";
	}

	protected abstract ModelPosition getMovementPosition(DocumentMapper mapper,
			ILElement[] selected, IElementType<?> siblingType);

	/**
	 * Checks that the elements passed as parameters have the same type. Returns
	 * <code>null</code> otherwise.
	 * 
	 * @param elems
	 *            the elements we want to check the type
	 * @return the type of the given elements if they all have this type,
	 *         <code>null</code> otherwise
	 */
	protected static IElementType<?> checkAndGetSameType(ILElement[] elems) {
		if (elems.length == 0) {
			return null;
		}
		final IInternalElementType<? extends IInternalElement> type = elems[0]
				.getElementType();
		for (ILElement el : elems) {
			if (el.getElementType() != type) {
				return null;
			}
		}
		return type;
	}

	protected static ILElement getParent(ILElement[] selected) {
		return selected[0].getParent();
	}
	
	@Override
	protected boolean checkEnablement(RodinEditor editor, int caretOffset) {
		final ILElement[] selection = editor.getSelectionController()
		.getSelectedElements();
		if (selection.length <= 0) {
			return false;
		}
		return isMovePossible(selection);
	}
	
	protected static ILElement getNextSibling(ILElement element,
			List<ILElement> sameType) {
		for (int i = 0; i < sameType.size() - 1; i++) {
			if (sameType.get(i).equals(element)) {
				return sameType.get(i + 1);
			}
		}
		return null;
	}

	protected static ILElement getPreviousSibling(ILElement element,
			List<ILElement> sameType) {
		for (int i = sameType.size() - 1; i > 0; i--) {
			if (sameType.get(i).equals(element)) {
				return sameType.get(i - 1);
			}
		}
		return null;
	}

	protected abstract ILElement getSibling(ILElement element,
			List<ILElement> sameType);
	
	protected boolean isMovePossible(ILElement[] selection) {
		if (selection.length == 0) {
			return false;
		}
		final IElementType<?> type = checkAndGetSameType(selection);
		if (type == null)
			return false;
		final ILElement parent = getParent(selection);
		if (parent == null) {
			return false;
		}
		final List<ILElement> cot = parent
				.getChildrenOfType((IInternalElementType<?>) type);
		final List<ILElement> al = Arrays.asList(selection);
		for (int i = 0; i <= al.size() - 1; i++) {
			final ILElement selectedElem = al.get(i);
			final ILElement sibling = getSibling(selectedElem, cot);
			if (sibling != null && !sibling.isImplicit()
					&& !al.contains(sibling)) {
				return true;
			}
		}
		return false;
	}

}
