/*******************************************************************************
 * Copyright (c) 2009, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;

/**
 * Dummy implementation of {@link IConfigurationElement} for testing purposes.
 * 
 * @author Laurent Voisin
 */
public class MockConfigurationElement implements IConfigurationElement {

	private static final String NAMESPACE = "some.plugin.id";

	private static final IConfigurationElement[] NO_CONFIGURATION_ELEMENTS = new IConfigurationElement[0];

	private static final IContributor CONTRIBUTOR = new IContributor() {
		public String getName() {
			return NAMESPACE;
		}
	};

	private final Map<String, String> attributes = new HashMap<String, String>();

	/**
	 * Creates a dummy configuration element with the attributes specified by
	 * the string arguments, which list pairs of (attribute name, attribute
	 * value).
	 * 
	 * @param strings
	 *            list of alternating attribute names and attribute values
	 */
	public MockConfigurationElement(String... strings) {
		assert strings.length % 2 == 0;
		for (int i = 0; i < strings.length; i += 2) {
			final String attrName = strings[i];
			final String attrValue = strings[i + 1];
			attributes.put(attrName, attrValue);
		}
	}

	public Object createExecutableExtension(String propertyName)
			throws CoreException {
		throw newRuntimeException();
	}

	public String getAttribute(String name) {
		return attributes.get(name);
	}

	public String getAttribute(String attrName, String locale) {
		return getAttribute(attrName);
	}

	public String getAttributeAsIs(String name) {
		return attributes.get(name);
	}

	public String[] getAttributeNames() {
		final Set<String> names = attributes.keySet();
		return names.toArray(new String[names.size()]);
	}

	public IConfigurationElement[] getChildren() {
		return NO_CONFIGURATION_ELEMENTS;
	}

	public IConfigurationElement[] getChildren(String name)
			throws InvalidRegistryObjectException {
		return NO_CONFIGURATION_ELEMENTS;
	}

	public IContributor getContributor() throws InvalidRegistryObjectException {
		return CONTRIBUTOR;
	}

	public IExtension getDeclaringExtension()
			throws InvalidRegistryObjectException {
		throw newRuntimeException();
	}

	public String getName() throws InvalidRegistryObjectException {
		throw newRuntimeException();
	}

	public String getNamespace() throws InvalidRegistryObjectException {
		throw newRuntimeException();
	}

	public String getNamespaceIdentifier() {
		return NAMESPACE;
	}

	public Object getParent() throws InvalidRegistryObjectException {
		throw newRuntimeException();
	}

	public String getValue() throws InvalidRegistryObjectException {
		throw newRuntimeException();
	}

	public String getValue(String locale) {
		return getValue();
	}

	public String getValueAsIs() throws InvalidRegistryObjectException {
		throw newRuntimeException();
	}

	public boolean isValid() {
		return true;
	}

	protected static RuntimeException newRuntimeException() {
		return new IllegalStateException();
	}

}
