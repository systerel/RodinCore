/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
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
	@Override
	public Object getInformation() {
		return information;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.pm.IUserSupportInformation#getPriority()
	 */
	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public String toString() {
		return information.toString() + " (priority " + priority + ")";
	}


}
