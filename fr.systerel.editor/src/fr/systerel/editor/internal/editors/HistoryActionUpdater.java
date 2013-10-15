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

import static fr.systerel.editor.internal.editors.RodinEditorUtils.getPlatformHistory;
import static org.eclipse.ui.texteditor.ITextEditorActionConstants.REDO;
import static org.eclipse.ui.texteditor.ITextEditorActionConstants.UNDO;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.commands.ICommandService;

/**
 * A delegate which updates the undo and redo actions when the main platform
 * history gets modified. This ensures that the state of undo and redo actions
 * is consistent with the contents of the history.
 */
public class HistoryActionUpdater implements IOperationHistoryListener {

	private final RodinEditor editor;

	public HistoryActionUpdater(RodinEditor editor) {
		this.editor = editor;
	}

	/**
	 * Updates UNDO and REDO actions only.
	 */
	private void updateUndoRedoActions() {
		editor.updateAction(UNDO);
		editor.updateAction(REDO);
	}

	public void startListening() {
		getPlatformHistory().addOperationHistoryListener(this);
	}

	public void finishListening() {
		getPlatformHistory().removeOperationHistoryListener(this);
	}

	@Override
	public void historyNotification(OperationHistoryEvent event) {
		final int eventType = event.getEventType();
		switch (eventType) {
		case OperationHistoryEvent.OPERATION_ADDED:
		case OperationHistoryEvent.OPERATION_REMOVED:
		case OperationHistoryEvent.UNDONE:
		case OperationHistoryEvent.REDONE:
			updateUndoRedoActions();
			updateHandlersEnablement();
		}
	}

	private void updateHandlersEnablement() {
		refreshHandlerEnablement(IWorkbenchCommandConstants.EDIT_UNDO);
		refreshHandlerEnablement(IWorkbenchCommandConstants.EDIT_REDO);
	}

	private void refreshHandlerEnablement(String commandId) {
		final ICommandService service = (ICommandService) editor
				.getEditorSite().getService(ICommandService.class);
		final Command undoCommand = service.getCommand(commandId);
		undoCommand.setEnabled(RodinEditorUtils
				.getDefaultEvaluationContext(editor));
	}

}
