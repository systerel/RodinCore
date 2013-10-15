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
package fr.systerel.editor.internal.editors;

import static fr.systerel.editor.internal.editors.RodinEditorUtils.RODIN_HISTORY;
import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_UNDO_HISTORY_SIZE;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.ui.editors.text.EditorsUI;

/**
 * Specific manager for the UNDO/REDO actions in the overlay editor. This
 * manager is used to filter out the Rodin database context operations and
 * manage the remaining textual operations.
 */
public class OverlayEditorUndoManager extends TextViewerUndoManager {

	private RodinEditor editor;

	public OverlayEditorUndoManager(RodinEditor editor) {
		super(getPreferencesLimit());
		this.editor = editor;
	}
	
	private static int getPreferencesLimit() {
		return EditorsUI.getPreferenceStore().getInt(EDITOR_UNDO_HISTORY_SIZE);
	}

	@Override
	public IUndoContext getUndoContext() {
		return null;
	}

	/**
	 * Tells whether overlay text edition can be undone.
	 */
	@Override
	public boolean undoable() {
		return RODIN_HISTORY.isUndo(getOverlayUndoContext());
	}

	/**
	 * Tells whether overlay text edition can be re-done.
	 */
	@Override
	public boolean redoable() {
		return RODIN_HISTORY.isRedo(getOverlayUndoContext());
	}

	/**
	 * Undoes the last textual modification in the overlay.
	 */
	@Override
	public void undo() {
		RODIN_HISTORY.undo(getOverlayUndoContext());
	}

	/**
	 * Re-does the last textual modification in the overlay.
	 */
	@Override
	public void redo() {
		RODIN_HISTORY.redo(getOverlayUndoContext());
	}

	/**
	 * Gives a fresh undo context from the document managed by the Rodin editor.
	 *
	 * @return an object undo context which encapsulates the document handled by
	 *         the Rodin editor
	 */
	private ObjectUndoContext getOverlayUndoContext() {
		return new ObjectUndoContext(editor.getDocument());
	}

}
