/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.version.conf;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ConfElement implements IConfigurationElement {
	
	private ConfElement parent;
	
	private final static IConfigurationElement[] NO_CONF = new IConfigurationElement[0];

	public Object createExecutableExtension(String propertyName) throws CoreException {
		return null;
	}

	public String getAttributeAsIs(String name)
			throws InvalidRegistryObjectException {
				return getAttribute(name);
			}

	public String[] getAttributeNames() throws InvalidRegistryObjectException {
		return null;
	}

	public IConfigurationElement[] getChildren() throws InvalidRegistryObjectException {
		return null;
	}

	public IConfigurationElement[] getChildren(String name)
			throws InvalidRegistryObjectException {
				return NO_CONF;
			}

	public IExtension getDeclaringExtension()
			throws InvalidRegistryObjectException {
				return null;
			}

	public String getNamespace() throws InvalidRegistryObjectException {
		return null;
	}

	public String getNamespaceIdentifier()
			throws InvalidRegistryObjectException {
				return null;
			}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getContributor()
	 */
	public IContributor getContributor() throws InvalidRegistryObjectException {
		return parent.getContributor();
	}
	
	public void setParent(ConfElement parent) {
		this.parent = parent;
	}

	public ConfElement getParent() throws InvalidRegistryObjectException {
		return parent;
	}

	public String getValue() throws InvalidRegistryObjectException {
		return null;
	}

	public String getValueAsIs() throws InvalidRegistryObjectException {
		return null;
	}

	public boolean isValid() {
		return true;
	}


}
