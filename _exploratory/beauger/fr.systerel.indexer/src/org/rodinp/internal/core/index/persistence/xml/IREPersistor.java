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

import static org.rodinp.internal.core.index.persistence.xml.XMLAttributeTypes.*;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.w3c.dom.Element;

/**
 * @author Nicolas Beauger
 * 
 */
public class IREPersistor {

	public static IRodinElement getIREAtt(XMLAttributeTypes attributeType,
			Element elemNode) {

		final String elemStr = getAttribute(elemNode, attributeType);

		return RodinCore.valueOf(elemStr);
	}

	public static void setIREAtt(IRodinElement element,
			XMLAttributeTypes attributeType, Element elemNode) {
		setAttribute(elemNode, attributeType, element.getHandleIdentifier());
	}

}
