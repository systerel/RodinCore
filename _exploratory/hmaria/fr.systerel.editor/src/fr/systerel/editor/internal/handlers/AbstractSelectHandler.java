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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.Position;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.editors.SelectionController;

/**
 * @author Nicolas Beauger
 *
 */
public abstract class AbstractSelectHandler extends AbstractEditorHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final RodinEditor rEditor = getActiveRodinEditor(event);
		if (rEditor == null) {
			return null;
		}
		final SelectionController selController = rEditor
				.getSelectionController();
		final int offset = rEditor.getCurrentOffset();
		final ILElement element = selController.getSelectionAt(offset);
		final Position reveal;
		if (element == null) {
			// not selected => expand selection
			reveal = selController.toggleSelection(offset);
		} else {
			final ILElement sibling = getSibling(rEditor, element);
			if (sibling == null) {
				return null;
			}
			if (selController.isSelected(sibling)) {
				// deselect current
				selController.toggleSelection(element);
				reveal = rEditor.getDocumentMapper().getEnclosingPosition(sibling);
			} else {
				// select sibling
				reveal = selController.toggleSelection(sibling);
			}
		}
		if (reveal != null) {
			rEditor.reveal(reveal.offset, reveal.length);
		}
		return null;
	}

	protected abstract ILElement getSibling(RodinEditor rEditor, ILElement element);

}
