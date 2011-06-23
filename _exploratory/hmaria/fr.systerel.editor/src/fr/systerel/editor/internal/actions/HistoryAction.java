/*******************************************************************************
 * Copyright (c) 2008-2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.actions;

import static org.eclipse.ui.actions.ActionFactory.REDO;
import static org.eclipse.ui.actions.ActionFactory.UNDO;

import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import fr.systerel.editor.EditorPlugin;
import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.operations.History;
import fr.systerel.editor.internal.operations.OperationFactory;

/**
 * Common protocol for classes that manipulate the history within an event-B
 * editor.
 */
public abstract class HistoryAction extends Action implements
		IOperationHistoryListener {

	public static class Undo extends HistoryAction {

		public Undo(IWorkbenchWindow workbenchWindow) {
			super(UNDO.getId(), UNDO.getId()) ;
			this.workbenchWindow = workbenchWindow;
			history.addOperationHistoryListener(this);
		}

		@Override
		public void doRun(IUndoContext context) {
			history.undo(context);
		}

		@Override
		public String getActionName() {
			return "Undo";
		}

		@Override
		public String getLabel() {
			final IUndoContext context = getUndoContext();
			if (context != null) {
				return history.getNextUndoLabel(context);
			}
			return "";
		}

		@Override
		protected boolean historyIsEnabled() {
			final IUndoContext context = getUndoContext();
			if (context == null)
				return false;
			return history.isUndo(context);
		}

	}

	public static class Redo extends HistoryAction {

		public Redo(IWorkbenchWindow workbenchWindow) {
			super(REDO.getId(), REDO.getId());
			this.workbenchWindow = workbenchWindow;
			history.addOperationHistoryListener(this);
		}

		@Override
		public void doRun(IUndoContext context) {
			history.redo(context);
		}

		@Override
		public String getActionName() {
			return "Redo";
		}

		@Override
		public String getLabel() {
			final IUndoContext context = getUndoContext();
			if (context != null) {
				return history.getNextRedoLabel(context);
			}
			return "";
		}

		@Override
		protected boolean historyIsEnabled() {
			final IUndoContext context = getUndoContext();
			if (context == null)
				return false;

			return history.isRedo(context);
		}
	}

	// The workbench window where to look for an open editor
	protected IWorkbenchWindow workbenchWindow;

	// Short-cut for accessing the history
	protected static final History history = History.getInstance();

	public HistoryAction(String id, String text) {
		super(id);
	}

	private IEditorPart getActiveEditor() {
		final IWorkbenchPage page = workbenchWindow.getActivePage();
		if (page == null)
			return null;
		return page.getActiveEditor();
	}

	public IUndoContext getUndoContext() {
		final IEditorPart editor = getActiveEditor();
		if (!(editor instanceof RodinEditor))
			return null;
		final RodinEditor rodinEditor = (RodinEditor) editor;
		if (rodinEditor.isOverlayActive()) {
			return new ObjectUndoContext(rodinEditor.getDocument());
		}
		return OperationFactory.getRodinFileUndoContext(rodinEditor
				.getInputRoot());
	}

	@Override
	final public void run() {
		final IUndoContext context = getUndoContext();
		if (context != null) {
			abortEditionBeforeAction();
			doRun(context);
			//if (context instanceof RodinFileUndoContext)
				refreshEditor();
		}
	}

	@Override
	public void historyNotification(OperationHistoryEvent event) {
		refresh();
	}
	
	public void abortEditionBeforeAction(){
		final IEditorPart editor = getActiveEditor();
		if (editor instanceof RodinEditor) {
			((RodinEditor)editor).abortEditing();
		}
	}

	public void refresh() {
		final String text = getActionName() + " " + getLabel();
		if (!text.equals(getText())) {
			setText(text);
		}
		setEnabled(historyIsEnabled());
	}
	
	public void refreshEditor() {
		final IWorkbenchPage activePage = EditorPlugin.getActivePage();
		if (activePage == null) {
			return;
		}
		final IEditorPart activeEditor = activePage.getActiveEditor();
		if (!(activeEditor instanceof RodinEditor)) {
			return;
		}
		((RodinEditor)activeEditor).resync(null);
	}

	protected abstract void doRun(IUndoContext context);

	protected abstract String getActionName();

	protected abstract String getLabel();

	protected abstract boolean historyIsEnabled();
}
