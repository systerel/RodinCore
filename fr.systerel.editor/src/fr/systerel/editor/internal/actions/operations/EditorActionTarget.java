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
package fr.systerel.editor.internal.actions.operations;

import static fr.systerel.editor.internal.editors.RodinEditorUtils.log;

import java.util.Collections;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.commands.ICommandService;

import fr.systerel.editor.internal.editors.OverlayEditor;
import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.editors.SelectionController;
import fr.systerel.editor.internal.handlers.PasteHandler;

/**
 * Class used to replace legacy text editor target to redirect actions to the
 * appropriate implementations depending on the editor mode (i.e. overlay mode
 * or not).
 *
 * @author Thomas Muller
 */
public class EditorActionTarget implements ITextOperationTarget {

	private static final String[] commands = initializeCommandConstants();
	
	private static String[] initializeCommandConstants() {
		final String[] array = new String[12];
		array[UNDO] = IWorkbenchCommandConstants.EDIT_UNDO;
		array[REDO] = IWorkbenchCommandConstants.EDIT_REDO;
		array[COPY] = IWorkbenchCommandConstants.EDIT_COPY;
		array[PASTE] = IWorkbenchCommandConstants.EDIT_PASTE;
		array[DELETE] = IWorkbenchCommandConstants.EDIT_DELETE;
		array[SELECT_ALL] = IWorkbenchCommandConstants.EDIT_SELECT_ALL;
		return array;
	}

	private final RodinEditor editor;
	
	public EditorActionTarget(RodinEditor editor) {
		this.editor = editor;
	}

	@Override
	public boolean canDoOperation(int operation) {
		if (operation == ITextOperationTarget.SELECT_ALL) {
			return true;
		}
		final OverlayEditor overlayEditor = editor.getOverlayEditor();
		if (overlayEditor != null && overlayEditor.isActive()) {
			switch (operation) {
			case UNDO:
			case REDO:
				return editor.getTextOperationTarget()
						.canDoOperation(operation);
			}
			return overlayEditor.getTextOperationTarget().canDoOperation(
					operation);
		}
		return canDoEditorOperation(operation);
	}

	private boolean canDoEditorOperation(int operation) {
		final StyledText mainText = editor.getStyledText();
		if (mainText == null) {
			return false;
		}
		// if some text is selected we can perfrom the operation at text level
		if (mainText.getSelectionRange().y != 0) {
			return editor.getTextOperationTarget().canDoOperation(operation);
		}
		switch (operation) {
		case ITextOperationTarget.CUT:
			return false;
		case ITextOperationTarget.COPY:
		case ITextOperationTarget.DELETE:
			return isSomeElementSelected();
		case ITextOperationTarget.PASTE:
			return isPastePossible();
		}
		return false;
	}

	private boolean isPastePossible() {
		return PasteHandler.isPastePossible(editor, editor.getCurrentOffset());
	}

	private boolean isSomeElementSelected() {
		final SelectionController ctrller = editor.getSelectionController();
		return ctrller != null && ctrller.hasSelectedElements();
	}

	@Override
	public void doOperation(int operation) {
		if (editor.isOverlayActive()) {
			// undo/redo actions are handled by the source viewer
			switch (operation) {
			case UNDO:
			case REDO:
				editor.getTextOperationTarget().doOperation(operation);
				return;
			}
			// redirecting the action to overlay styledText
			final OverlayEditor overlayEditor = editor.getOverlayEditor();
			overlayEditor.getTextOperationTarget().doOperation(operation);
			return;
		}
		final String commandId = commands[operation];
		executeCommand(commandId);
	}

	private void executeCommand(String commandId) {
		final ICommandService service = (ICommandService) editor.getSite()
				.getService(ICommandService.class);
		final Command command = service.getCommand(commandId);
		try {
			final EvaluationContext context = new EvaluationContext(null,
					Collections.EMPTY_LIST);
			context.addVariable(ISources.ACTIVE_EDITOR_NAME, editor);
			command.executeWithChecks(new ExecutionEvent(command,
					Collections.EMPTY_MAP, null, context));
		} catch (Exception e) {
			log(e, "EditorActionTarget : could not execute command" + commandId);
		}
	}

}
