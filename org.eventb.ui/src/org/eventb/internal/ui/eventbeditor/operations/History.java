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
package org.eventb.internal.ui.eventbeditor.operations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eventb.internal.ui.UIUtils;

public class History {

	private static History singleton;
	private final IOperationHistory history;
	private int limit;
	private final Set<IUndoContext> contexts;
	final String PROPERTY_NAME = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_UNDO_HISTORY_SIZE;

	private History() {
		history = OperationHistoryFactory.getOperationHistory();
		limit = getPreferencesLimit();
		contexts = new HashSet<IUndoContext>();
		EditorsUI.getPreferenceStore().addPropertyChangeListener(
				new IPropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent event) {
						if (PROPERTY_NAME.equals(event.getProperty())) {
							setLimit(getPreferencesLimit());
						}
					}
				});
	}

	int getPreferencesLimit() {
		return EditorsUI.getPreferenceStore().getInt(PROPERTY_NAME);
	}

	public static synchronized History getInstance() {
		if (singleton == null) {
			singleton = new History();
		}
		return singleton;
	}

	private void setLimit(IUndoContext[] contexts) {
		for (IUndoContext context : contexts) {
			history.setLimit(context, limit);
		}
	}

	public void addOperation(AtomicOperation operation) {
		try {
			if (operation != null) {
				contexts.addAll(Arrays.asList(operation.getContexts()));
				history.execute(operation, null, null);
				setLimit(operation.getContexts());
			}
		} catch (ExecutionException e) {
			UIUtils.log(e.getCause(), "when executing an operation");
		}
	}

	public void redo(IUndoContext context) {
		try {
			history.redo(context, null, null);
		} catch (ExecutionException e) {
			UIUtils.log(e.getCause(), "when redoing an operation");
		}
	}

	public void undo(IUndoContext context) {
		try {
			history.undo(context, null, null);
		} catch (ExecutionException e) {
			UIUtils.log(e.getCause(), "when undoing an operation");
		}
	}

	public void dispose(IUndoContext context) {
		contexts.remove(context);
		history.dispose(context, true, true, true);
	}

	public String getNextUndoLabel(IUndoContext context) {
		final IUndoableOperation op = history.getUndoOperation(context);
		if (op != null && op.canUndo()) {
			return op.getLabel();
		} else {
			return "";
		}
	}

	void setLimit(int limit) {
		this.limit = limit;
		setLimit(contexts.toArray(new IUndoContext[contexts.size()]));
	}

	public boolean isUndo(IUndoContext context) {
		return history.canUndo(context);
	}

	public boolean isRedo(IUndoContext context) {
		return history.canRedo(context);
	}

	public String getNextRedoLabel(IUndoContext context) {
		final IUndoableOperation op = history.getRedoOperation(context);
		if (op != null && op.canRedo()) {
			return op.getLabel();
		} else {
			return "";
		}
	}

	public void addOperationHistoryListener(IOperationHistoryListener listener) {
		history.addOperationHistoryListener(listener);
	}
}
