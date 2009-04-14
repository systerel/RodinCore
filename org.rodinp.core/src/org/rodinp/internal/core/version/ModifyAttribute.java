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

	private static String TEMPLATE_START = "<" + XSLConstants.XSL_TEMPLATE
			+ " " + XSLConstants.XSL_MATCH + "=\"";
	private static String SLASH_AROBASE = "/@";
	private static String MODIFIER_NS = "\" xmlns:modifier=\""
			+ XSLModifier.class.getCanonicalName() + "\">\n";

	private static String ATTRIBUTE_START = "\t<" + XSLConstants.XSL_ATTRIBUTE
			+ " " + XSLConstants.XSL_NAME + "=\"";
	private static String ATTRIBUTE_MIDDLE = "\">\n";
	private static String VALUE_OF_MODIFIER = "\t\t<"
			+ XSLConstants.XSL_VALUE_OF + " " + XSLConstants.XSL_SELECT
			+ "=\"modifier:modify(string(.), number('";

	private static String VALUE_OF_END = "'))\"/>\n";
	private static String ATTRIBUTE_END = "\t</" + XSLConstants.XSL_ATTRIBUTE
			+ ">\n</";
	private static String TEMPLATE_END = XSLConstants.XSL_TEMPLATE + ">\n";

	private String makeTemplate(String path, int modifierKey) {
		final StringBuilder sb = new StringBuilder();
		sb.append(TEMPLATE_START);
		sb.append(path);
		sb.append(SLASH_AROBASE);
		sb.append(attributeTypeId);
		sb.append(MODIFIER_NS);
		sb.append(ATTRIBUTE_START);
		sb.append(attributeTypeId);
		sb.append(ATTRIBUTE_MIDDLE);
		sb.append(VALUE_OF_MODIFIER);
		sb.append(modifierKey);
		sb.append(VALUE_OF_END);
		sb.append(ATTRIBUTE_END);
		sb.append(TEMPLATE_END);

		return sb.toString();
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

	public String getTemplate(String path) {
		final int modifierKey = initModifier();
		if (modifierKey < 0) {
			throw new IllegalStateException(
					"Version Upgrade: unable to make an xsl template. See log for details");
		}
		return makeTemplate(path, modifierKey);
	}

	public String getId() {
		return attributeTypeId;
	}

}
