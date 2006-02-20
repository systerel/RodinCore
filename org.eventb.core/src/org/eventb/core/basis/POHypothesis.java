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
import org.eventb.core.IPRFile;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.UnnamedInternalElement;

/**
 * Implementation of Event-B PO hypothesis as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IPOHypothesis</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 * 
 */
public class POHypothesis extends UnnamedInternalElement implements IPOHypothesis {

	/**
	 * Creates a new PO hypothesis handle.
	 * 
	 * @param parent
	 *            parent of this node
	 */
	public POHypothesis(IRodinElement parent) {
		super(ELEMENT_TYPE, parent);
	}

	public IPOPredicateSet getGlobalHypothesis() throws RodinDBException {
		
		IPOPredicateSet gobalHypothesis = null;
		
		if (getOpenable() instanceof IPOFile) {
			gobalHypothesis = ((IPOFile) getOpenable()).getPredicateSet(getContents());
		}
		if (getOpenable() instanceof IPRFile) {
			gobalHypothesis = ((IPRFile) getOpenable()).getPredicateSet(getContents());
		}
		
		assert gobalHypothesis != null;
		
		return gobalHypothesis;
	}
	
	public IPOAnyPredicate[] getLocalHypothesis() throws RodinDBException {
		List<IRodinElement> predicates = getFilteredChildrenList(IPOPredicate.ELEMENT_TYPE);
		List<IRodinElement> modifiedPredicates = getFilteredChildrenList(IPOModifiedPredicate.ELEMENT_TYPE);
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
