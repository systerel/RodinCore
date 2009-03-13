/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added isInitialisation() method
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IParameter;
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
 * @author Stefan Hallerstede
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

	@Deprecated
	public IVariable[] getVariables() throws RodinDBException {
		return getChildrenOfType(IVariable.ELEMENT_TYPE); 
	}

	public IParameter[] getParameters() throws RodinDBException {
		return getChildrenOfType(IParameter.ELEMENT_TYPE); 
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
	
	@Deprecated
	public boolean hasInherited() throws RodinDBException {
		return hasAttribute(EventBAttributes.INHERITED_ATTRIBUTE);
	}

	@Deprecated
	public boolean isInherited() throws RodinDBException {
		return getAttributeValue(EventBAttributes.INHERITED_ATTRIBUTE);
	}

	@Deprecated
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

	@Deprecated
	public IVariable getVariable(String elementName) {
		return getInternalElement(IVariable.ELEMENT_TYPE, elementName);
	}

	public IParameter getParameter(String elementName) {
		return getInternalElement(IParameter.ELEMENT_TYPE, elementName);
	}

	public IWitness getWitness(String elementName) {
		return getInternalElement(IWitness.ELEMENT_TYPE, elementName);
	}

	public boolean hasExtended() throws RodinDBException {
		return hasAttribute(EventBAttributes.EXTENDED_ATTRIBUTE);
	}

	public boolean isExtended() throws RodinDBException {
		return getAttributeValue(EventBAttributes.EXTENDED_ATTRIBUTE);
	}

	public void setExtended(boolean extended, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(EventBAttributes.EXTENDED_ATTRIBUTE, extended, monitor);
	}

	public boolean isInitialisation() throws RodinDBException {
		return IEvent.INITIALISATION.equals(getLabel());
	}

}
