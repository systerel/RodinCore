/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

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
	public IInternalElementType<IEvent> getElementType() {
		return ELEMENT_TYPE;
	}
	
	public IRefinesEvent[] getRefinesClauses() throws RodinDBException {
		return getChildrenOfType(IRefinesEvent.ELEMENT_TYPE); 
	}

	public IVariable[] getVariables() throws RodinDBException {
		return getChildrenOfType(IVariable.ELEMENT_TYPE); 
	}

	public IWitness[] getWitnesses() throws RodinDBException {
		return getChildrenOfType(IWitness.ELEMENT_TYPE); 
	}
	
	public IGuard[] getGuards() throws RodinDBException {
		return getChildrenOfType(IGuard.ELEMENT_TYPE); 
	}
	
	public IAction[] getActions() throws RodinDBException {
		return getChildrenOfType(IAction.ELEMENT_TYPE); 
	}

	public boolean isInherited() throws RodinDBException {
		return getAttributeValue(EventBAttributes.INHERITED_ATTRIBUTE);
	}

	public void setInherited(boolean inherited, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.INHERITED_ATTRIBUTE, inherited, monitor);
	}

	public IAction getAction(String elementName) {
		return getInternalElement(IAction.ELEMENT_TYPE, elementName);
	}

	public IGuard getGuard(String elementName) {
		return getInternalElement(IGuard.ELEMENT_TYPE, elementName);
	}

	public IRefinesEvent getRefinesClause(String elementName) {
		return getInternalElement(IRefinesEvent.ELEMENT_TYPE, elementName);
	}

	public IVariable getVariable(String elementName) {
		return getInternalElement(IVariable.ELEMENT_TYPE, elementName);
	}

	public IWitness getWitness(String elementName) {
		return getInternalElement(IWitness.ELEMENT_TYPE, elementName);
	}

}
