/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added attribute modification
 *     Systerel - used XSL writer
 *******************************************************************************/
package org.rodinp.internal.core.version;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class AddAttribute extends SimpleOperation {

	public AddAttribute(IConfigurationElement configElement,
			SimpleConversionSheet sheet) {
		super(configElement, sheet);
		newId = configElement.getAttribute("newId");
		newValue = configElement.getAttribute("newValue");
		checkId(newId);
		getSheet().checkBundle(newId, this);
	}
	
	public AddAttribute(String newId, String newValue, SimpleConversionSheet sheet) {
		super(null, sheet);
		this.newId = newId;
		this.newValue = newValue;
	}

	private final String newId;
	private final String newValue;
	
	public String getNewId() {
		return newId;
	}

	public String getNewValue() {
		return newValue;
	}
	
	public void addAttribute(XSLWriter writer) {
		writer.simpleAttribute(newId, newValue);
	}

}
