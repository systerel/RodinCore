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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;

import fr.systerel.editor.EditorPlugin;
import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * Unselects the selection if any, and abort the edition within the overlay
 * editor.
 * 
 * @author "Thomas Muller"
 */
public class AbortHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IEditorPart activeEditor = EditorPlugin.getActivePage()
				.getActiveEditor();
		if (!(activeEditor instanceof RodinEditor)) {
			return "The current active editor is not a Rodin Editor";
		}
		final RodinEditor editor = (RodinEditor) activeEditor;
		editor.getSelectionController().clearSelection();
		editor.abordEdition();
		return null;
	}

}
