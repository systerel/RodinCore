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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * @author "Thomas Muller"
 */
public class CutHandler extends AbstractEditorHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final RodinEditor editor = getActiveRodinEditor();
		if (editor == null) {
			return null;
		}
		// TODO implement this		
		return null;
	}
	
	
	@Override
	protected boolean checkEnablement(RodinEditor editor, int caretOffset) {
		return editor.getSelectionController().getSelectedElements().length > 0;
	}

}
