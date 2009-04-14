/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel   - added attribute modification
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
	
	private static String T1 = "\t\t<" + XSLConstants.XSL_ATTRIBUTE + " " + XSLConstants.XSL_NAME + "=\"";
	private static String T2 = "\">";
	private static String T3 = "</" + XSLConstants.XSL_ATTRIBUTE + ">\n";

	public void addAttribute(StringBuffer document) {
		document.append(T1);
		document.append(newId);
		document.append(T2);
		document.append(newValue);
		document.append(T3);
	}

}
