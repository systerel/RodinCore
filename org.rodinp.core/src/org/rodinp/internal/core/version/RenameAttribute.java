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
 *     Systerel   - used XSLWriter
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
	
	public void addTemplate(XSLWriter writer, String path) {
		final String match = path + "/@" + id;
		writer.beginTemplate(match);
		writer.beginAttribute(newId);
		writer.valueOf(".");
		writer.endAttribute();
		writer.endTemplate();
	}

}
