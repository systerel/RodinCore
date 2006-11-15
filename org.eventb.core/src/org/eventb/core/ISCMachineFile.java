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
 * Common protocol for Event-B statically checked (SC) machine files.
 * <p>
 * An SC machine file has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}.
 * </p>
 * The elements contained in an event-B SC machine file are:
 * <ul>
 * <li>at most one refines clause (<code>ISCRefinesMachine</code>)</li>
 * <li>internal contexts (<code>ISCInternalContext</code>)</li>
 * <li>variables (<code>ISCVariable</code>)</li>
 * <li>invariants (<code>ISCInvariant</code>)</li>
 * <li>theorems (<code>ISCTheorem</code>)</li>
 * <li>events (<code>ISCEvent</code>)</li>
 * <li>at most one variant (<code>ISCVariant</code>)</li>
 * </ul>
 * </p>
 * <p>
 * The internal contexts are a local copy of the contents of the contexts seen,
 * directly or indirectly, by this machine. The other child elements of this
 * machine are the SC versions of the elements of the unchecked version of this
 * machine.
 * In addition, access methods for related file handles are provided.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * 
 * @author Stefan Hallerstede
 */
public interface ISCMachineFile extends IRodinFile {

	IFileElementType ELEMENT_TYPE = 
		RodinCore.getFileElementType(EventBPlugin.PLUGIN_ID + ".scMachineFile"); //$NON-NLS-1$

	/**
	 * Returns a handle to the unchecked version of this machine.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the unchecked version of this machine
	 */
	IMachineFile getMachineFile();

	/**
	 * Returns a handle to the statically checked version of the abstraction of
	 * this machine or <code>null</code> if there is no abstraction (case of a
	 * top level machine).
	 * 
	 * @return a handle to the statically checked version of the abstraction, or
	 *         <code>null</code>
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getAbstractSCMachines(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCMachineFile getAbstractSCMachine() throws RodinDBException;

	/**
	 * Returns a handle to the statically checked version of the abstraction of
	 * this machine or <code>null</code> if there is no abstraction (case of a
	 * top level machine).
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return a handle to the statically checked version of the abstraction, or
	 *         <code>null</code>
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getAbstractSCMachines(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCMachineFile getAbstractSCMachine(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns a handle to the statically checked versions of the abstractions of
	 * this SC machine.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return a array of handles to the statically checked versions of the abstractions
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCMachineFile[] getAbstractSCMachines(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns an array of all SC refines clauses of this SC machine.
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of SC refines clauses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCRefinesMachine[] getSCRefinesClauses(IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the internal SC contexts that are (transitively) seen by this SC
	 * machine.
	 * 
	 * @return an array of all internal contexts
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getSCInternalContexts(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCInternalContext[] getSCSeenContexts() throws RodinDBException;

	/**
	 * Returns the internal SC contexts that are (transitively) seen by this SC
	 * machine.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of all internal contexts
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCInternalContext[] getSCSeenContexts(IProgressMonitor monitor) 
	throws RodinDBException;

	/**
	 * Returns an array containing all SC variables of this SC machine.
	 * 
	 * @return an array of all SC variables
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getSCVariables(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCVariable[] getSCVariables() throws RodinDBException;

	/**
	 * Returns an array containing all SC variables of this SC machine.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of all SC variables
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCVariable[] getSCVariables(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns an array containing all SC invariants of this SC machine.
	 * 
	 * @return an array of all SC invariants
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getSCInvariants(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCInvariant[] getSCInvariants() throws RodinDBException;

	/**
	 * Returns an array containing all SC invariants of this SC machine.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of all SC invariants
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCInvariant[] getSCInvariants(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns an array containing all SC theorems of this SC machine.
	 * 
	 * @return an array of all SC theorems
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getSCTheorems(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCTheorem[] getSCTheorems() throws RodinDBException;

	/**
	 * Returns an array containing all SC theorems of this SC machine.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return an array of all SC theorems
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCTheorem[] getSCTheorems(IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the array containing all SC events of this SC machine.
	 * 
	 * @return the array of all SC events
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getSCEvents(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCEvent[] getSCEvents() throws RodinDBException;
	
	/**
	 * Returns the array containing all SC events of this SC machine.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the array of all SC events
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCEvent[] getSCEvents(IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the handle of the SC variant of this SC machine.
	 * 
	 * @return the handle of the SC variant
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getSCVariants(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCVariant getSCVariant() throws RodinDBException;
	
	/**
	 * Returns the handle of the SC variant of this SC machine.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the handle of the SC variant
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 * @deprecated use <code>getSCVariants(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCVariant getSCVariant(IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the array containing all SC variants of this SC machine.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the array of all SC variants
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCVariant[] getSCVariants(IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the handle of the refines clause of this SC machine.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the handle of the refines clause
	 * @throws RodinDBException if there was a problem accessing the database
	 * @deprecated use <code>getSCRefinesClause(IProgressMonitor)</code> instead
	 */
	@Deprecated
	ISCRefinesMachine getRefinesClause(IProgressMonitor monitor) throws RodinDBException;

}
