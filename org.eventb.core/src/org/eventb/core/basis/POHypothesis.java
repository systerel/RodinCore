/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.List;

import org.eventb.core.IPOAnyPredicate;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOHypothesis;
import org.eventb.core.IPOModifiedPredicate;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.UnnamedInternalElement;

/**
 * @author halstefa
 * 
 * A Hypothesis is a pair (GOBAL_HYP, LOCAL_HYP)
 * <p>
 * The contents of the hypothesis is a reference to a predicate set (GLOBAL_HYP).
 * The children of the hypothesis are the local hypotheses (LOCAL_HYP).
 * The children are of either of type POPredicate or POModifiedPredicate.
 * </p>
 */
public class POHypothesis extends UnnamedInternalElement implements IPOHypothesis {

	/**
	 * @param type
	 * @param parent
	 */
	public POHypothesis(String type, IRodinElement parent) {
		super(type, parent);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	public IPOPredicateSet getGlobalHypothesis() throws RodinDBException {
		IPOPredicateSet gobalHypothesis = ((IPOFile) getOpenable()).getPredicateSet(getContents());
		
		assert gobalHypothesis != null;
		
		return gobalHypothesis;
	}
	
	public IPOAnyPredicate[] getLocalHypothesis() throws RodinDBException {
		List<IRodinElement> predicates = getChildrenOfType(IPOPredicate.ELEMENT_TYPE);
		List<IRodinElement> modifiedPredicates = getChildrenOfType(IPOModifiedPredicate.ELEMENT_TYPE);
		IPOAnyPredicate[] localHypothesis = new IPOAnyPredicate[predicates.size() + modifiedPredicates.size()];
		int i = 0;
		for(IRodinElement predicate : predicates) {
			localHypothesis[i] = (IPOPredicate) predicate;
			i++;
		}
		for(IRodinElement predicate : modifiedPredicates) {
			localHypothesis[i] = (IPOModifiedPredicate) predicate;
			i++;
		}
		return localHypothesis;
	}

}
