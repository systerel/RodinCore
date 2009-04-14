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
public class RenameElement extends SimpleOperation {
	
	public RenameElement(String id, String newId, SimpleConversionSheet sheet) {
		super(null, sheet);
		this.id = id;
		this.newId = newId;
	}

	public RenameElement(
			IConfigurationElement configElement,
			String id,
			SimpleConversionSheet sheet) {
		super(configElement, sheet);
		this.newId = configElement.getAttribute("newId");
		this.id = id;
		checkIds();
	}
	
	private void checkIds() {
		if (!id.equals(newId))
			if (id.equals(getSheet().getType().getId()))
				throw new IllegalStateException("File element ids must not be renamed");
		getSheet().checkBundle(id, this);
		checkId(newId);
		getSheet().checkBundle(newId, this);
	}
	
	public RenameElement(
			IConfigurationElement configElement, 
			String id, String newid, SimpleConversionSheet sheet) {
		super(configElement, sheet);
		this.newId = newid;
		this.id = id;
		checkIds();
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
	private static String T2 = "\">\n\t<" + XSLConstants.XSL_ELEMENT + " " + XSLConstants.XSL_NAME + "=\"";
	private static String T3 = "\">\n";
	private static String T4 = "\t</" + XSLConstants.XSL_ELEMENT + ">\n</" + XSLConstants.XSL_TEMPLATE + ">\n";
		
	public void beginTemplate(StringBuffer document, String path) {
		
		document.append(T1);
		document.append(path);
		document.append(T2);
		document.append(newId);
		document.append(T3);
	}
	
	public void endTemplate(StringBuffer document) {
		document.append(T4);
	}

}
