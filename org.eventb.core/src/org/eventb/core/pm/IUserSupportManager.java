/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.pm;

import java.util.Collection;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *<p>
 *
 * Represents the user support manager. Since there
 * is only one manager, it is commonly referred to as <em>the</em>
 * user support manager. The manager is a singleton instance and is created needs to be created before
 * it can be navigated or manipulated. The Rodin database element has no parent
 * (it is the root of the Rodin element hierarchy). Its children are
 * <code>IRodinProject</code>s.
 * <p>
 * This interface provides methods for performing copy, move, rename, and delete
 * operations on multiple Rodin elements.
 * </p>
 * <p>
 * An instance of one of these handles can be created via
 * <code>RodinCore.valueOf(workspace.getRoot())</code>.
 * </p>
 * 
 * @see RodinCore#valueOf(IWorkspaceRoot)
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IUserSupportManager {

	/**
	 * Create a new User Support with no input.
	 * <p>
	 * 
	 * @return a new instance of User Support
	 */
	public abstract IUserSupport newUserSupport();

	/**
	 * Return the list of User Support managed by the manager
	 * <p>
	 * 
	 * @return a collection of User Supports managed by the manager
	 */
	public abstract Collection<IUserSupport> getUserSupports();

	/**
	 * Add a listener to the manager
	 * <p>
	 * 
	 * @param listener
	 *            a USManager listener
	 */
	public abstract void addChangeListener(IUserSupportManagerChangedListener listener);

	/**
	 * Remove a listener from the manager
	 * <p>
	 * 
	 * @param listener
	 *            an existing USManager listener
	 */
	public abstract void removeChangeListener(IUserSupportManagerChangedListener listener);

	/**
	 * Run the give action as an atomic User Support Manager operation.
	 * <p>
	 * After running a method that changes user supportss,
	 * registered listeners receive after-the-fact notification of
	 * what just transpired, in the form of an user support manager changed delta.
	 * This method allows clients to call a number of
	 * methods that changes user supports and only have user support manager
	 * changed notifications reported at the end of the entire
	 * batch.
	 * </p>
	 * <p>
	 * If this method is called outside the dynamic scope of another such
	 * call, this method runs the action and then reports a single
	 * user support manager changed notification describing the net effect of all changes
	 * done to user supports by the action.
	 * </p>
	 * <p>
	 * If this method is called in the dynamic scope of another such
	 * call, this method simply runs the action.
	 * </p>
	 * @param op the operation to perform
	 * @throws RodinDBException if the operation failed.
	 */
	public void run(Runnable op) throws RodinDBException;

	/**
	 * Sets whether or not to consider hidden hypotheses in search results.
	 * 
	 * @param value
	 *            <code>true</code> to include hidden hypotheses in search
	 *            results, <code>false</code> otherwise
	 * @since 1.1
	 */
	void setConsiderHiddenHypotheses(boolean value);

	/**
	 * Returns whether or not to consider hidden hypotheses in search results.
	 * 
	 * @return <code>true</code> if hidden hypotheses are included in search
	 *         results, <code>false</code> otherwise
	 * @since 3.0
	 */
	boolean isConsiderHiddenHypotheses();

}
