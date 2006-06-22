/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
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
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.eventb.internal.core.Messages;
import org.eventb.internal.core.Util;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Implementation of Event-B events as an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
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
	
	/**
	 *  Constructor used by the Rodin database. 
	 */
	public Event(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IEvent#getRefinesClauses()
	 */
	public IRefinesEvent[] getRefinesClauses() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IRefinesEvent.ELEMENT_TYPE);
		RefinesEvent[] events = new RefinesEvent[list.size()];
		list.toArray(events);
		return events; 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IEvent#getVariables()
	 */
	public IVariable[] getVariables() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IVariable.ELEMENT_TYPE);
		Variable[] variables = new Variable[list.size()];
		list.toArray(variables);
		return variables; 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IEvent#getWitnesses()
	 */
	public IWitness[] getWitnesses() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IWitness.ELEMENT_TYPE);
		Witness[] witnesses = new Witness[list.size()];
		list.toArray(witnesses);
		return witnesses; 
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IEvent#getGuards()
	 */
	public IGuard[] getGuards() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IGuard.ELEMENT_TYPE);
		Guard[] guards = new Guard[list.size()];
		list.toArray(guards);
		return guards; 
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IEvent#getActions()
	 */
	public IAction[] getActions() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IAction.ELEMENT_TYPE);
		Action[] actions = new Action[list.size()];
		list.toArray(actions);
		return actions; 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ILabeledElement#setLabel(java.lang.String)
	 */
	public void setLabel(String label) throws RodinDBException {
		LabeledElementUtil.setLabel(this, label);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ILabeledElement#getLabel()
	 */
	public String getLabel() throws RodinDBException {
		return LabeledElementUtil.getLabel(this);
	}

	public boolean isInherited() throws RodinDBException {
		return getContents().equals("TRUE");
	}

	public void setInherited(boolean inherited) throws RodinDBException {
		if (inherited) {
			if (hasChildren())
				throw Util.newRodinDBException(Messages.database_EventSetInheritedFailure, this);
		}
		setContents(inherited ? "TRUE" : "FALSE");
	}

}
