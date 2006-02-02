/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.ArrayList;

import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author halstefa
 * 
 * A predicate set consists of predicates and perhaps a predicate set 
 * whose name is stored in the contents field.
 * Note, that predicates can be identified by their NAME attributes.
 * if the contents equals the empty string there is no contained predicate set.
 *
 */
public class POPredicateSet extends InternalElement implements IPOPredicateSet {

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
	
	public IPOPredicate[] getPredicates() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IPOPredicate.ELEMENT_TYPE);
		IPOPredicate[] predicates = new IPOPredicate[list.size()];
		list.toArray(predicates);
		return predicates;
	}
	
	public IPOPredicateSet getPredicateSet() throws RodinDBException {
		if (getContents().equals("")) return null;
		IPOPredicateSet sup = ((IPOFile) getOpenable()).getPredicateSet(getContents());
	
		return sup;
	}

}
