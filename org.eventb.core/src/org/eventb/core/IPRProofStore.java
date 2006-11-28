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
 * A proof store gathers together all predicates and expressions used in a
 * persisted proof. The children of this element are:
 * <ul>
 * <li>Identifiers occurring free in a predicate or expression in the proof.</li>
 * <li>Predicates used in the proof.</li>
 * <li>Expressions used in the proof.</li>
 * </ul>
 * Moreover, the store also carries an attribute called
 * <code>org.eventb.core.prSets</code> which contains a list of all carrier
 * sets used in the proof (set names separated by commas).
 * 
 * @author Farhad Mehta
 */
public interface IPRProofStore extends IInternalElement {

	IInternalElementType ELEMENT_TYPE = RodinCore
			.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prProofStore"); //$NON-NLS-1$

	/**
	 * Returns a handle to the identifier child with the given name.
	 * <p>
	 * This is a handle-only method. The identifier element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param name
	 *            name of the child
	 * 
	 * @return a handle to the child identifier with the given name
	 * @see #getIdentifiers()
	 */
	IPRIdentifier getIdentifier(String name);

	/**
	 * Returns all children identifier elements.
	 * 
	 * @return an array of all chidren element of type identifier
	 * @throws RodinDBException
	 * @see #getIdentifier(String)
	 */
	IPRIdentifier[] getIdentifiers() throws RodinDBException;

	/**
	 * Returns a handle to the expression child with the given name.
	 * <p>
	 * This is a handle-only method. The expression element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param name
	 *            element name of the child
	 * 
	 * @return a handle to the child expression with the given element name
	 * @see #getExpressions()
	 */
	IPRStoredExpr getExpression(String name);

	/**
	 * Returns all children expression elements.
	 * 
	 * @return an array of all chidren element of type expression
	 * @throws RodinDBException
	 * @see #getExpression(String)
	 */
	IPRStoredExpr[] getExpressions() throws RodinDBException;

	/**
	 * Returns a handle to the predicate child with the given name.
	 * <p>
	 * This is a handle-only method. The predicate element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param name
	 *            element name of the child
	 * 
	 * @return a handle to the child predicate with the given element name
	 * @see #getPredicates()
	 */
	IPRStoredPred getPredicate(String name);

	/**
	 * Returns all children predicate elements.
	 * 
	 * @return an array of all chidren element of type predicate
	 * @throws RodinDBException
	 * @see #getPredicate(String)
	 */
	IPRStoredPred[] getPredicates() throws RodinDBException;

	/**
	 * Returns the carrier sets that are used in this store.
	 * 
	 * @return an array of the carrier set names of this store
	 * @throws RodinDBException
	 * @see #setSets(String[], IProgressMonitor)
	 */
	String[] getSets() throws RodinDBException;

	/**
	 * Sets the carrier sets that are used in this store.
	 * 
	 * @param sets
	 *            the carrier set names to set
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException
	 * @see #getSets()
	 */
	void setSets(String[] sets, IProgressMonitor monitor)
			throws RodinDBException;

}
