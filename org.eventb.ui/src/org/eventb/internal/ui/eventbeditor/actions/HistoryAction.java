/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.eventbeditor.IEventBEditor;

/**
 * Common protocol for classes that manipulate the history within an event-B
 * editor.
 */
public abstract class HistoryAction extends Action {

	public static class Undo extends HistoryAction {

		public Undo(IWorkbenchWindow workbenchWindow) {
			super(workbenchWindow);
		}

		@Override
		public void doRun(IUndoContext context) {
			history.undo(context);
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

	}

	// The workbench window where to look for an open editor
	private final IWorkbenchWindow workbenchWindow;

	// Short-cut for accessing the history
	protected static final History history = History.getInstance();

	public HistoryAction(IWorkbenchWindow workbenchWindow) {
		super();
		this.workbenchWindow = workbenchWindow;
	}

	private IEditorPart getActiveEditor() {
		return workbenchWindow.getActivePage().getActiveEditor();
	}

	private IUndoContext getUndoContext() {
		final IEditorPart editor = getActiveEditor();
		if (!(editor instanceof IEventBEditor<?>))
			return null;
		return OperationFactory.getContext((IEventBEditor<?>) editor);
	}

	@Override
	final public void run() {
		final IUndoContext context = getUndoContext();
		if (context != null) {
			doRun(context);
		}
	}

	protected abstract void doRun(IUndoContext context);
}
