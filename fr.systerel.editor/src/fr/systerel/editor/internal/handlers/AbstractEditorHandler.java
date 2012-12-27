/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

import fr.systerel.editor.EditorPlugin;
import fr.systerel.editor.internal.editors.RodinEditor;


public abstract class AbstractEditorHandler extends AbstractHandler {

	@Override
	public boolean isEnabled() {
		final RodinEditor rEditor = getActiveRodinEditor();
		return rEditor!= null	&& checkEnablement(rEditor, rEditor.getCurrentOffset());
	}

	/**
	 * Implementors should perform enablement checkings using the given
	 * RodinEditor. Default implementation returns <code>true</code>.
	 * 
	 * @param editor
	 *            the current Rodin Editor
	 * @param caretOffset
	 * @return <code>true</code> is the command should be enabled,
	 *         <code>false</code> otherwise
	 */
	protected boolean checkEnablement(RodinEditor editor, int caretOffset) {
		return true;
	}
	
	protected static RodinEditor getActiveRodinEditor(ExecutionEvent event) {
		final IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (!(editor instanceof RodinEditor)) {
			return null;
		}
		return (RodinEditor) editor;
	}
	
	protected static RodinEditor getActiveRodinEditor() {
		final IWorkbenchPage activePage = EditorPlugin.getActivePage();
		if (activePage == null)
			return null;
		final IEditorPart activeEditor = activePage
				.getActiveEditor();
		if (!(activeEditor instanceof RodinEditor)) {
			return null;
		}
		return (RodinEditor) activeEditor;
	}
	
}
