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
 */
public interface IPOSequent extends IInternalElement {
	
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".poSequent"); //$NON-NLS-1$
	
	@Deprecated
	String getName();
	
	@Deprecated
	IPOIdentifier[] getIdentifiers() throws RodinDBException;
	
	/**
	 * Returns the predicate set containing the hypothesis of this proof obligation
	 * 
	 * @return the predicate set containing the hypothesis of this proof obligation
	 * @throws RodinDBException if there was a problem accessing the database
	 * @deprecated use <code>getHypothesis(IProgressMonitor)</code> instead
	 */
	@Deprecated
	IPOPredicateSet getHypothesis() throws RodinDBException;
	
	/**
	 * Returns the predicate set containing the hypothesis of this proof obligation
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @return the predicate set containing the hypothesis of this proof obligation
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	IPOPredicateSet getHypothesis(IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the goal predicate of this proof obligation
	 * 
	 * @return the goal predicate of this proof obligation
	 * @throws RodinDBException if there was a problem accessing the database
	 * @deprecated use <code>getGoal(IProgressMonitor)</code> instead
	 */
	@Deprecated
	IPOPredicate getGoal() throws RodinDBException;
	
	/**
	 * Returns the goal predicate of this proof obligation
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the goal predicate of this proof obligation
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	IPOPredicate getGoal(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns a more descriptive name of this proof obligation.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return a descriptive proof obligation name
	 */
	String getDescription(IProgressMonitor monitor) throws RodinDBException;

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
	 * @deprecated use <code>getSources(IProgressMonitor)</code> instead
	 */
	@Deprecated
	IPOSource[] getSources() throws RodinDBException;

	/**
	 * Returns the (most important) source elements of a proof obligation.
	 * <p>
	 * The returned elements contain handle identifiers to elements of the
	 * database.
	 * </p>
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the array of sources associated with the proof obligation that
	 *         contains this description
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IPOSource[] getSources(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns the hints for a proof obligation.
	 * 
	 * @return the array of hints associated with the proof obligation that
	 *         contains this description
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getHints(IProgressMonitor)</code> instead
	 */
	@Deprecated
	IPOHint[] getHints() throws RodinDBException;

	/**
	 * Returns the hints for a proof obligation.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the array of hints associated with the proof obligation that
	 *         contains this description
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IPOHint[] getHints(IProgressMonitor monitor) throws RodinDBException;

}
