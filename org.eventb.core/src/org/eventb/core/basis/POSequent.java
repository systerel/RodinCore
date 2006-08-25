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
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOHint;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

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
 */
public class POSequent extends InternalElement implements IPOSequent {

	public static String DESCRIPTION_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".label";

	/**
	 * @param name
	 * @param parent
	 */
	public POSequent(String name, IRodinElement parent) {
		super(name, parent);
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
	
	public IPOPredicateSet getHypothesis() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IPOPredicateSet.ELEMENT_TYPE);
			
		assert list.size() == 1;
			
		IPOPredicateSet hypothesis = (IPOPredicateSet) list.get(0);
		return hypothesis;
	}
	
	public IPOPredicate getGoal() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IPOPredicate.ELEMENT_TYPE);
			
		assert list.size() == 1;
			
		IPOPredicate goal = (IPOPredicate) list.get(0);
		return goal;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IPODescription#getName()
	 */
	public String getDescription(IProgressMonitor monitor) throws RodinDBException {
		return getStringAttribute(DESCRIPTION_ATTRIBUTE, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IPODescription#getSources()
	 */
	public IPOSource[] getSources() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IPOSource.ELEMENT_TYPE);
		IPOSource[] sources = new IPOSource[list.size()];
		list.toArray(sources);
		return sources;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IPODescription#getHints()
	 */
	public IPOHint[] getHints() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IPOHint.ELEMENT_TYPE);
		IPOHint[] hints = new IPOHint[list.size()];
		list.toArray(hints);
		return hints;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IPOSequent#setDescriptionName(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setDescription(String description, IProgressMonitor monitor) throws RodinDBException {
		setStringAttribute(DESCRIPTION_ATTRIBUTE, description, monitor);
	}

}
