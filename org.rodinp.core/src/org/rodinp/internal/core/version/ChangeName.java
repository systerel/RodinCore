/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.version;

import static org.rodinp.internal.core.Buffer.NAME_ATTRIBUTE;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Nicolas Beauger
 *
 */
public class ChangeName extends SimpleOperation {

	private final String newValue;
	
	public ChangeName(IConfigurationElement configElement,
			SimpleConversionSheet sheet) {
		super(configElement, sheet);
		final String userValue = configElement.getAttribute("newValue");
		newValue = computeNewValue(userValue);
	}

	private String computeNewValue(final String userValue) {
		final XSLWriter writer = new XSLWriter();
		writer.valueOf(".", false);
		final String currentName = writer.getDocument();
		return userValue.replace("@", currentName);
	}

	public void addTemplate(XSLWriter writer, String path) {
		final String match = path + "/@" + NAME_ATTRIBUTE;
		writer.beginTemplate(match);
		writer.simpleAttribute(NAME_ATTRIBUTE, newValue);
		writer.endTemplate();
	}
	
	public String getNewValue() {
		return newValue;
	}
}
