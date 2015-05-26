/*******************************************************************************
 * Copyright (c) 2015 Systerel and others.
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

import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * Handler for the symbol table insertion command "org.eventb.ui.edit.insert".
 * 
 * @author Thomas Muller
 */
public class SymbolInsertionHandler extends AbstractEditorHandler {

	/**
	 * The string parameter of the insertion command corresponding to the
	 * Event-B symbol to insert.
	 */
	private static final String TEXT_INSERTION_PARAMETER_ID = "org.eventb.ui.edit.insert.text";

	private static final String NO_ACTIVE_OVERLAY_MSG = "No object editor to insert symbol.";

	private static final String NOT_RODIN_EDITOR_MSG = "The current active editor is not a Rodin Editor.";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final RodinEditor editor = getActiveRodinEditor();
		if (editor == null) {
			return NOT_RODIN_EDITOR_MSG;
		}
		if (!editor.isOverlayActive()) {
			return NO_ACTIVE_OVERLAY_MSG;
		}
		final String symbol = event.getParameter(TEXT_INSERTION_PARAMETER_ID);
		editor.getOverlayEditor().insert((symbol == null) ? "" : symbol);
		return null;
	}

	@Override
	protected boolean isEnabled(RodinEditor editor, int caretOffset) {
		return editor.isOverlayActive();
	}

}
