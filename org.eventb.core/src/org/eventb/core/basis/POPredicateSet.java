/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

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
 * @since 1.0
 */
public class POPredicateSet extends EventBPOElement implements IPOPredicateSet {

	public POPredicateSet(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public IInternalElementType<IPOPredicateSet> getElementType() {
		return ELEMENT_TYPE;
	}
	
	public IPOPredicate[] getPredicates() throws RodinDBException {
		return getChildrenOfType(IPOPredicate.ELEMENT_TYPE);
	}
	
	@Deprecated
	public String getParentPredicateSetName() 
	throws RodinDBException {
//		if (hasAttribute(EventBAttributes.PARENT_SET_ATTRIBUTE, monitor))
//			return getAttributeValue(EventBAttributes.PARENT_SET_ATTRIBUTE, monitor);
//		else 
			return null;
	}
	
	public IPOPredicateSet getParentPredicateSet() 
	throws RodinDBException {
		if (hasAttribute(EventBAttributes.PARENT_SET_ATTRIBUTE))
			return (IPOPredicateSet) getTranslatedAttributeValue(EventBAttributes.PARENT_SET_ATTRIBUTE);
		else 
			return null;
	}

	@Deprecated
	public void setParentPredicateSetName(String setName, IProgressMonitor monitor) 
	throws RodinDBException {
//		setAttributeValue(EventBAttributes.PARENT_SET_ATTRIBUTE, setName, monitor);
	}

	public IPOIdentifier[] getIdentifiers() throws RodinDBException {
		return getChildrenOfType(IPOIdentifier.ELEMENT_TYPE);
	}

	public IPOIdentifier getIdentifier(String elementName) {
		return getInternalElement(IPOIdentifier.ELEMENT_TYPE, elementName);
	}

	public IPOPredicate getPredicate(String elementName) {
		return getInternalElement(IPOPredicate.ELEMENT_TYPE, elementName);
	}

	public void setParentPredicateSet(IPOPredicateSet predicateSet, IProgressMonitor monitor) 
	throws RodinDBException {
		if (predicateSet == null)
			removeAttribute(EventBAttributes.PARENT_SET_ATTRIBUTE, monitor);
		else
			setTranslatedAttributeValue(EventBAttributes.PARENT_SET_ATTRIBUTE, predicateSet, monitor);
	}

}
