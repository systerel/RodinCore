/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
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
import org.eclipse.core.commands.IHandler;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.api.itf.ILFile;

import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.editors.SelectionController;

/**
 * Select all handler at the Rodin element level. <br />
 * This handler selects the root ILElement handled by the active Rodin Editor
 * which let all its child elements be selected (this is the current
 * implementation provided from the selectionController.
 */
public class SelectAllHandler extends AbstractEditorHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final RodinEditor editor = getActiveRodinEditor(event);
		if (editor != null) {
			final ILFile file = editor.getResource();
			final SelectionController ctlr = editor.getSelectionController();
			ctlr.selectItems(new ILElement[] { file.getRoot() });
		}
		return null;
	}
}
