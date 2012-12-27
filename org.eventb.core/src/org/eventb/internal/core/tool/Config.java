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
public class Config extends BasicDesc {

	// Unique ids of modules that are required to be executed before this module 
	private final String[] included;

	/**
	 * Creates a new module decription.
	 * 
	 * @param configElement
	 *            description of this module in the Eclipse registry
	 * @throws ModuleLoadingException 
	 */
	public Config(IConfigurationElement configElement) throws ModuleLoadingException {
		super(configElement);
		
		IConfigurationElement[] includedElements = getChildren(configElement, "config");
		included = new String[includedElements.length];
		for (int i=0; i<includedElements.length; i++) {
			included[i] = getAttribute(includedElements[i], "id");
		}
		
	}
	
	public String[] getIncluded() {
		return included;
	}
	
	
		
}
