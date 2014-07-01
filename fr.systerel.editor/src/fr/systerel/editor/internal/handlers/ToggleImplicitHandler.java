/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
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
 * A handler to show/hide implicit elements.
 * 
 * @author Thomas Muller
 */
public class ToggleImplicitHandler extends AbstractEditorHandler {

	public static final String COMMAND_ID = "fr.systerel.editor.toggleImplicit";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final RodinEditor editor = getActiveRodinEditor(event);
		if (editor == null) {
			return null;
		}
		editor.toggleShowImplicitElements();
		return null;
	}

}