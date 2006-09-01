/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPRFile;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

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
		IPOPredicateSet sup = null;
		
		// TODO the hack below has to disappear
		
			if (getOpenable() instanceof IPOFile)
			sup = ((IPOFile) getOpenable()).getPredicateSet(getContents());
			else sup = ((IPRFile) getOpenable()).getPredicateSet(getContents());
	
		return sup;
	}

	public void setParentPredicateSet(String setName, IProgressMonitor monitor) throws RodinDBException {
		setContents(setName, monitor);
	}

}
