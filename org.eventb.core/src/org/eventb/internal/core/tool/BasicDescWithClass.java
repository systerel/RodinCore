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
public class BasicDescWithClass extends BasicDesc {
	
	public BasicDescWithClass(IConfigurationElement configElement) throws ModuleLoadingException {
		super(configElement);
		this.className = getAttribute(configElement, "class");		
	}

	// Fully qualified name of the class implementing the object described by this desc
	private final String className;

	public String getClassName() {
		return className;
	}

}
