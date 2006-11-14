/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Implementation of Event-B PO predicate set as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IPOPredicateSet</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 *
 */
public class POPredicateSet extends InternalElement implements IPOPredicateSet {

	public POPredicateSet(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}
	
	public IPOPredicate[] getPredicates(IProgressMonitor monitor) throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IPOPredicate.ELEMENT_TYPE);
		IPOPredicate[] predicates = new IPOPredicate[list.size()];
		list.toArray(predicates);
		return predicates;
	}
	
	@Deprecated
	public IPOPredicate[] getPredicates() throws RodinDBException {
		return getPredicates(null);
	}
	
	public String getParentPredicateSetName(IProgressMonitor monitor) 
	throws RodinDBException {
		if (hasAttribute(EventBAttributes.PARENT_SET_ATTRIBUTE, monitor))
			return getStringAttribute(EventBAttributes.PARENT_SET_ATTRIBUTE, monitor);
		else return null;
	}
	
	public IPOPredicateSet getParentPredicateSet(IProgressMonitor monitor) 
	throws RodinDBException {
		String parentPredicateSetName = getParentPredicateSetName(monitor);
		if (parentPredicateSetName == null)
			return null;
		IPOPredicateSet parentSet = 
			((IPOFile) getOpenable()).getPredicateSet(parentPredicateSetName, monitor);
		return parentSet;
	}

	public void setParentPredicateSetName(String setName, IProgressMonitor monitor) 
	throws RodinDBException {
		setStringAttribute(EventBAttributes.PARENT_SET_ATTRIBUTE, setName, monitor);
	}

	@Deprecated
	public IPOPredicateSet getParentPredicateSet() throws RodinDBException {
		return getParentPredicateSet(null);
	}

	public IPOIdentifier[] getIdentifiers(IProgressMonitor monitor) throws RodinDBException {
		IPOIdentifier[] identifiers = 
			(IPOIdentifier[]) getChildrenOfType(IPOIdentifier.ELEMENT_TYPE);
		return identifiers;
	}

}
