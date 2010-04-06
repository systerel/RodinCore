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
 * A predicate set consists of predicates <code>getPredicates()</code> and
 * perhaps a predicate set <code>getParentPredicateSet()</code>. Predicate
 * sets are stored in a tree structure. A predicate set without a parent
 * predicate set is the root of the tree.
 * </p>
 * <p>
 * References to predicate sets are stored relative to plain PO files and
 * translated back and forth, see {@link IPORoot}.
 * </p>
 * 
 * @author Stefan Hallerstede
 * 
 * @since 1.0
 */
public interface IPOPredicateSet extends IInternalElement, IPOStampedElement {
	
	IInternalElementType<IPOPredicateSet> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".poPredicateSet"); //$NON-NLS-1$

	/**
	 * Returns a handle to a child identifier with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the identifier
	 * @return a handle to a child identifier with the given element name
	 */
	IPOIdentifier getIdentifier(String elementName);
	
	/**
	 * Returns a list of typed identifiers <code>IPOIdentifier</code> that constitutes a type
	 * environment for all predicates in this predicate set and all predicate sets that are 
	 * below this predicate set in the predicate set tree.
	 * 
	 * @return a list of typed identifiers
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	IPOIdentifier[] getIdentifiers() throws RodinDBException;

	/**
	 * Returns a handle to a child predicate with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the predicate
	 * @return a handle to a child predicate with the given element name
	 */
	IPOPredicate getPredicate(String elementName);

	/**
	 * Returns the predicates contained in this predicate set
	 * 
	 * @return the predicates contained in this predicate set
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	IPOPredicate[] getPredicates() throws RodinDBException;
	
	/**
	 * Sets the parent predicate set of this predicate set.
	 * @param predicateSet the predicate set; 
	 * 		if it is <code>null</code> this predicate set is turned into a root predicate set
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	void setParentPredicateSet(IPOPredicateSet predicateSet, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the parent predicate set of this predicate set, 
	 * or <code>null</code> if this is the root set.
	 * 
	 * @return the parent predicate set, or <code>null</code>
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	IPOPredicateSet getParentPredicateSet() throws RodinDBException;
	
	/**
	 * Returns the name of the parent predicate set of this predicate set, 
	 * or <code>null</code> if this is the root set.
	 * 
	 * @return the name of the parent predicate set, or <code>null</code>
	 * @throws RodinDBException if there was a problem accessing the database
	 * @deprecated use <code>getParentPredicateSet(IProgressMonitor)</code> instead
	 */
	@Deprecated
	String getParentPredicateSetName() throws RodinDBException;
	
	/**
	 * Sets the parent predicate set of this predicate set. The name must be a non-empty string.
	 * @param name the name of the predicate set
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException if there was a problem accessing the database
	 * @deprecated use <code>setParentPredicateSet(IPOPredicateSet,IProgressMonitor)</code> instead
	 */
	@Deprecated
	void setParentPredicateSetName(String name, IProgressMonitor monitor) throws RodinDBException;
}
