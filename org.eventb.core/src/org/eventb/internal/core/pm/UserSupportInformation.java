/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.core.pm;

import org.eventb.core.pm.IUserSupportInformation;

public class UserSupportInformation implements IUserSupportInformation {

	Object information;
	int priority;
	
	public UserSupportInformation(Object information, int priority) {
		this.information = information;
		this.priority = priority;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.pm.IUserSupportInformation#getInformation()
	 */
	public Object getInformation() {
		return information;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.pm.IUserSupportInformation#getPriority()
	 */
	public int getPriority() {
		return priority;
	}

	@Override
	public String toString() {
		return information.toString() + " (priority " + priority + ")";
	}


}
