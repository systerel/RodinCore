/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
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
	 *            description of this executable extension in the Eclipse registry,
	 *            or null if none
	 */
	public ExtensionDesc(IConfigurationElement configElement) {
		this.bundleName = configElement == null ? null : configElement.getContributor().getName();
	}
	public String getBundleName() {
		return bundleName;
	}	
	
}
