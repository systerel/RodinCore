/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.actions;

import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;

import fr.systerel.editor.internal.actions.operations.AtomicOperation;

public interface IRodinHistory {

	public void addOperation(AtomicOperation operation);

	public void redo(IUndoContext context);

	public void undo(IUndoContext context);

	public void dispose(IUndoContext context);

	public String getNextUndoLabel(IUndoContext context);

	public void setLimit(int limit);

	public boolean isUndo(IUndoContext context);

	public boolean isRedo(IUndoContext context);

	public String getNextRedoLabel(IUndoContext context);

	public void addOperationHistoryListener(IOperationHistoryListener listener);

}