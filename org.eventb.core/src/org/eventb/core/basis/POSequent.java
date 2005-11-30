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
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOModifiedPredicate;
import org.eventb.core.IPOSequent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author halstefa
 * 
 * A sequent is a tuple (NAME, TYPE_ENV, HYP, GOAL, HINTS)
 * 
 * <p>
 * The name (NAME) identifies uniquely a sequent (resp. proof obligation) in a PO file.
 * The type environment (TYPE_ENV) specifies type of identifiers local to the sequent.
 * (The type environment is contained in the sequent in form of POTypeExpressions.)
 * There is one hypothesis (HYP) in the sequent. It is of type POHypothesis.
 * There is one goal (GOAL) in the sequent. It is a POPredicate or a POModifiedPredicate.
 * Hints (HINTS) are associated with a sequent in form of attributes.
 * </p>
 *
 */
public class POSequent extends InternalElement implements IPOSequent {

	/**
	 * @param name
	 * @param parent
	 */
	public POSequent(String name, IRodinElement parent) {
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
		ArrayList<IRodinElement> list = getChildrenOfType(IPOIdentifier.ELEMENT_TYPE);
		IPOIdentifier[] identifiers = new IPOIdentifier[list.size()];
		list.toArray(identifiers);
		return identifiers;
	}
	
	public IPOHypothesis getHypothesis() throws RodinDBException {
		ArrayList<IRodinElement> list = getChildrenOfType(IPOHypothesis.ELEMENT_TYPE);
			
		assert list.size() == 1;
			
		IPOHypothesis hypothesis = (IPOHypothesis) list.get(0);
		return hypothesis;
	}
	
	public IPOAnyPredicate getGoal() throws RodinDBException {
		ArrayList<IRodinElement> list = getChildrenOfType(IPOPredicate.ELEMENT_TYPE);
		if(list.size() == 0)
			list = getChildrenOfType(IPOModifiedPredicate.ELEMENT_TYPE);
			
		assert list.size() == 1;
			
		IPOAnyPredicate goal = (IPOAnyPredicate) list.get(0);
		return goal;
	}
	
	public String getHint(String hintName) throws RodinDBException {
		// TODO implement hints as attributes
		return null;
	}

}
