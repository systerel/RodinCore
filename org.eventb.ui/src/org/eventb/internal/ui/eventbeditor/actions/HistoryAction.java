/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.eventbeditor.IEventBEditor;

/**
 * Common protocol for classes that manipulate the history within an event-B
 * editor.
 */
public abstract class HistoryAction extends Action implements
		IOperationHistoryListener {

	public static class Undo extends HistoryAction {

		public Undo(IWorkbenchWindow workbenchWindow) {
			super(workbenchWindow);
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
			super(workbenchWindow);
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
	private final IWorkbenchWindow workbenchWindow;

	// Short-cut for accessing the history
	protected static final History history = History.getInstance();

	public HistoryAction(IWorkbenchWindow workbenchWindow) {
		super();
		this.workbenchWindow = workbenchWindow;
		history.addOperationHistoryListener(this);
	}

	private IEditorPart getActiveEditor() {
		final IWorkbenchPage page = workbenchWindow.getActivePage();
		if (page == null)
			return null;
		return page.getActiveEditor();
	}

	protected IUndoContext getUndoContext() {
		final IEditorPart editor = getActiveEditor();
		if (!(editor instanceof IEventBEditor<?>))
			return null;
		return OperationFactory.getContext(((IEventBEditor<?>) editor)
				.getRodinInput());
	}

	@Override
	final public void run() {
		final IUndoContext context = getUndoContext();
		if (context != null) {
			doRun(context);
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
