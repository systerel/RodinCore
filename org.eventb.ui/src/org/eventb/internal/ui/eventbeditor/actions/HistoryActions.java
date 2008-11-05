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

import java.util.HashMap;

import org.eclipse.ui.IWorkbenchWindow;

public enum HistoryActions {
	INSTANCE;

	private final HashMap<IWorkbenchWindow, UndoAction> undoActions = new HashMap<IWorkbenchWindow, UndoAction>();
	private final HashMap<IWorkbenchWindow, RedoAction> redoActions = new HashMap<IWorkbenchWindow, RedoAction>();

	/**
	 * return an instance of UndoAction.
	 * <p>
	 * For all wb, getUndoAction(wb) == getUndoAction(wb)
	 * 
	 */
	public UndoAction getUndoAction(IWorkbenchWindow wb) {
		if (undoActions.containsKey(wb))
			return undoActions.get(wb);

		final UndoAction undoAction = new UndoAction(wb);
		undoActions.put(wb, undoAction);
		return undoAction;
	}

	/**
	 * return an instance of RedoAction.
	 * <p>
	 * For all wb, getRedoAction(wb) == getRedoAction(wb)
	 * 
	 */
	public RedoAction getRedoAction(IWorkbenchWindow wb) {
		if (redoActions.containsKey(wb))
			return redoActions.get(wb);

		final RedoAction redoAction = new RedoAction(wb);
		redoActions.put(wb, redoAction);
		return redoAction;
	}
}