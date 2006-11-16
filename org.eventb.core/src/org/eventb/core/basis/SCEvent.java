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
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCRefinesEvent;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISCWitness;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B SC events as an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>ISCEvent</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 */
public class SCEvent extends EventBElement implements ISCEvent {
	
	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCEvent(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinElement#getElementType()
	 */
	@Override
	public IInternalElementType getElementType() {
		return ISCEvent.ELEMENT_TYPE;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getSCRefinesClauses()
	 */
	public ISCRefinesEvent[] getSCRefinesClauses(IProgressMonitor monitor) throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCRefinesEvent.ELEMENT_TYPE);
		SCRefinesEvent[] events = new SCRefinesEvent[list.size()];
		list.toArray(events);
		return events; 
	}

	@Deprecated
	public ISCRefinesEvent[] getSCRefinesClauses() throws RodinDBException {
		return getSCRefinesClauses(null); 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getAbstractSCEvents()
	 */
	public ISCEvent[] getAbstractSCEvents(IProgressMonitor monitor) throws RodinDBException {
		ArrayList<IRodinElement> refinesClauses = getFilteredChildrenList(ISCRefinesEvent.ELEMENT_TYPE);
		ISCEvent[] result = new ISCEvent[refinesClauses.size()];
		for (int i = 0; i < result.length; i++) {
			ISCRefinesEvent refinesClause = (ISCRefinesEvent) refinesClauses.get(i);
			result[i] = refinesClause.getAbstractSCEvent(null);
		}
		return result;
	}
	
	@Deprecated
	public ISCEvent[] getAbstractSCEvents() throws RodinDBException {
		return getAbstractSCEvents(null);
	}
	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getSCVariables()
	 */
	public ISCVariable[] getSCVariables(IProgressMonitor monitor) throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCVariable.ELEMENT_TYPE);
		SCVariable[] variables = new SCVariable[list.size()];
		list.toArray(variables);
		return variables; 
	}

	@Deprecated
	public ISCVariable[] getSCVariables() throws RodinDBException {
		return getSCVariables(null); 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getSCWitnesses()
	 */
	public ISCWitness[] getSCWitnesses(IProgressMonitor monitor) throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCWitness.ELEMENT_TYPE);
		SCWitness[] witnesses = new SCWitness[list.size()];
		list.toArray(witnesses);
		return witnesses; 
	}

	@Deprecated
	public ISCWitness[] getSCWitnesses() throws RodinDBException {
		return getSCWitnesses(null); 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getSCGuards()
	 */
	public ISCGuard[] getSCGuards(IProgressMonitor monitor) throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCGuard.ELEMENT_TYPE);
		SCGuard[] guards = new SCGuard[list.size()];
		list.toArray(guards);
		return guards; 
	}

	@Deprecated
	public ISCGuard[] getSCGuards() throws RodinDBException {
		return getSCGuards(null); 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getSCActions()
	 */
	public ISCAction[] getSCActions(IProgressMonitor monitor) throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCAction.ELEMENT_TYPE);
		SCAction[] actions = new SCAction[list.size()];
		list.toArray(actions);
		return actions; 
	}

	@Deprecated
	public ISCAction[] getSCActions() throws RodinDBException {
		return getSCActions(null); 
	}

	public ISCAction getSCAction(String elementName) {
		return (ISCAction) getInternalElement(ISCAction.ELEMENT_TYPE, elementName);
	}

	public ISCGuard getSCGuard(String elementName) {
		return (ISCGuard) getInternalElement(ISCGuard.ELEMENT_TYPE, elementName);
	}

	public ISCRefinesEvent getSCRefinesClause(String elementName) {
		return (ISCRefinesEvent) getInternalElement(ISCRefinesEvent.ELEMENT_TYPE, elementName);
	}

	public ISCVariable getSCVariable(String elementName) {
		return (ISCVariable) getInternalElement(ISCVariable.ELEMENT_TYPE, elementName);
	}

	public ISCWitness getSCWitness(String elementName) {
		return (ISCWitness) getInternalElement(ISCWitness.ELEMENT_TYPE, elementName);
	}

}
