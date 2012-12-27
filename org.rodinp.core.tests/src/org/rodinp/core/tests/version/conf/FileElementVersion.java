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
public class FileElementVersion extends BundleConfElement {

	private final String id;
	private final String version;
	
	public FileElementVersion(String bundle, String id, String version) {
		super(bundle);
		this.id = id;
		this.version = version;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getAttribute(java.lang.String)
	 */
	public String getAttribute(String name)
			throws InvalidRegistryObjectException {
		if (name.equals("id"))
			return id;
		else if (name.equals("version"))
			return version;
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getName()
	 */
	public String getName() throws InvalidRegistryObjectException {
		return "fileElementVersion";
	}

}
