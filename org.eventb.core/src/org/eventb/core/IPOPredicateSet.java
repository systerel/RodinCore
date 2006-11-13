/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for predicate sets in Event-B Proof Obligation (PO) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * A predicate set consists of predicates <code>getPredicates()</code> and perhaps a predicate set 
 * <code>getParentPredicateSet()</code>.
 * Predicate sets are stored in a tree structure. A predicate set without a parent predicate set
 * is the root of the tree.
 * </p>
 * @author Stefan Hallerstede
 *
 */
public interface IPOPredicateSet extends IInternalElement {
	
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".poPredicateSet"); //$NON-NLS-1$
	
	/**
	 * @return the predicates contained in this predicate set
	 * @throws RodinDBException if there was a problem accessing the database
	 * @deprecated use <code>getPredicates(IProgressMonitor)</code> instead
	 */
	@Deprecated
	IPOPredicate[] getPredicates() throws RodinDBException;
	
	/**
	 * Returns the predicates contained in this predicate set
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the predicates contained in this predicate set
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	IPOPredicate[] getPredicates(IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the parent predicate set of this predicate set, 
	 * or <code>null</code> if this is the root set.
	 * 
	 * @return the parent predicate set, or <code>null</code>
	 * @throws RodinDBException if there was a problem accessing the database
	 * @deprecated use <code>getParentPredicateSet(IProgressMonitor)</code> instead
	 */
	@Deprecated
	IPOPredicateSet getParentPredicateSet() throws RodinDBException;
	
	/**
	 * Returns the parent predicate set of this predicate set, 
	 * or <code>null</code> if this is the root set.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the parent predicate set, or <code>null</code>
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	IPOPredicateSet getParentPredicateSet(IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Sets the parent predicate set of this predicate set. The name must be anon-empty string.
	 * @param name the name of the predicate set
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	void setParentPredicateSet(String name, IProgressMonitor monitor) throws RodinDBException;
}
