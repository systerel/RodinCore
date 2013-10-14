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

/**
 * The handler for the cut operation triggered when the Rodin Editor has the
 * focus.
 */
public class CutHandler extends AbstractEditorHandler {

	@Override
	public boolean isEnabled() {
		// TODO implement cut at the Rodin level.
		return false;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO implement cut at the Rodin level.
		return null;
	}

}
