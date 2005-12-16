/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import java.util.ArrayList;

import org.eventb.core.IPOIdentifier;
import org.eventb.core.ISCEvent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public class SCEvent extends Event implements ISCEvent {
	
	public SCEvent(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public String getElementType() {
		return ISCEvent.ELEMENT_TYPE;
	}
	
	public IPOIdentifier[] getIdentifiers() throws RodinDBException {
		ArrayList<IRodinElement> identifierList = getChildrenOfType(IPOIdentifier.ELEMENT_TYPE);
		
		IPOIdentifier[] identifiers = new IPOIdentifier[identifierList.size()];
		identifierList.toArray(identifiers);
		return identifiers; 
	}
}

