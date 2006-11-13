/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B statically checked (SC) context files.
 * <p>
 * There are two kinds of such contexts:
 * <ul>
 * <li>{@link org.eventb.core.ISCContextFile} is a statically checked context
 * that corresponds directly to a context file
 * {@link org.eventb.core.IContextFile}</li>
 * <li>{@link org.eventb.core.ISCInternalContext} is a statically checked
 * context that is stored inside another statically checked context or
 * statically checked machine. It is usually a copy of an
 * {@link org.eventb.core.ISCContextFile}.</li>
 * <ul>
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
public interface ISCContext extends IRodinElement {

	/**
	 * Returns an array containing all SC carrier sets of this SC context.
	 * 
	 * @return an array of all SC carrier sets
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getSCCarrierSets(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCCarrierSet[] getSCCarrierSets() throws RodinDBException;

	/**
	 * Returns an array containing all SC carrier sets of this SC context.
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of all SC carrier sets
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCCarrierSet[] getSCCarrierSets(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns an array containing all SC constants of this SC context.
	 * 
	 * @return an array of all SC constants
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getSCConstants(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCConstant[] getSCConstants() throws RodinDBException;

	/**
	 * Returns an array containing all SC constants of this SC context.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of all SC constants
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCConstant[] getSCConstants(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns an array containing all SC axioms of this SC context.
	 * 
	 * @return an array of all SC axioms
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getSCAxioms(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCAxiom[] getSCAxioms() throws RodinDBException;

	/**
	 * Returns an array containing all SC axioms of this SC context.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of all SC axioms
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCAxiom[] getSCAxioms(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns an array containing all SC theorems of this SC context.
	 * 
	 * @return an array of all SC theorems
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getSCTheorems(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCTheorem[] getSCTheorems() throws RodinDBException;

	/**
	 * Returns an array containing all SC theorems of this SC context.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of all SC theorems
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCTheorem[] getSCTheorems(IProgressMonitor monitor) throws RodinDBException;
	
}
