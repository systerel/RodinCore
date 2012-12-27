/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.eventbeditor;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoContext;

/**
 * A simplified version of the {@link IOperationHistory} dedicated to Rodin
 * edition undoable operations.
 * <p>
 * The IRodinHistory tracks a history of rodin model operations that can be
 * undone or redone. Operations are executed and added to the history using the
 * ElementManipulationFacade.
 * </p>
 * This interface shall be used ONLY to redirect UNDO and REDO
 * actions to the RodinHistory.
 * 
 * @since 2.4
 */
public interface IRodinHistory {

	/**
	 * Undoes the most recently executed operation in the given context.
	 * 
	 * @param context
	 *            the context to be searched for the undo operation to be
	 *            executed
	 */
	public void undo(IUndoContext context);

	/**
	 * Returns the label of the operation that will next be undone in the given
	 * undo context.
	 * 
	 * @param context
	 *            the undo context
	 * @return the label of the operation that will next be undone in the given
	 *         undo context or an empty string otherwise
	 */
	public String getNextUndoLabel(IUndoContext context);

	/**
	 * Returns whether there is a valid undoable operation available in the
	 * given context.
	 * 
	 * @param context
	 *            the context to be checked
	 * @return <code>true</code> if there is an undoable operation,
	 *         <code>false</code> otherwise.
	 */
	public boolean isUndo(IUndoContext context);

	/**
	 * Redoes the most recently undone operation in the given context.
	 * 
	 * @param context
	 *            the context to be searched for the redo operation to be
	 *            executed
	 */
	public void redo(IUndoContext context);

	/**
	 * Returns the label of the operation that will next be redone in the given
	 * context.
	 * 
	 * @param context
	 *            the context to be redone
	 * @return the label of the operation that will next be redone in the given
	 *         context or an empty string otherwise
	 */
	public String getNextRedoLabel(IUndoContext context);

	/**
	 * Returns whether there is a valid redoable operation available in the
	 * given context.
	 * 
	 * @param context
	 *            the context to be checked
	 * @return <code>true</code> if there is an redoable operation,
	 *         <code>false</code> otherwise.
	 */
	public boolean isRedo(IUndoContext context);

	/**
	 * Adds the specified listener to the list of operation history listeners
	 * that are notified about changes in the history or operations that are
	 * executed, undone, or redone.
	 * 
	 * @param listener
	 *            the IOperationHistoryListener to be added as a listener. Must
	 *            not be <code>null</code>. If an attempt is made to register an
	 *            instance which is already registered with this instance, this
	 *            method has no effect.
	 * 
	 * @see org.eclipse.core.commands.operations.IOperationHistoryListener
	 * @see org.eclipse.core.commands.operations.OperationHistoryEvent
	 */
	public void addOperationHistoryListener(IOperationHistoryListener listener);

}