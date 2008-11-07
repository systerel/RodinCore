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

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.internal.core.index.persistence.PersistenceException;
import org.w3c.dom.Element;

/**
 * @author Nicolas Beauger
 * 
 */
public class IREPersistor {

    public static IRodinElement getIREAtt(Element elemNode,
	    XMLAttributeTypes attributeType) throws PersistenceException {

	final String elemStr = getAttribute(elemNode, attributeType);
	return RodinCore.valueOf(elemStr);
    }

    public static IInternalElement getIIEAtt(Element elemNode,
	    XMLAttributeTypes attributeType) throws PersistenceException {
	final IRodinElement ire = getIREAtt(elemNode, attributeType);
	if (ire instanceof IInternalElement) {
	    return (IInternalElement) ire;
	}
	throw new PersistenceException();
    }

    public static IRodinFile getIRFAtt(Element elemNode,
	    XMLAttributeTypes attributeType) throws PersistenceException {
	final IRodinElement ire = getIREAtt(elemNode, attributeType);
	if (ire instanceof IRodinFile) {
	    return (IRodinFile) ire;
	}
	throw new PersistenceException();
    }

    public static void setIREAtt(IRodinElement element,
	    XMLAttributeTypes attributeType, Element elemNode) {
	setAttribute(elemNode, attributeType, element.getHandleIdentifier());
    }

}
