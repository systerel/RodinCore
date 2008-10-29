/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.index.persistence.xml;

import org.w3c.dom.Element;

/**
 * @author Nicolas Beauger
 * 
 */
public enum XMLAttributeTypes {
	// TODO simplify, factorize if possible

	PROJECT, ELEMENT, NAME, FILE, OCC_KIND, LOC_ATTRIBUTE, LOC_CHAR_START,
	LOC_CHAR_END, IS_SORTED, LABEL, MARK, ORDER_POS;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

	public static String getAttribute(Element node,
			XMLAttributeTypes attributeType) {
		return node.getAttribute(attributeType.toString());
	}

	public static void setAttribute(Element node,
			XMLAttributeTypes attributeType, String value) {
		node.setAttribute(attributeType.toString(), value);
	}

}
