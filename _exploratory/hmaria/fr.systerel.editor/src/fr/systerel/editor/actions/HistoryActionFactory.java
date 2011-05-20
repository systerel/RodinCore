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
package fr.systerel.editor.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

public enum HistoryActionFactory {

	// Singleton instance
	INSTANCE;

	static abstract class ActionCache<T extends HistoryAction> {

		private final Map<IWorkbenchWindow, T> cache = new HashMap<IWorkbenchWindow, T>();

		public T getOrCreateAction(IWorkbenchWindow window) {
			T action = cache.get(window);
			if (action == null) {
				action = createAction(window);
				cache.put(window, action);
			}
			return action;
		}

		protected abstract T createAction(IWorkbenchWindow window);

	}

	static class UndoActionCache extends ActionCache<HistoryAction.Undo> {

		@Override
		protected HistoryAction.Undo createAction(IWorkbenchWindow window) {
			final HistoryAction.Undo undo = new HistoryAction.Undo(window);
			undo.setActionDefinitionId("org.eclipse.ui.edit.undo");
			return undo;
		}

	}

	static class RedoActionCache extends ActionCache<HistoryAction.Redo> {

		@Override
		protected HistoryAction.Redo createAction(IWorkbenchWindow window) {
			final HistoryAction.Redo redo = new HistoryAction.Redo(window);
			redo.setActionDefinitionId("org.eclipse.ui.edit.redo");
			return redo;
		}

	}

	private final UndoActionCache undos = new UndoActionCache();

	private final RedoActionCache redos = new RedoActionCache();

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