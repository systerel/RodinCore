/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
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

import fr.systerel.editor.actions.IRodinHistory;
import fr.systerel.editor.internal.actions.operations.RodinEditorHistory;
import fr.systerel.editor.internal.actions.operations.OperationFactory;
import fr.systerel.editor.internal.editors.RodinEditor;

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
	protected static final IRodinHistory history = RodinEditorHistory.getInstance();

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
			doRun(context);
			//refreshContents(context);
		}
	}

	@Override
	public void historyNotification(OperationHistoryEvent event) {
		refresh();
	}
	
	public void refresh() {
		final String text = getActionName() + " " + getLabel();
		if (!text.equals(getText())) {
			setText(text);
		}
		setEnabled(historyIsEnabled());
	}

	protected abstract void doRun(IUndoContext context);

	protected abstract String getActionName();

	protected abstract String getLabel();

	protected abstract boolean historyIsEnabled();
	
}
