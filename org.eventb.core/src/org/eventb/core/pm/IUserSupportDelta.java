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


/**
 * @author htson
 *         <p>
 *         A User Support delta describes changes in an user support between two
 *         discrete points in time. Given a delta, clients can access the user
 *         support that has changed, and any proof states belong to this user
 *         support that have changed.
 *         <p>
 *         Deltas have a different status depending on the kind of change they
 *         represent. The list below summarizes each status (as returned by
 *         <code>getKind</code>) and its meaning (see individual constants
 *         for a more detailled description):
 *         <ul>
 *         <li><code>ADDED</code> - The user support described by the delta
 *         has been added.</li>
 *         <li><code>REMOVED</code> - The user support described by the delta
 *         has been removed.</li>
 *         <li><code>CHANGED</code> - The user support described by the delta
 *         has been changed in some way. Specification of the type of change is
 *         provided by <code>getFlags</code> which returns the following
 *         values:
 *         <ul>
 *         <li><code>F_CURRENT</code> - The current proof obligation of the
 *         user support has been changed.</li>
 *         <li><code>F_STATE</code> - A proof state of the user support has
 *         changed in some way.</li>
 *         <li><code>F_INFORMATION</code> - The information about the user support has
 *         changed in some way.</li>
 *         </ul>
 *         </li>
 *         </ul>
 *         </p>
 *         <p>
 *         <code>IUserSupportDelta</code> object are not valid outside the
 *         dynamic scope of the notification.
 *         </p>
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IUserSupportDelta {

	/**
	 * Status constant indicating that the user support has been added. Note
	 * that an added Rodin element delta has no proof state delta, as they are
	 * all implicitely added.
	 */
	public static final int ADDED = 1;

	/**
	 * Status constant indicating that the user support has been removed. Note
	 * that an added Rodin element delta has no proof state delta, as they are
	 * all implicitely removed.
	 */
	public static final int REMOVED = 2;

	/**
	 * Status constant indicating that the element has been changed, as
	 * described by the change flags.
	 * 
	 * @see #getFlags()
	 */
	public static final int CHANGED = 4;

	/**
	 * Change flag indicating that the current proof obligation of the user
	 * support has been changed.
	 */
	public static final int F_CURRENT = 0x00001;

	/**
	 * Change flag indicating that proof state of the user support has changed
	 * in some way.
	 */
	public static final int F_STATE = 0x00002;

	/**
	 * Change flag indicating that information about the user support has changed
	 * in some way.
	 */
	public static final int F_INFORMATION = 0x00004;
	
	/**
	 * Returns the user support that this delta describes a change to.
	 * <p>
	 * 
	 * @return the user support that this delta describes a change to
	 */
	public IUserSupport getUserSupport();

	/**
	 * Returns the kind of this delta - one of <code>ADDED</code>,
	 * <code>REMOVED</code>, or <code>CHANGED</code>.
	 * 
	 * @return the kind of this delta
	 */
	public int getKind();

	/**
	 * Returns flags that describe how an user support has changed. 
	 * Such flags should be tested using the <code>&</code> operand. For example:
	 * <pre>
	 * if ((delta.getFlags() & IUserSupportDelta.F_CURRENT) != 0) {
	 * 	// the delta indicates the current proof obligation has changed
	 * }
	 * </pre>
	 *
	 * @return flags that describe how an user support has changed
	 */
	public int getFlags();

	/**
	 * Returns deltas for the proof states that have been added.
	 * <p>
	 * 
	 * @return deltas for the proof states that have been added
	 */
	public IProofStateDelta[] getAddedProofStates();

	/**
	 * Returns deltas for the proof states that have been removed.
	 * <p>
	 * 
	 * @return deltas for the proof states that have been removed
	 */
	public IProofStateDelta[] getRemovedProofStates();

	/**
	 * Returns deltas for the proof states that have been changed.
	 * <p>
	 * 
	 * @return deltas for the proof states that have been changed
	 */
	public IProofStateDelta[] getChangedProofStates();

	/**
	 * Returns deltas for the affected (added, removed or changed) proof states.
	 * <p>
	 * 
	 * @return deltas for the affected (added, removed or changed) proof states
	 */
	public IProofStateDelta[] getAffectedProofStates();

	/**
	 * Getting the list of information about the changes in user support
	 * (including the output of the applied tactics, messages about the last
	 * action in the user support, etc.)
	 * 
	 * @return an array of IUserSupportInformation
	 */
	public IUserSupportInformation [] getInformation();

}
