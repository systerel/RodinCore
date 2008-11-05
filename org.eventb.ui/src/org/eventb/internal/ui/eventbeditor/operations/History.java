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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eventb.internal.ui.UIUtils;

public class History {

	private static History singleton;
	final private IOperationHistory history;

	private History() {
		history = OperationHistoryFactory.getOperationHistory();
	}

	public static synchronized History getInstance() {
		if (singleton == null) {
			singleton = new History();
		}
		return singleton;
	}

	public void addOperation(AtomicOperation operation) {
		try {
			if (operation != null) {
				history.execute(operation, null, null);
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
		history.dispose(context, true, true, true);
	}
}
