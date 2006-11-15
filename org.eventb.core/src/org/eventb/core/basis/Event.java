/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

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
public class Event extends EventBElement implements IEvent {
	
	/**
	 *  Constructor used by the Rodin database. 
	 */
	public Event(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IEvent#getRefinesClauses()
	 */
	public IRefinesEvent[] getRefinesClauses(IProgressMonitor monitor) throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IRefinesEvent.ELEMENT_TYPE);
		RefinesEvent[] refines = new RefinesEvent[list.size()];
		list.toArray(refines);
		return refines; 
	}

	@Deprecated
	public IRefinesEvent[] getRefinesClauses() throws RodinDBException {
		return getRefinesClauses(null); 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IEvent#getVariables()
	 */
	public IVariable[] getVariables(IProgressMonitor monitor) throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IVariable.ELEMENT_TYPE);
		Variable[] variables = new Variable[list.size()];
		list.toArray(variables);
		return variables; 
	}

	@Deprecated
	public IVariable[] getVariables() throws RodinDBException {
		return getVariables(null); 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IEvent#getWitnesses()
	 */
	public IWitness[] getWitnesses(IProgressMonitor monitor) throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IWitness.ELEMENT_TYPE);
		Witness[] witnesses = new Witness[list.size()];
		list.toArray(witnesses);
		return witnesses; 
	}
	
	@Deprecated
	public IWitness[] getWitnesses() throws RodinDBException {
		return getWitnesses(null); 
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IEvent#getGuards()
	 */
	public IGuard[] getGuards(IProgressMonitor monitor) throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IGuard.ELEMENT_TYPE);
		Guard[] guards = new Guard[list.size()];
		list.toArray(guards);
		return guards; 
	}
	
	@Deprecated
	public IGuard[] getGuards() throws RodinDBException {
		return getGuards(null); 
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IEvent#getActions()
	 */
	public IAction[] getActions(IProgressMonitor monitor) throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IAction.ELEMENT_TYPE);
		Action[] actions = new Action[list.size()];
		list.toArray(actions);
		return actions; 
	}

	@Deprecated
	public IAction[] getActions() throws RodinDBException {
		return getActions(); 
	}

	public boolean isInherited(IProgressMonitor monitor) throws RodinDBException {
		return getAttributeValue(EventBAttributes.INHERITED_ATTRIBUTE, monitor);
	}

	public void setInherited(boolean inherited, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.INHERITED_ATTRIBUTE, inherited, monitor);
	}

}
