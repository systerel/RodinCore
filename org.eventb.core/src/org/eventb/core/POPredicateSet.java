/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import java.util.ArrayList;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.InternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 * 
 * A predicate set consists of other predicate sets and some predicates.
 * Note, that predicates can be identified by their NAME attributes.
 *
 */
public class POPredicateSet extends InternalElement {

	public static final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".poPredicateSet";
	
	/**
	 * @param name
	 * @param parent
	 */
	public POPredicateSet(String name, IRodinElement parent) {
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
	
	public POPredicate[] getPredicates() throws RodinDBException {
		ArrayList<IRodinElement> list = getChildrenOfType(POPredicate.ELEMENT_TYPE);
		POPredicate[] predicates = new POPredicate[list.size()];
		list.toArray(predicates);
		return predicates;
	}
	
	public POPredicateSet[] getPredicateSets() throws RodinDBException {
		ArrayList<IRodinElement> list = getChildrenOfType(POPredicateSet.ELEMENT_TYPE);
		POPredicateSet[] predicateSets = new POPredicateSet[list.size()];
		list.toArray(predicateSets);
		return predicateSets;
	}

}
