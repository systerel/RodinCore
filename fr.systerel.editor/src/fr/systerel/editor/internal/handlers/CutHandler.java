/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
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
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.ST;

import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * @author "Thomas Muller"
 */
public class CutHandler extends AbstractEditorHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final RodinEditor editor = getActiveRodinEditor(event);
		if (editor == null) {
			return null;
		}
		if (editor.isOverlayActive()) {
			final IAction action = editor.getOverlayEditorAction(ST.CUT);
			if (action != null) {
				action.run();
				return "Text cut";		
			}
			return "Text cut failed";
		}
		// TODO implement this	behaviour for Rodin elements	
		return null;
	}
	
	@Override
	protected boolean checkEnablement(RodinEditor editor, int caretOffset) {
		return editor.isOverlayActive()
				|| editor.getSelectionController().getSelectedElements().length > 0
				&& !isReadOnlyElementSelected(editor);
	}

}
