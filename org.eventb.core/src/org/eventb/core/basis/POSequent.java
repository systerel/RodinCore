/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added PO nature
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSelectionHint;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.pog.IPOGNature;
import org.eventb.internal.core.pog.POGNatureFactory;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B PO proof obligation as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IPOSequent</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public class POSequent extends EventBElement implements IPOSequent {

	public POSequent(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public IInternalElementType<IPOSequent> getElementType() {
		return ELEMENT_TYPE;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IPODescription#getName()
	 */
	@Override
	public String getDescription() throws RodinDBException {
		return getAttributeValue(EventBAttributes.PODESC_ATTRIBUTE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IPODescription#getSources()
	 */
	@Override
	public IPOSource[] getSources() throws RodinDBException {
		return getChildrenOfType(IPOSource.ELEMENT_TYPE); 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IPODescription#getHints()
	 */
	@Override
	public IPOSelectionHint[] getSelectionHints() throws RodinDBException {
		return getChildrenOfType(IPOSelectionHint.ELEMENT_TYPE); 
	}

	@Override
	public IPOPredicate[] getGoals() throws RodinDBException {
		return getChildrenOfType(IPOPredicate.ELEMENT_TYPE); 
	}

	@Override
	public IPOPredicateSet[] getHypotheses() throws RodinDBException {
		return getChildrenOfType(IPOPredicateSet.ELEMENT_TYPE); 
	}

	@Override
	public IPOPredicate getGoal(String elementName) {
		return getInternalElement(IPOPredicate.ELEMENT_TYPE, elementName);
	}

	@Override
	public IPOSelectionHint getSelectionHint(String elementName) {
		return getInternalElement(IPOSelectionHint.ELEMENT_TYPE, elementName);
	}

	@Override
	public IPOPredicateSet getHypothesis(String elementName) {
		return getInternalElement(IPOPredicateSet.ELEMENT_TYPE, elementName);
	}

	@Override
	public IPOSource getSource(String elementName) {
		return getInternalElement(IPOSource.ELEMENT_TYPE, elementName);
	}

	/**
	 * @since 1.3
	 */
	@Override
	public IPOGNature getPOGNature()
			throws RodinDBException {
		return POGNatureFactory.getInstance().getNature(
				getAttributeValue(EventBAttributes.PODESC_ATTRIBUTE));
	}

	/**
	 * @since 1.3
	 */
	@Override
	public void setPOGNature(IPOGNature nature, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.PODESC_ATTRIBUTE, nature
				.getDescription(), monitor);
	}

}
