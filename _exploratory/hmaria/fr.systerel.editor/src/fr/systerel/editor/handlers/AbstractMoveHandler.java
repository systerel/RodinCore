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

import static java.util.Arrays.asList;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.documentModel.DocumentMapper;
import fr.systerel.editor.documentModel.ModelOperations.ModelPosition;
import fr.systerel.editor.documentModel.ModelOperations.Move;
import fr.systerel.editor.editors.RodinEditor;
import fr.systerel.editor.editors.SelectionController;

/**
 * @author Thomas Muller
 * 
 */
public abstract class AbstractMoveHandler extends AbstractEditorHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (!(editor instanceof RodinEditor)) {
			return null;
		}
		final RodinEditor rEditor = (RodinEditor) editor;
		final SelectionController selController = rEditor
				.getSelectionController();
		if (!selController.hasSelectedElements()) {
			return null; // nothing to move up
		}
		final ILElement[] selected = selController.getSelectedElements();
		final IElementType<?> siblingType = checkAndGetSameType(selected);
		if (siblingType == null)
			return null;
		final DocumentMapper mapper = rEditor.getDocumentMapper();
		final ModelPosition pos = getMovementPosition(mapper, selected,
				siblingType);
		if (pos == null) {
			return null;
		}
		new Move(pos).perform(asList(selected));
		rEditor.resync2(null);
		rEditor.getSite().getShell().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				rEditor.getSelectionController().selectItems(selected);

			}
		});
		return null;
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
	private static IElementType<?> checkAndGetSameType(ILElement[] elems) {
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
		return editor.getSelectionController().getSelectedElements().length > 0;
	}

}
