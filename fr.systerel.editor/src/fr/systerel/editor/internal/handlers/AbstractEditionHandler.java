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
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.ui.EventBUIPlugin;

import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * Abstract class for the editor handlers such as add, remove, copy, etc.
 */
public abstract class AbstractEditionHandler extends AbstractEditorHandler {

	@Override
	protected boolean isEnabled(RodinEditor editor, int caretOffset) {
		return !editor.isOverlayActive();
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final RodinEditor editor = getActiveRodinEditor();
		if (editor == null) {
			return "The current active editor is not a Rodin Editor";
		}
		final ISelection curSel = HandlerUtil.getActiveMenuSelection(event);
		final int offset;
		if (curSel instanceof TextSelection) {
			offset = ((TextSelection) curSel).getOffset();
		} else {
			offset = editor.getCurrentOffset();
		}
		final String result = handleSelection(editor, offset);
		return result;
	}

	protected abstract String handleSelection(RodinEditor editor, int offset);

	protected static Clipboard getClipBoard() {
		final IWorkbench workbench = EventBUIPlugin.getDefault().getWorkbench();
		return new Clipboard(workbench.getDisplay());
	}
	
}
