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
import org.rodinp.core.IInternalElement;

/**
 * This class is subclassed by UndoAction and RedoAction
 * 
 * @see UndoAction
 * @see RedoAction
 */
public abstract class HistoryAction extends Action {

	protected final History history;
	protected final IWorkbenchWindow workbenchWindow;

	public HistoryAction(IWorkbenchWindow workbenchWindow) {
		super();
		this.workbenchWindow = workbenchWindow;
		history = History.getInstance();
	}

	private IEditorPart getActiveEditor() {
		return workbenchWindow.getActivePage().getActiveEditor();
	}

	protected abstract void doRun(IUndoContext context);

	@Override
	final public void run() {
		final IEditorPart editor = getActiveEditor();
		if (!(editor instanceof IEventBEditor<?>))
			return;
		final IInternalElement root = ((IEventBEditor<?>) editor)
				.getRodinInput();
		final IUndoContext context = OperationFactory.getContext(root);
		doRun(context);
	}
}
