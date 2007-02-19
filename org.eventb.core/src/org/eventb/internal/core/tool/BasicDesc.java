/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class BasicDesc {

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
	
	public BasicDesc(IConfigurationElement configElement) {
		this.bundleName = configElement.getNamespaceIdentifier();
		this.configElement = configElement;
		this.name = configElement.getAttribute("name");
		this.id = configElement.getNamespaceIdentifier() + "." + configElement.getAttribute("id");
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
