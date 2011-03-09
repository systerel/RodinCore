/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.core.pm;

/**
 * @author htson
 *         <p>
 *         An user support manager changed listener receives notification of
 *         changes to user supports maintained by the system.
 *         <p>
 *         This interface may be implemented by clients.
 *         </p>
 * @since 1.0
 */
public interface IUserSupportManagerChangedListener {

	/**
	 * Notifies that one or more user supports have changed. The specific
	 * details of the change are described by the given delta.
	 * 
	 * @param delta
	 *            the delta describes the changes.
	 */
	public void userSupportManagerChanged(IUserSupportManagerDelta delta);

}
