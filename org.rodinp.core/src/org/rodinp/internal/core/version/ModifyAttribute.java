/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.rodinp.internal.core.version;

import static java.util.Arrays.asList;
import static org.rodinp.internal.core.version.XSLConstants.XSL_MATCH;
import static org.rodinp.internal.core.version.XSLConstants.XSL_TEMPLATE;

import org.eclipse.core.runtime.IConfigurationElement;
import org.rodinp.core.version.IAttributeModifier;
import org.rodinp.internal.core.util.Util;

/**
 * @author Nicolas Beauger
 * 
 */
public class ModifyAttribute extends PatternOperation {

	private final String attributeTypeId;
	private final IConfigurationElement configElement;

	public ModifyAttribute(IConfigurationElement configElement) {
		super(configElement);
		this.configElement = configElement;
		this.attributeTypeId = configElement.getAttribute("id");
		checkId(attributeTypeId);
	}

	private void addTemplate(XSLWriter writer, String path, int modifierKey) {
		final String match = path + "/@" + attributeTypeId;
		final String modifierClass = XSLModifier.class.getCanonicalName();
		final String modifierCall = "modifier:modify(string(.), number('"
				+ modifierKey + "'))";

		writer.beginMarkup(XSL_TEMPLATE, asList(XSL_MATCH, "xmlns:modifier"),
				asList(match, modifierClass), true);
		writer.beginAttribute(attributeTypeId);
		writer.valueOf(modifierCall);
		writer.endAttribute();
		writer.endTemplate();
	}

	private int initModifier() {
		if (!configElement.isValid()) {
			processError(null,
					"configuration configElement is not valid anymore");
			return -1;
		}
		try {
			final Object instance = configElement
					.createExecutableExtension("class");
			if (!(instance instanceof IAttributeModifier)) {
				processError(null,
						"executable extension is not an instance of IAttributeModifier");
				return -1;
			}
			final IAttributeModifier modifier = (IAttributeModifier) instance;
			final int modifierKey = XSLModifier.addModifier(modifier);

			return modifierKey;
		} catch (Exception e) {
			processError(e, e.getMessage());
			return -1;
		}
	}

	private void processError(Exception e, String reason) {
		final String message = "Unable to instantiate attribute modifier class "
				+ attributeTypeId + "\nReason: " + reason;
		Util.log(e, message);
	}

	public String getId() {
		return attributeTypeId;
	}

	public void addTemplate(XSLWriter writer, String path) {
		final int modifierKey = initModifier();
		if (modifierKey < 0) {
			throw new IllegalStateException(
					"Version Upgrade: unable to make an xsl template. See log for details");
		}
		addTemplate(writer, path, modifierKey);
	}

}
