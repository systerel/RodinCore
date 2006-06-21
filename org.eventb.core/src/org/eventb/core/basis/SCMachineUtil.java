/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.ArrayList;

import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachine;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ISCVariable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinElement;

/**
 * This class contains some utility methods for SC machines.
 * <p>
 * The methods correspond (roughly) to the interface {@link ISCMachine}.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
public class SCMachineUtil {

	public static ISCVariable[] getSCVariables(RodinElement element)
			throws RodinDBException {
		ArrayList<IRodinElement> list = 
			element.getFilteredChildrenList(ISCVariable.ELEMENT_TYPE);
		SCVariable[] variables = new SCVariable[list.size()];
		list.toArray(variables);
		return variables;
	}

	public static ISCInvariant[] getSCInvariants(RodinElement element)
			throws RodinDBException {
		ArrayList<IRodinElement> list =
			element.getFilteredChildrenList(ISCInvariant.ELEMENT_TYPE);
		SCInvariant[] invariants = new SCInvariant[list.size()];
		list.toArray(invariants);
		return invariants;
	}

	public static ISCTheorem[] getSCTheorems(RodinElement element)
			throws RodinDBException {
		ArrayList<IRodinElement> list = 
			element.getFilteredChildrenList(ISCTheorem.ELEMENT_TYPE);
		SCTheorem[] theorems = new SCTheorem[list.size()];
		list.toArray(theorems);
		return theorems;
	}

	public static ISCEvent[] getSCEvents(RodinElement element)
			throws RodinDBException {
		ArrayList<IRodinElement> list = 
			element.getFilteredChildrenList(ISCEvent.ELEMENT_TYPE);
		SCEvent[] events = new SCEvent[list.size()];
		list.toArray(events);
		return events;
	}

}
