/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import java.util.ArrayList;

import org.eventb.core.ISCEvent;
import org.eventb.core.ISCVariable;
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
	
	public ISCVariable[] getSCVariables() throws RodinDBException {
		ArrayList<IRodinElement> identifierList = getFilteredChildrenList(ISCVariable.ELEMENT_TYPE);
		
		SCVariable[] identifiers = new SCVariable[identifierList.size()];
		identifierList.toArray(identifiers);
		return identifiers; 
	}
}

