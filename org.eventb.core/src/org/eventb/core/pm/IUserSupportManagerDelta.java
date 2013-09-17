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
 *         A User Support Manager delta describes changes in set of User Support
 *         between two discrete points in time. Given a delta, clients can
 *         access the User Support(s) that have changed.
 *         <p>
 *         <code>IUserSupportManagerDelta</code> object are not valid outside
 *         the dynamic scope of the notification.
 *         </p>
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IUserSupportManagerDelta {

	/**
	 * Returns deltas for the added User Support
	 * <p>
	 * 
	 * @return deltas for the added User Support
	 */
	public IUserSupportDelta[] getAddedUserSupports();

	/**
	 * Returns deltas for the removed User Support
	 * <p>
	 * 
	 * @return deltas for the removed User Support
	 */
	public IUserSupportDelta[] getRemovedUserSupports();

	/**
	 * Returns deltas for the changed User Support
	 * <p>
	 * 
	 * @return deltas for the changed User Support
	 */
	public IUserSupportDelta[] getChangedUserSupports();

	/**
	 * Returns deltas for the affected (added, removed, or changed) User Support
	 * <p>
	 * 
	 * @return deltas for the affected (added, removed, or changed) User Support
	 */
	public IUserSupportDelta[] getAffectedUserSupports();

}
