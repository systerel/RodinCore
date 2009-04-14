/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *     ETH Zurich - initial API and implementation
 *     Systerel   - added attribute modification
 *******************************************************************************/
package org.rodinp.internal.core.version;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class RenameAttribute extends SimpleOperation {
	
	public RenameAttribute(IConfigurationElement configElement,
			SimpleConversionSheet sheet) {
		super(configElement, sheet);
		id = configElement.getAttribute("id");
		newId = configElement.getAttribute("newId");
		checkId(id);
		getSheet().checkBundle(id, this);
		checkId(newId);
		getSheet().checkBundle(newId, this);
	}
	
	private final String id;
	private final String newId;
	
	public String getId() {
		return id;
	}
	public String getNewId() {
		return newId;
	}
	
	private static String T1 = "<" + XSLConstants.XSL_TEMPLATE + " " + XSLConstants.XSL_MATCH + "=\"";
	private static String T2 = "/@";
	private static String T3 = 
		"\">\n\t<" + XSLConstants.XSL_ATTRIBUTE + " " + XSLConstants.XSL_NAME + "=\"";
	private static String T4 = 
		"\">\n\t\t<" + XSLConstants.XSL_VALUE_OF + " " + XSLConstants.XSL_SELECT + "=\".\"/>\n\t</" + 
		XSLConstants.XSL_ATTRIBUTE + ">\n</" + XSLConstants.XSL_TEMPLATE + ">";
		
	public void addTemplate(StringBuffer document, String path) {
		
		document.append(T1);
		document.append(path);
		document.append(T2);
		document.append(id);
		document.append(T3);
		document.append(newId);
		document.append(T4);
	}

}
