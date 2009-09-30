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
 * Common protocol for proof obligations in Event-B Proof Obligation (PO) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * A sequent is a tuple (NAME, TYPE_ENV, HYP, GOAL, HINTS) consists of
 * <ul>
 * <li> a name (<code>String</code>)</li>
 * <li> a hypothesis (<code>IPOPredicateSet</code>)</li>
 * <li> a goal (<code>IPOPredicate</code>)</li>
 * <li> some hints (<code>IPOHint[]</code>)</li>
 * <li> some handles to the source from which this proof obligation is derived (<code>IPOSource[]</code>)</li>
 * </ul>
 * </p>
 *
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface IPOSequent extends IInternalElement, IPOStampedElement, IAccuracyElement {
	
	IInternalElementType<IPOSequent> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".poSequent"); //$NON-NLS-1$
	
	@Deprecated
	String getName();
	
	@Deprecated
	IPOIdentifier[] getIdentifiers() throws RodinDBException;

	/**
	 * Returns a handle to a child hypothesis (predicate set) with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the hypthesis
	 * @return a handle to a child hypthesis with the given element name
	 */
	IPOPredicateSet getHypothesis(String elementName);
	
	/**
	 * Returns the predicate set containing the hypothesis of this proof obligation
	 * @return the predicate set containing the hypothesis of this proof obligation
	 * @throws RodinDBException if there was a problem accessing the database
	 * @deprecated use <code>getHypotheses()</code> instead
	 */
	@Deprecated
	IPOPredicateSet getHypothesis() throws RodinDBException;
	
	/**
	 * Returns the predicate sets containing the hypotheses of this proof
	 * obligation. In the current version, only one predicate set is stored by
	 * the POG. Tools can then consider it as an error if the returned array has
	 * a length different from 1.
	 * 
	 * @return an array of predicate sets containing the hypotheses of this
	 *         proof obligation
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IPOPredicateSet[] getHypotheses() throws RodinDBException;

	/**
	 * Returns a handle to a child goal (predicate) with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the goal
	 * @return a handle to a child goal with the given element name
	 */
	IPOPredicate getGoal(String elementName);

	/**
	 * Returns the goal predicate of this proof obligation
	 * 
	 * @return the goal predicate of this proof obligation
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getGoals()</code> instead
	 */
	@Deprecated
	IPOPredicate getGoal() throws RodinDBException;
	
	/**
	 * Returns the goal predicates of this proof obligation. In the current
	 * version, only one goal predicate is stored by the POG. Tools can then
	 * consider it as an error if the returned array has a length different from
	 * 1.
	 * 
	 * @return an array of goal predicates of this proof obligation
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IPOPredicate[] getGoals() throws RodinDBException;

	/**
	 * Returns a more descriptive name of this proof obligation.
	 * 
	 * @return a descriptive proof obligation name
	 */
	String getDescription() throws RodinDBException;

	/**
	 * Sets the descriptive name of this proof obligation.
	 * 
	 * @param description the descriptive name
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	void setDescription(String description, IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns a handle to a child source with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the source
	 * @return a handle to a child source with the given element name
	 */
	IPOSource getSource(String elementName);

	/**
	 * Returns the (most important) source elements of a proof obligation.
	 * <p>
	 * The returned elements contain handle identifiers to elements of the
	 * database.
	 * </p>
	 * 
	 * @return the array of sources associated with the proof obligation that
	 *         contains this description
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IPOSource[] getSources() throws RodinDBException;

	/**
	 * Returns a handle to a child hint with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the hint
	 * @return a handle to a child hint with the given element name
	 */
	IPOSelectionHint getSelectionHint(String elementName);

	/**
	 * Returns the hints for a proof obligation.
	 * 
	 * @return the array of hints associated with the proof obligation that
	 *         contains this description
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IPOSelectionHint[] getSelectionHints() throws RodinDBException;

}
