/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/

package org.rodinp.internal.core;

import java.lang.reflect.Constructor;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.IRodinElement;

/**
 * Base class for contributed element types.
 * 
 * @author Laurent Voisin
 */
public abstract class ContributedElementType<T extends IRodinElement> extends
		ElementType<T> {

	// Name of the plugin which contributes this element type
	private final String bundleName;

	// Constructor to use to create elements of this element type
	// (cached value)
	protected Constructor<? extends T> constructor;
	
	private static String readId(IConfigurationElement configElement) {
		String nameSpace = configElement.getNamespaceIdentifier();
		return nameSpace + "." + configElement.getAttribute("id");
	}
	
	private static String readName(IConfigurationElement configElement) {
		return configElement.getAttribute("name");
	}

	public ContributedElementType(IConfigurationElement configurationElement) {
		super(readId(configurationElement), readName(configurationElement));
		this.bundleName = configurationElement.getContributor().getName();
	}

	protected String getBundleName() {
		return bundleName;
	}
}
