/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

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
	public ISCRefinesEvent[] getSCRefinesClauses() throws RodinDBException {
		return (ISCRefinesEvent[]) getChildrenOfType(ISCRefinesEvent.ELEMENT_TYPE); 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getAbstractSCEvents()
	 */
	public ISCEvent[] getAbstractSCEvents() throws RodinDBException {
		final ISCRefinesEvent[] refinesClauses =
			(ISCRefinesEvent[]) getChildrenOfType(ISCRefinesEvent.ELEMENT_TYPE);
		final ISCEvent[] result = new ISCEvent[refinesClauses.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = refinesClauses[i].getAbstractSCEvent();
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getSCVariables()
	 */
	public ISCVariable[] getSCVariables() throws RodinDBException {
		return (ISCVariable[]) getChildrenOfType(ISCVariable.ELEMENT_TYPE); 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getSCWitnesses()
	 */
	public ISCWitness[] getSCWitnesses() throws RodinDBException {
		return (ISCWitness[]) getChildrenOfType(ISCWitness.ELEMENT_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getSCGuards()
	 */
	public ISCGuard[] getSCGuards() throws RodinDBException {
		return (ISCGuard[]) getChildrenOfType(ISCGuard.ELEMENT_TYPE); 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCEvent#getSCActions()
	 */
	public ISCAction[] getSCActions() throws RodinDBException {
		return (ISCAction[]) getChildrenOfType(ISCAction.ELEMENT_TYPE); 
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
