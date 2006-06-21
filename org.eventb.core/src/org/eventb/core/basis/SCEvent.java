/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import java.util.ArrayList;

import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCRefinesEvent;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISCWitness;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

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
public class SCEvent extends InternalElement implements ISCEvent {
	
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
	public String getElementType() {
		return ISCEvent.ELEMENT_TYPE;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getSCRefinesClauses()
	 */
	public ISCRefinesEvent[] getSCRefinesClauses() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCRefinesEvent.ELEMENT_TYPE);
		SCRefinesEvent[] events = new SCRefinesEvent[list.size()];
		list.toArray(events);
		return events; 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getAbstractSCEvents()
	 */
	public ISCEvent[] getAbstractSCEvents() throws RodinDBException {
		ArrayList<IRodinElement> refinesClauses = getFilteredChildrenList(ISCRefinesEvent.ELEMENT_TYPE);
		ISCEvent[] result = new ISCEvent[refinesClauses.size()];
		for (int i = 0; i < result.length; i++) {
			ISCRefinesEvent refinesClause = (ISCRefinesEvent) refinesClauses.get(i);
			result[i] = refinesClause.getAbstractSCEvent();
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getSCVariables()
	 */
	public ISCVariable[] getSCVariables() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCVariable.ELEMENT_TYPE);
		SCVariable[] variables = new SCVariable[list.size()];
		list.toArray(variables);
		return variables; 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getSCWitnesses()
	 */
	public ISCWitness[] getSCWitnesses() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCWitness.ELEMENT_TYPE);
		SCWitness[] witnesses = new SCWitness[list.size()];
		list.toArray(witnesses);
		return witnesses; 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getSCGuards()
	 */
	public ISCGuard[] getSCGuards() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCGuard.ELEMENT_TYPE);
		SCGuard[] guards = new SCGuard[list.size()];
		list.toArray(guards);
		return guards; 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getSCActions()
	 */
	public ISCAction[] getSCActions() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCAction.ELEMENT_TYPE);
		SCAction[] actions = new SCAction[list.size()];
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

}
