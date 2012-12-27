/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests.version.conf;

import org.eclipse.core.runtime.InvalidRegistryObjectException;

/**
 * @author Stefan Hallerstede
 *
 */
public class AddAttribute extends Operation {

	private final String newId;
	private final String newValue;

	public AddAttribute(String newId, String newValue) {
		this.newId = newId;
		this.newValue = newValue;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getAttribute(java.lang.String)
	 */
	public String getAttribute(String name)
			throws InvalidRegistryObjectException {
		if (name.equals("newId"))
			return newId;
		else if (name.equals("newValue"))
			return newValue;
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getName()
	 */
	public String getName() throws InvalidRegistryObjectException {
		return "addAttribute";
	}

}
