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
	
	public void beginTemplate(XSLWriter writer, String path) {
		writer.beginTemplate(path);
		writer.beginElement(newId);
	}
	
	public void endTemplate(XSLWriter writer) {
		writer.endElement();
		writer.endTemplate();
	}

}
