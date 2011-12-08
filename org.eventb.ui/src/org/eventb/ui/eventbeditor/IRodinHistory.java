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
package org.eventb.ui.eventbeditor;

import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;

/**
 * @since 2.4
 */
public interface IRodinHistory {

	public void addOperation(IAtomicOperation operation);

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