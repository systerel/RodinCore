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

import static org.eventb.ui.manipulation.ElementManipulationFacade.getRodinFileUndoContext;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * Abstract implementation for the undo/redo commands for the Rodin Editor (not
 * in overlay mode)
 */
public abstract class AbstractUndoRedoHandler extends AbstractEditorHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IUndoContext undoCtx = getRodinUndoRedoContext(event);
		doOperation(undoCtx);
		return null;
	}

	@Override
	public boolean isEnabled() {
		final RodinEditor editor = getActiveRodinEditor();
		if (editor == null) {
			return false;
		}
		final IUndoContext undoCtx = getRodinUndoRedoContext(editor);
		return isEnabled(undoCtx);
	}

	/**
	 * Operation implementation which uses the given Rodin undo context.
	 */
	protected abstract void doOperation(IUndoContext undoCtx);

	/**
	 * Tells whether the operation implementation which uses the given Rodin
	 * undo context.
	 */
	protected abstract boolean isEnabled(IUndoContext undoCtx);

	private static IUndoContext getRodinUndoRedoContext(final RodinEditor editor) {
		final ILElement root = editor.getResource().getRoot();
		final IEventBRoot iRoot = (IEventBRoot) root.getElement();
		return getRodinFileUndoContext(iRoot);
	}

	private static IUndoContext getRodinUndoRedoContext(ExecutionEvent event) {
		final RodinEditor editor = getActiveRodinEditor(event);
		return getRodinUndoRedoContext(editor);
	}

}
