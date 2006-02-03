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
 * Implementation of Event-B SC event as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>ISCEvent</code>.
 * </p>
 *
 * @author Stefan Hallerstede
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

