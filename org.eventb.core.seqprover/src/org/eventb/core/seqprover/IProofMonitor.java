/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.seqprover;

/**
 * The <code>IProofMonitor</code> interface is implemented by objects that
 * monitor the progress of a proving activity; the methods in this interface are
 * invoked by code that performs the activity.
 * <p>
 * The performer of the activity should regularly call
 * <code>setTask(String)</code> to report about the current task which is
 * performed.
 * </p>
 * <p>
 * A request to cancel an operation can be signaled using the
 * <code>setCanceled</code> method. Long running proof operations are expected
 * to poll the monitor (using <code>isCanceled</code>) periodically and abort
 * at their earliest convenience. Proof operations can however choose to ignore
 * cancellation requests.
 * </p>
 * <p>
 * Since notification is synchronous with the activity itself, the listener
 * should provide a fast and robust implementation. If the handling of
 * notifications would involve blocking operations, or operations which might
 * throw uncaught exceptions, the notifications should be queued, and the actual
 * processing deferred (or perhaps delegated to a separate thread).
 * </p>
 * <p>
 * This interface is quite similar to the
 * {@link org.eclipse.core.runtime.IProgressMonitor} provided by Eclipse. The
 * main difference is that progress is not tracked by this class, as it would be
 * meaningless in a proof activity.
 * </p>
 * <p>
 * Clients may implement this interface.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public interface IProofMonitor {

	/**
	 * Returns whether cancellation of the current operation has been requested.
	 * Long-running operations should poll to see if cancellation has been
	 * requested.
	 * 
	 * @return <code>true</code> iff cancellation has been requested
	 * @see #setCanceled(boolean)
	 */
	boolean isCanceled();

	/**
	 * Sets the cancel state to the given value.
	 * 
	 * @param value
	 *            <code>true</code> indicates that cancellation has been
	 *            requested (but not necessarily acknowledged);
	 *            <code>false</code> clears this flag
	 * @see #isCanceled()
	 */
	void setCanceled(boolean value);

	/**
	 * Notifies that a task is beginning.
	 * 
	 * @param name
	 *            the name of the task
	 */
	void setTask(String name);

}
