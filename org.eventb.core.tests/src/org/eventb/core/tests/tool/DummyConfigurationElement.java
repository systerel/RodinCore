/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.tool;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eventb.core.IContextRoot;

/**
 * This is a dummy implementation of configuration elements.
 * All contents should be ignored in tests!
 * 
 * @author Stefan Hallerstede
 *
 */
public class DummyConfigurationElement implements IConfigurationElement {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#createExecutableExtension(java.lang.String)
	 */
	public Object createExecutableExtension(String propertyName)
			throws CoreException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getAttribute(java.lang.String)
	 */
	public String getAttribute(String name)
			throws InvalidRegistryObjectException {
		if (name.equals("input"))
			return IContextRoot.ELEMENT_TYPE.getId();
		else
			return name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getAttributeAsIs(java.lang.String)
	 */
	public String getAttributeAsIs(String name)
			throws InvalidRegistryObjectException {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getAttributeNames()
	 */
	public String[] getAttributeNames() throws InvalidRegistryObjectException {
		return new String[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getChildren()
	 */
	public IConfigurationElement[] getChildren()
			throws InvalidRegistryObjectException {
		return new IConfigurationElement[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getChildren(java.lang.String)
	 */
	public IConfigurationElement[] getChildren(String name)
			throws InvalidRegistryObjectException {
		return new IConfigurationElement[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getContributor()
	 */
	public IContributor getContributor() throws InvalidRegistryObjectException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getDeclaringExtension()
	 */
	public IExtension getDeclaringExtension()
			throws InvalidRegistryObjectException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getName()
	 */
	public String getName() throws InvalidRegistryObjectException {
		return "name";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getNamespace()
	 */
	public String getNamespace() throws InvalidRegistryObjectException {
		return "org.name.bin";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getNamespaceIdentifier()
	 */
	public String getNamespaceIdentifier()
			throws InvalidRegistryObjectException {
		return "org.name.bin";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getParent()
	 */
	public Object getParent() throws InvalidRegistryObjectException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getValue()
	 */
	public String getValue() throws InvalidRegistryObjectException {
		return "value";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#getValueAsIs()
	 */
	public String getValueAsIs() throws InvalidRegistryObjectException {
		return "value";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IConfigurationElement#isValid()
	 */
	public boolean isValid() {
		return true;
	}

}
