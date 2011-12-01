/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.handlers;

import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.editors.EditPos;
import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.editors.SelectionController;

/**
 * @author Nicolas Beauger
 * 
 */
public abstract class AbstractSelectHandler extends AbstractEditionHandler {

	@Override
	public boolean isEnabled() {
		final RodinEditor editor = getActiveRodinEditor();
		return editor != null;
	}
	
	@Override
	protected String handleSelection(RodinEditor editor, int offset) {
		if (editor.isOverlayActive()) {
			handleOverlayAction(editor);
			return "";
		}
		final SelectionController selController = editor
				.getSelectionController();
		final ILElement element = selController.getSelectionAt(offset);
		final EditPos reveal;
		if (element == null) {
			// not selected => expand selection
			reveal = selController.toggleSelection(offset);
		} else {
			final ILElement sibling = getSibling(editor, element);
			if (sibling == null) {
				return "";
			}
			if (selController.isSelected(sibling)) {
				// deselect current
				selController.toggleSelection(element);
				reveal = editor.getDocumentMapper().getEnclosingPosition(
						sibling);
			} else {
				// select sibling
				reveal = selController.toggleSelection(sibling);
			}
		}
		if (reveal != null) {
			editor.reveal(reveal);
		}
		return "";
	};

	protected abstract ILElement getSibling(RodinEditor rEditor,
			ILElement element);
	
	protected abstract void handleOverlayAction(RodinEditor editor);

}
