/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core.version;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ExtensionDesc {

	private final String bundleName;
	/**
	 * Creates a new executable extension description.
	 * 
	 * @param configElement
	 *            description of this executable extension in the Eclipse registry
	 */
	public ExtensionDesc(IConfigurationElement configElement) {
		this.bundleName = configElement.getContributor().getName();
	}
	public String getBundleName() {
		return bundleName;
	}	
	
}
