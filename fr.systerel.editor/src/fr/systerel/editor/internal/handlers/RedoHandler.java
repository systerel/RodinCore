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

import static fr.systerel.editor.internal.editors.RodinEditorUtils.RODIN_HISTORY;

import org.eclipse.core.commands.operations.IUndoContext;

/**
 * Implementation of the redo command for the Rodin Editor (not in overlay mode)
 */
public class RedoHandler extends AbstractUndoRedoHandler {

	@Override
	protected void doOperation(IUndoContext undoCtx) {
		RODIN_HISTORY.redo(undoCtx);
	}

	@Override
	protected boolean isEnabled(IUndoContext undoCtx) {
		return RODIN_HISTORY.isRedo(undoCtx);
	}

}
