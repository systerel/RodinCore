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

import static org.rodinp.core.tests.AbstractRodinDBTests.PLUGIN_ID;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private static final String FAKE_NAMESPACE = PLUGIN_ID;
	private static final String FAKE_CONTRIBUTOR_NAME //
	= FAKE_NAMESPACE + ".contributor";
	private static final IContributor FAKE_CONTRIBUTOR //
	= new FakeContributor(FAKE_CONTRIBUTOR_NAME);

	//private final RuntimeException error = new UnsupportedOperationException();

	/**
	 * The name of the configuration element which would denote the XML tag of
	 * the element if this element was created from an XML tree
	 */
	private final String name;

	private final IContributor contributor;

	private IConfigurationElement[] children;

	private Map<String, String> attributes;

	/**
	 * Builds a fake configuration element from the given parameters.
	 * <p>
	 * The given <code>name</code> would denote the XML tag of the configuration
	 * element.<br>
	 * The attribute definition strings <code>attributesStrs</code> are given as
	 * an array of strings of the following shape : attributeId='value'.
	 * </p>
	 * 
	 * @param name
	 *            the name of the configuration element
	 * @param attributeStrs
	 *            the array of attribute definitions
	 * @param children
	 *            the children configuration elements
	 */
	public FakeConfigurationElement(String name, String[] attributeStrs,
			IConfigurationElement[] children) {
		this.name = name;
		this.contributor = FAKE_CONTRIBUTOR;
		this.attributes = new HashMap<String, String>();
		fillAttributesMap(attributeStrs);
		this.children = children;
	}
	
	private void fillAttributesMap(String[] attributeMapping) {
		for (String attr : attributeMapping) {
			final String[] def = attr.split("=");
			attributes.put(def[0], getAttributeValue(def[1]));
		}
	}

	private String getAttributeValue(String string) {
		final Pattern p = Pattern.compile("\\s*'(.+)'\\s*");
		final Matcher m = p.matcher(string);
		if (m.matches())
			return m.group(1);
		return null;
	}

	@Override
	public Object createExecutableExtension(String propertyName)
			throws CoreException {
		throw getError();
	}

	@Override
	public String getAttribute(String name)
			throws InvalidRegistryObjectException {
		return attributes.get(name);
	}

	@Override
	public String getAttribute(String attrName, String locale)
			throws InvalidRegistryObjectException {
		throw getError();
	}

	@Override
	public String getAttributeAsIs(String name)
			throws InvalidRegistryObjectException {
		throw getError();
	}

	@Override
	public String[] getAttributeNames() throws InvalidRegistryObjectException {
		throw getError();
	}

	@Override
	public IConfigurationElement[] getChildren()
			throws InvalidRegistryObjectException {
		return children;
	}

	@Override
	public IConfigurationElement[] getChildren(String name)
			throws InvalidRegistryObjectException {
		throw getError();
	}

	@Override
	public IExtension getDeclaringExtension()
			throws InvalidRegistryObjectException {
		throw getError();
	}

	@Override
	public String getName() throws InvalidRegistryObjectException {
		return name;
	}

	@Override
	public Object getParent() throws InvalidRegistryObjectException {
		throw getError();
	}

	@Override
	public String getValue() throws InvalidRegistryObjectException {
		throw getError();
	}

	@Override
	public String getValue(String locale) throws InvalidRegistryObjectException {
		throw getError();
	}

	@Override
	public String getValueAsIs() throws InvalidRegistryObjectException {
		throw getError();
	}

	@Override
	public String getNamespace() throws InvalidRegistryObjectException {
		throw getError();
	}

	@Override
	public String getNamespaceIdentifier()
			throws InvalidRegistryObjectException {
		return FAKE_NAMESPACE;
	}

	@Override
	public IContributor getContributor() throws InvalidRegistryObjectException {
		return contributor;
	}

	@Override
	public boolean isValid() {
		throw getError();
	}
	
	private RuntimeException getError() {
		return new UnsupportedOperationException();
	}

	private static class FakeContributor implements IContributor {

		/** The name of the contributor **/
		private final String name;

		public FakeContributor(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

	}

}
