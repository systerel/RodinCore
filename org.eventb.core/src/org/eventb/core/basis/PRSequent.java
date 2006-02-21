/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.ArrayList;

import org.eventb.core.IPOAnyPredicate;
import org.eventb.core.IPOHypothesis;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOModifiedPredicate;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPRSequent;
import org.eventb.core.IPRStatus;
import org.eventb.core.prover.IProofTree;
import org.eventb.internal.core.pom.PRUtil;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Implementation of Event-B PR proof obligation as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IPRSequent</code>.
 * </p>
 *
 * @author Farhad Mehta
 *
 */
public class PRSequent extends InternalElement implements IPRSequent {

	/**
	 * @param name
	 * @param parent
	 */
	public PRSequent(String name, IRodinElement parent) {
		super(name, parent);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	public String getName() {
		return getElementName();
	}
	
	public IPOIdentifier[] getIdentifiers() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IPOIdentifier.ELEMENT_TYPE);
		IPOIdentifier[] identifiers = new IPOIdentifier[list.size()];
		list.toArray(identifiers);
		return identifiers;
	}
	
	public IPOHypothesis getHypothesis() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IPOHypothesis.ELEMENT_TYPE);
			
		assert list.size() == 1;
			
		IPOHypothesis hypothesis = (IPOHypothesis) list.get(0);
		return hypothesis;
	}
	
	public IPOAnyPredicate getGoal() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IPOPredicate.ELEMENT_TYPE);
		if(list.size() == 0)
			list = getFilteredChildrenList(IPOModifiedPredicate.ELEMENT_TYPE);
			
		assert list.size() == 1;
			
		IPOAnyPredicate goal = (IPOAnyPredicate) list.get(0);
		return goal;
	}
	
	public String getHint(String hintName) throws RodinDBException {
		// TODO implement hints as attributes
		return null;
	}

	public IPRStatus getStatus() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(PRStatus.ELEMENT_TYPE);
		assert list.size() == 1;
		return (IPRStatus) list.get(0);
	}

	public IProofTree makeProofTree() throws RodinDBException {
		return PRUtil.makeProofTree(this);
	}
	
	public void updateStatus(IProofTree pt) throws RodinDBException {
		PRUtil.updateStatus(this,pt);
	}
	
	
	
}
