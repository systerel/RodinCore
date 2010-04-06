/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
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
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSelectionHint;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.pog.IPOGNature;
import org.eventb.internal.core.Messages;
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
	
	@Deprecated
	public String getName() {
		return getElementName();
	}
	
	@Deprecated
	public IPOIdentifier[] getIdentifiers() throws RodinDBException {
		return getChildrenOfType(IPOIdentifier.ELEMENT_TYPE);
	}
	
	@Deprecated
	public IPOPredicateSet getHypothesis() throws RodinDBException {
		return getSingletonChild(IPOPredicateSet.ELEMENT_TYPE,
				Messages.database_SequentMultipleHypothesisFailure);
	}

	@Deprecated
	public IPOPredicate getGoal() throws RodinDBException {
		return getSingletonChild(IPOPredicate.ELEMENT_TYPE,
				Messages.database_SequentMultipleGoalFailure);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.IPODescription#getName()
	 */
	public String getDescription() throws RodinDBException {
		return getAttributeValue(EventBAttributes.PODESC_ATTRIBUTE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IPODescription#getSources()
	 */
	public IPOSource[] getSources() throws RodinDBException {
		return getChildrenOfType(IPOSource.ELEMENT_TYPE); 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IPODescription#getHints()
	 */
	public IPOSelectionHint[] getSelectionHints() throws RodinDBException {
		return getChildrenOfType(IPOSelectionHint.ELEMENT_TYPE); 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IPOSequent#setDescriptionName(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setDescription(String description, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.PODESC_ATTRIBUTE, description, monitor);
	}

	public IPOPredicate[] getGoals() throws RodinDBException {
		return getChildrenOfType(IPOPredicate.ELEMENT_TYPE); 
	}

	public IPOPredicateSet[] getHypotheses() throws RodinDBException {
		return getChildrenOfType(IPOPredicateSet.ELEMENT_TYPE); 
	}

	public IPOPredicate getGoal(String elementName) {
		return getInternalElement(IPOPredicate.ELEMENT_TYPE, elementName);
	}

	public IPOSelectionHint getSelectionHint(String elementName) {
		return getInternalElement(IPOSelectionHint.ELEMENT_TYPE, elementName);
	}

	public IPOPredicateSet getHypothesis(String elementName) {
		return getInternalElement(IPOPredicateSet.ELEMENT_TYPE, elementName);
	}

	public IPOSource getSource(String elementName) {
		return getInternalElement(IPOSource.ELEMENT_TYPE, elementName);
	}

	/**
	 * @since 1.3
	 */
	public IPOGNature getPOGNature()
			throws RodinDBException {
		return POGNatureFactory.getInstance().getNature(
				getAttributeValue(EventBAttributes.PODESC_ATTRIBUTE));
	}

	/**
	 * @since 1.3
	 */
	public void setPOGNature(IPOGNature nature, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.PODESC_ATTRIBUTE, nature
				.getDescription(), monitor);
	}

}
