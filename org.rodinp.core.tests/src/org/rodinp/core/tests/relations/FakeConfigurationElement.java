/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests.relations;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;

/**
 * Fake implementation of a configuration element for testing purposes.
 * 
 * @author Laurent Voisin
 */
public class FakeConfigurationElement implements IConfigurationElement {

	private final RuntimeException error = new UnsupportedOperationException();

	// TODO implement only the operations needed for unit testing of the parser

	@Override
	public Object createExecutableExtension(String propertyName)
			throws CoreException {
		throw error;
	}

	@Override
	public String getAttribute(String name)
			throws InvalidRegistryObjectException {
		throw error;
	}

	@Override
	public String getAttribute(String attrName, String locale)
			throws InvalidRegistryObjectException {
		throw error;
	}

	@Override
	public String getAttributeAsIs(String name)
			throws InvalidRegistryObjectException {
		throw error;
	}

	@Override
	public String[] getAttributeNames() throws InvalidRegistryObjectException {
		throw error;
	}

	@Override
	public IConfigurationElement[] getChildren()
			throws InvalidRegistryObjectException {
		throw error;
	}

	@Override
	public IConfigurationElement[] getChildren(String name)
			throws InvalidRegistryObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IExtension getDeclaringExtension()
			throws InvalidRegistryObjectException {
		throw error;
	}

	@Override
	public String getName() throws InvalidRegistryObjectException {
		throw error;
	}

	@Override
	public Object getParent() throws InvalidRegistryObjectException {
		throw error;
	}

	@Override
	public String getValue() throws InvalidRegistryObjectException {
		throw error;
	}

	@Override
	public String getValue(String locale) throws InvalidRegistryObjectException {
		throw error;
	}

	@Override
	public String getValueAsIs() throws InvalidRegistryObjectException {
		throw error;
	}

	@Override
	public String getNamespace() throws InvalidRegistryObjectException {
		throw error;
	}

	@Override
	public String getNamespaceIdentifier()
			throws InvalidRegistryObjectException {
		throw error;
	}

	@Override
	public IContributor getContributor() throws InvalidRegistryObjectException {
		throw error;
	}

	@Override
	public boolean isValid() {
		throw error;
	}

}
