/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.ArrayList;

import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IVariable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Implementation of Event-B events as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IEvent</code>.
 * </p>
 *
 * @author Laurent Voisin
 */
public class Event extends InternalElement implements IEvent {
	
	public Event(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	public IVariable[] getVariables() throws RodinDBException {
		ArrayList<IRodinElement> list = getChildrenOfType(IVariable.ELEMENT_TYPE);
		Variable[] variables = new Variable[list.size()];
		list.toArray(variables);
		return variables; 
	}

	public IGuard[] getGuards() throws RodinDBException {
		ArrayList<IRodinElement> list = getChildrenOfType(IGuard.ELEMENT_TYPE);
		Guard[] guards = new Guard[list.size()];
		list.toArray(guards);
		return guards; 
	}
	
	public IAction[] getActions() throws RodinDBException {
		ArrayList<IRodinElement> list = getChildrenOfType(IAction.ELEMENT_TYPE);
		Action[] actions = new Action[list.size()];
		list.toArray(actions);
		return actions; 
	}
	
}
