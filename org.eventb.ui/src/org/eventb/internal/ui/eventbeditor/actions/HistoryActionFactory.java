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
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

public enum HistoryActionFactory {

	// Singleton instance
	INSTANCE;

	static abstract class ActionMap<T extends HistoryAction> {

		private final Map<IWorkbenchWindow, T> map = new HashMap<IWorkbenchWindow, T>();

		public T getOrCreateAction(IWorkbenchWindow window) {
			T action = map.get(window);
			if (action == null) {
				action = createAction(window);
				map.put(window, action);
			}
			return action;
		}

		protected abstract T createAction(IWorkbenchWindow window);

	}

	static class UndoActionMap extends ActionMap<HistoryAction.Undo> {

		@Override
		protected HistoryAction.Undo createAction(IWorkbenchWindow window) {
			return new HistoryAction.Undo(window);
		}

	}

	static class RedoActionMap extends ActionMap<HistoryAction.Redo> {

		@Override
		protected HistoryAction.Redo createAction(IWorkbenchWindow window) {
			return new HistoryAction.Redo(window);
		}

	}

	private final UndoActionMap undos = new UndoActionMap();

	private final RedoActionMap redos = new RedoActionMap();

	/**
	 * Returns an Undo action for the given workbench window.
	 */
	public Action getUndoAction(IWorkbenchWindow window) {
		return undos.getOrCreateAction(window);
	}

	/**
	 * Returns a Redo action for the given workbench window.
	 */
	public Action getRedoAction(IWorkbenchWindow window) {
		return redos.getOrCreateAction(window);
	}

}