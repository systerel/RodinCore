/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - refactored to implement IRodinHistory
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
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IAtomicOperation;
import org.eventb.ui.eventbeditor.IRodinHistory;

public class History implements IRodinHistory {

	private static History singleton;
	private final IOperationHistory history;
	private int limit;
	private final Set<IUndoContext> contexts;
	final String PROPERTY_NAME = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_UNDO_HISTORY_SIZE;

	private History() {
		history = PlatformUI.getWorkbench().getOperationSupport()
				.getOperationHistory();
		limit = getPreferencesLimit();
		contexts = new HashSet<IUndoContext>();
		EditorsUI.getPreferenceStore().addPropertyChangeListener(
				new IPropertyChangeListener() {
					@Override
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

	@Override
	public void addOperation(IAtomicOperation operation) {
		if (operation == null) {
			return;
		}
//		if (!isRodinUndoContext(operation)) {
//			return;
//		}
		contexts.addAll(Arrays.asList(operation.getContexts()));
		try {
			history.execute(operation, null, null);
		} catch (ExecutionException e) {
			UIUtils.showUnexpectedError(e.getCause(),
					"when executing operation:" + operation.getLabel());
		}
		setLimit(operation.getContexts());
	}	
	
//	private boolean isRodinUndoContext(IAtomicOperation operation) {
//		for (IUndoContext ct : operation.getContexts()) {
//			if (!(ct instanceof RodinFileUndoContext)) {
//				return false;
//			}
//		}
//		return true;
//	}

	@Override
	public void redo(IUndoContext context) {
		try {
			history.redo(context, null, null);
		} catch (ExecutionException e) {
			UIUtils.showUnexpectedError(e.getCause(), "when redoing operation:"
					+ context.getLabel());
		}
	}

	@Override
	public void undo(IUndoContext context) {
		try {
			history.undo(context, null, null);
		} catch (ExecutionException e) {
			UIUtils.showUnexpectedError(e.getCause(), "when undoing operation:"
					+ context.getLabel());
		}
	}

	@Override
	public void dispose(IUndoContext context) {
		contexts.remove(context);
		history.dispose(context, true, true, true);
	}

	@Override
	public String getNextUndoLabel(IUndoContext context) {
		final IUndoableOperation op = history.getUndoOperation(context);
		if (op != null && op.canUndo()) {
			return op.getLabel();
		} else {
			return "";
		}
	}

	@Override
	public void setLimit(int limit) {
		this.limit = limit;
		setLimit(contexts.toArray(new IUndoContext[contexts.size()]));
	}

	@Override
	public boolean isUndo(IUndoContext context) {
		return history.canUndo(context);
	}

	@Override
	public boolean isRedo(IUndoContext context) {
		return history.canRedo(context);
	}

	@Override
	public String getNextRedoLabel(IUndoContext context) {
		final IUndoableOperation op = history.getRedoOperation(context);
		if (op != null && op.canRedo()) {
			return op.getLabel();
		} else {
			return "";
		}
	}

	@Override
	public void addOperationHistoryListener(IOperationHistoryListener listener) {
		history.addOperationHistoryListener(listener);
	}
}
