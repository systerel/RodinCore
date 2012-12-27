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
package org.eventb.internal.core.tool;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class BasicDesc {

	public static class ModuleLoadingException extends Exception {

		private static final long serialVersionUID = 8275960586900622227L;

		public ModuleLoadingException(Throwable e) {
			super(e);
		}
	}

	// Fully qualified name of the plugin that provides this module
	private final String bundleName;

	// Configuration element describing this module
	// stored here for debugging purposes
	@SuppressWarnings("unused")
	private final IConfigurationElement configElement;

	// Human-readable name of this module
	private final String name;

	// Unique identifier of this module
	private final String id;
	
	public BasicDesc(IConfigurationElement configElement) throws ModuleLoadingException {
		final String namespaceIdentifier = getNamespaceIdentifier(configElement);
		this.bundleName = namespaceIdentifier;
		this.configElement = configElement;
		this.name = getAttribute(configElement, "name");
		this.id = namespaceIdentifier + "." + getAttribute(configElement, "id");
	}

	protected String getAttribute(IConfigurationElement confElem,
			String attribute) throws ModuleLoadingException {
		try {
			return confElem.getAttribute(attribute);
		} catch (Throwable e) {
			throw new ModuleLoadingException(e);
		}
	}

	protected IConfigurationElement[] getChildren(
			IConfigurationElement confElem, String childName)
			throws ModuleLoadingException {
		try {
			return confElem.getChildren(childName);
		} catch (Throwable e) {
			throw new ModuleLoadingException(e);
		}
	}

	private String getNamespaceIdentifier(IConfigurationElement confElem) throws ModuleLoadingException {
		try {
			return confElem.getNamespaceIdentifier();
		} catch (Throwable e) {
			throw new ModuleDesc.ModuleLoadingException(e);
		}
	}

	public String getBundleName() {
		return bundleName;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		return id.equals(((BasicDesc) obj).id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}
