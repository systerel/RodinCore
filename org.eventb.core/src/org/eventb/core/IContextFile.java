/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B (unchecked) contexts.
 * <p>
 * A context has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}.
 * </p>
 * <p>
 * The elements contained in an event-B context are:
 * <ul>
 * <li>extends clauses (<code>IExtendsContext</code>)</li>
 * <li>carrier sets (<code>ICarrierSet</code>)</li>
 * <li>constants (<code>IConstant</code>)</li>
 * <li>axioms (<code>IAxiom</code>)</li>
 * <li>theorems (<code>ITheorem</code>)</li>
 * </ul>
 * </p>
 * <p>
 * In addition to access methods for children elements, also access methods for
 * related file handles are provided.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * 
 * @author Laurent Voisin
 */
public interface IContextFile extends IRodinFile {

	IFileElementType ELEMENT_TYPE = 
		RodinCore.getFileElementType(EventBPlugin.PLUGIN_ID + ".contextFile"); //$NON-NLS-1$

	/**
	 * Returns a handle to the checked version of this context, that is the file
	 * produced when statically checking this context.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the checked version of this context
	 */
	ISCContextFile getSCContextFile();

	/**
	 * Returns a handle to the file containing proof obligations for this
	 * context.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the PO file of this context
	 */
	IPOFile getPOFile();

	/**
	 * Returns a handle to the file containing proofs for this context.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the proof file of this context
	 */
	IPSFile getPRFile();

	/**
	 * Returns an array of all extends clauses of this context.
	 * @return an array of extends clauses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getExtendsClauses(IProgressMonitor)</code> instead
	 */
	@Deprecated
	IExtendsContext[] getExtendsClauses() throws RodinDBException;

	/**
	 * Returns an array of all extends clauses of this context.
	 * @param monitor 
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @return an array of extends clauses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IExtendsContext[] getExtendsClauses(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns an array containing all carrier sets of this context.
	 * 
	 * @return an array of carrier sets
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getCarrierSets(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ICarrierSet[] getCarrierSets() throws RodinDBException;

	/**
	 * Returns an array containing all carrier sets of this context.
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @return an array of carrier sets
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ICarrierSet[] getCarrierSets(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns an array containing all constants of this context.
	 * 
	 * @return an array of constants
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getConstants(IProgressMonitor)</code> instead
	 */
	@Deprecated
	IConstant[] getConstants() throws RodinDBException;

	/**
	 * Returns an array containing all constants of this context.
	 * @param monitor 
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @return an array of constants
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IConstant[] getConstants(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns an array containing all axioms of this context.
	 * 
	 * @return an array of axioms
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getAxioms(IProgressMonitor)</code> instead
	 */
	@Deprecated
	IAxiom[] getAxioms() throws RodinDBException;

	/**
	 * Returns an array containing all axioms of this context.
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @return an array of axioms
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IAxiom[] getAxioms(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns an array containing all theorems of this context.
	 * 
	 * @return an array of theorems
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getTheorems(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ITheorem[] getTheorems() throws RodinDBException;

	/**
	 * Returns an array containing all theorems of this context.
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @return an array of theorems
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ITheorem[] getTheorems(IProgressMonitor monitor) throws RodinDBException;

}
