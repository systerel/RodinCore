/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.registry.tests;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;

/**
 * Stub implementation of configuration elements, used for testing the extension
 * parser. Only the methods needed for testing are implemented, the others throw
 * an exception.
 * 
 * @author Laurent Voisin
 * @see ExtensionParserTests
 */
public class ConfigurationElementStub implements IConfigurationElement {

	private static final String PLUGIN_ID = "some.stub.plugin";
	private final String elementName;
	private final Map<String, String> attributes = new HashMap<String, String>();

	public ConfigurationElementStub(String elementName, String id,
			String... attrs) {
		this.elementName = elementName;
		attributes.put("id", id);
		assert attrs.length % 2 == 0;
		for (int i = 0; i < attrs.length; i += 2) {
			attributes.put(attrs[i], attrs[i + 1]);
		}
	}

	@Override
	public Object createExecutableExtension(String propertyName)
			throws CoreException {
		throw notImplemented();
	}

	@Override
	public String getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public String getAttribute(String attrName, String locale) {
		throw notImplemented();
	}

	@Override
	@Deprecated
	public String getAttributeAsIs(String name) {
		throw notImplemented();
	}

	@Override
	public String[] getAttributeNames() {
		throw notImplemented();
	}

	@Override
	public IConfigurationElement[] getChildren() {
		throw notImplemented();
	}

	@Override
	public IConfigurationElement[] getChildren(String name) {
		throw notImplemented();
	}

	@Override
	public IExtension getDeclaringExtension() {
		throw notImplemented();
	}

	@Override
	public String getName() {
		return elementName;
	}

	@Override
	public Object getParent() {
		throw notImplemented();
	}

	@Override
	public String getValue() {
		throw notImplemented();
	}

	@Override
	public String getValue(String locale) {
		throw notImplemented();
	}

	@Override
	@Deprecated
	public String getValueAsIs() {
		throw notImplemented();
	}

	@Override
	@Deprecated
	public String getNamespace() {
		throw notImplemented();
	}

	@Override
	public String getNamespaceIdentifier() {
		return PLUGIN_ID;
	}

	@Override
	public IContributor getContributor() {
		return new IContributor() {
			@Override
			public String getName() {
				return PLUGIN_ID;
			}
		};
	}

	@Override
	public boolean isValid() {
		return true;
	}

	private RuntimeException notImplemented() {
		return new UnsupportedOperationException();
	}
}
