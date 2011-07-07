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

import org.rodinp.core.IInternalElement;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.actions.operations.AtomicOperation;
import fr.systerel.editor.internal.actions.operations.History;
import fr.systerel.editor.internal.actions.operations.OperationFactory;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.editors.SelectionController;

/**
 * @author Thomas Muller & Nicolas Beauger
 */
public class DeleteHandler extends AbstractEditionHandler {

	@Override
	protected String handleSelection(RodinEditor editor, int offset) {
		if (editor.isOverlayActive()) {
			return "No deletion when the overlay editor is active";
		}
		final SelectionController selection = editor.getSelectionController();
		final ILElement[] selected = selection.getSelectedElements();
		final AtomicOperation op;
		if(selected.length == 0) {
			op = deleteCurrentElement(editor, offset);
			if (op == null) {
				return "No element to delete or non deletable element";
			}
		} else {
			op = deleteElements(selected);
		}
		History.getInstance().addOperation(op);
		return "Element deleted.";
	}

	private AtomicOperation deleteElements(ILElement[] selected) {
		final IInternalElement[] rElements = toIElements(selected);
		return OperationFactory.deleteElement(rElements, false);
	}
	
	private static IInternalElement[] toIElements(ILElement[] elements) {
		final IInternalElement[] result = new IInternalElement[elements.length];
		for (int i = 0; i < elements.length; i++) {
			final ILElement element = elements[i];
			result[i] = element.getElement();
		}
		return result;
	}

	private AtomicOperation deleteCurrentElement(RodinEditor editor, int offset) {
		final Interval inter = editor.getDocumentMapper()
				.findFirstElementIntervalAfter(offset);
		if (inter == null)
			return null;
		final ILElement element = inter.getElement();
		if (element == null || element.isImplicit()) {
			return null;
		}
		return OperationFactory.deleteElement(element.getElement());
	}
}
