/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.CoreException;
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
 * @noinstantiate This class is not intended to be instantiated by clients.
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
	
	@Override
	public IPOPredicate[] getPredicates() throws RodinDBException {
		return getChildrenOfType(IPOPredicate.ELEMENT_TYPE);
	}
	
	@Override
	public IPOPredicateSet getParentPredicateSet() throws CoreException {
		if (hasAttribute(EventBAttributes.PARENT_SET_ATTRIBUTE))
			return (IPOPredicateSet) getTranslatedAttributeValue(EventBAttributes.PARENT_SET_ATTRIBUTE);
		else
			return null;
	}

	@Override
	public IPOIdentifier[] getIdentifiers() throws RodinDBException {
		return getChildrenOfType(IPOIdentifier.ELEMENT_TYPE);
	}

	@Override
	public IPOIdentifier getIdentifier(String elementName) {
		return getInternalElement(IPOIdentifier.ELEMENT_TYPE, elementName);
	}

	@Override
	public IPOPredicate getPredicate(String elementName) {
		return getInternalElement(IPOPredicate.ELEMENT_TYPE, elementName);
	}

	@Override
	public void setParentPredicateSet(IPOPredicateSet predicateSet,
			IProgressMonitor monitor) throws CoreException {
		if (predicateSet == null)
			removeAttribute(EventBAttributes.PARENT_SET_ATTRIBUTE, monitor);
		else
			setTranslatedAttributeValue(EventBAttributes.PARENT_SET_ATTRIBUTE,
					predicateSet, monitor);
	}

}
