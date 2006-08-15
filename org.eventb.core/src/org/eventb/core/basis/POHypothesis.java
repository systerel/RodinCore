/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.List;

import org.eventb.core.IPOFile;
import org.eventb.core.IPOHypothesis;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPRFile;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

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
public class POHypothesis extends InternalElement implements IPOHypothesis {

	/**
	 * Creates a new PO hypothesis handle.
	 * 
	 * @param name
	 *            name of this element
	 * @param parent
	 *            parent of this element
	 */
	public POHypothesis(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	public IPOPredicateSet getGlobalHypothesis() throws RodinDBException {
		
		IPOPredicateSet globalHypothesis = null;
		
		if (getOpenable() instanceof IPOFile) {
			globalHypothesis = ((IPOFile) getOpenable()).getPredicateSet(getContents());
		}
		if (getOpenable() instanceof IPRFile) {
			globalHypothesis = ((IPRFile) getOpenable()).getPredicateSet(getContents());
		}
		
		assert globalHypothesis != null;
		
		return globalHypothesis;
	}
	
	public IPOPredicate[] getLocalHypothesis() throws RodinDBException {
		List<IRodinElement> predicates = getFilteredChildrenList(IPOPredicate.ELEMENT_TYPE);
		IPOPredicate[] localHypothesis = new IPOPredicate[predicates.size()];
		predicates.toArray(localHypothesis);
		return localHypothesis;
	}

}
