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

import org.rodinp.internal.core.index.persistence.PersistenceException;
import org.w3c.dom.Element;

/**
 * @author Nicolas Beauger
 * 
 */
public enum XMLAttributeTypes {

	PROJECT, ELEMENT, NAME, FILE, KIND, LOC_ATTRIBUTE, LOC_CHAR_START,
	LOC_CHAR_END, IS_SORTED, LABEL, MARK, ORDER_POS, ELEMENT_TYPE, ID;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

	public static String getAttribute(Element node,
			XMLAttributeTypes attributeType) throws PersistenceException {
		if (!hasAttribute(node, attributeType)) {
			throw new PersistenceException();
		}
		return node.getAttribute(attributeType.toString());
	}

	public static boolean hasAttribute(Element node,
			XMLAttributeTypes attributeType) {
		return node.hasAttribute(attributeType.toString());
	}

	public static void setAttribute(Element node,
			XMLAttributeTypes attributeType, String value) {
		node.setAttribute(attributeType.toString(), value);
	}

}
