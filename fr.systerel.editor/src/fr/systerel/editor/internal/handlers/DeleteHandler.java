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

import org.eventb.ui.manipulation.ElementManipulationFacade;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.documentModel.EditorElement;
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
		if (selected.length == 0) {
			final boolean deleted = deleteCurrentElement(editor, offset);
			if (!deleted) {
				return "No element to delete or non deletable element";
			}
		} else {
			deleteElements(selected);
		}
		return "Element deleted.";
	}

	private void deleteElements(ILElement[] selected) {
		final IInternalElement[] rElements = toIElements(selected);
		ElementManipulationFacade.deleteElement(rElements, true);
	}

	private static IInternalElement[] toIElements(ILElement[] elements) {
		final IInternalElement[] result = new IInternalElement[elements.length];
		for (int i = 0; i < elements.length; i++) {
			final ILElement element = elements[i];
			result[i] = element.getElement();
		}
		return result;
	}

	private boolean deleteCurrentElement(RodinEditor editor, int offset) {
		final EditorElement item = editor.getDocumentMapper()
				.findItemContaining(offset);
		if (item != null && item.getLightElement() != null) {
			return ElementManipulationFacade.deleteElement(item
					.getLightElement().getElement());
		}
		final Interval inter = editor.getDocumentMapper()
				.findFirstElementIntervalAfter(offset);
		if (inter == null)
			return false;
		final ILElement element = inter.getElement();
		if (element == null || element.isImplicit()) {
			return false;
		}
		return ElementManipulationFacade.deleteElement(element.getElement());
	}
	
}
