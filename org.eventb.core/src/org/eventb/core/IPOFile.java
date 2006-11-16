/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
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
 * Common protocol for Event-B Proof Obligation (PO) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @author Stefan Hallerstede
 *
 */
public interface IPOFile extends IRodinFile {

	public IFileElementType ELEMENT_TYPE =
		RodinCore.getFileElementType(EventBPlugin.PLUGIN_ID + ".poFile"); //$NON-NLS-1$
	
	/**
	 * Returns a handle to the checked version of the context for which this
	 * proof obligation file has been generated.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the checked version of the corresponding context
	 */
	public ISCContextFile getSCContext();

	/**
	 * Returns a handle to the checked version of the machine for which this
	 * proof obligation file has been generated.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the checked version of the corresponding machine
	 */
	public ISCMachineFile getSCMachine();

	/**
	 * Returns a handle to the file containing proofs for this component.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the proof file of this component
	 */
	public IPRFile getPRFile();
	
	/**
	 * Returns a handle to the file containing proof status for this component.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the proof file of this component
	 */
	public IPSFile getPSFile();

	/**
	 * Returns a handle to a child predicate set with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the predicate set
	 * @return a handle to a child predicate set with the given element name
	 */
	public IPOPredicateSet getPredicateSet(String elementName) throws RodinDBException;

	/**
	 * Returns the handles to the predicate sets of this component.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return a handle to the predicate set or <code>null</code> there is no
	 *         predicate set witrh given name
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	public IPOPredicateSet[] getPredicateSets(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns a handle to the predicate set with the given name.
	 * 
	 * @param name
	 *            the element name of the predicate set
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return a handle to the predicate set or <code>null</code> there is no
	 *         predicate set witrh given name
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	@Deprecated
	public IPOPredicateSet getPredicateSet(String name, IProgressMonitor monitor) throws RodinDBException;
	
	@Deprecated
	public IPOIdentifier[] getIdentifiers() throws RodinDBException;
	
	/**
	 * Returns handles to the proof obligations of this component. 
	 * 
	 * @return the array of handles to the proof obligations
	 * @throws RodinDBException if there was a problem accessing the database
	 * @deprecated use <code>getSequents(IProgressMonitor)</code> instead
	 */
	@Deprecated
	public IPOSequent[] getSequents() throws RodinDBException;
	
	/**
	 * Returns handles to the proof obligations of this component. 
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the array of handles to the proof obligations
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	public IPOSequent[] getSequents(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns a handle to a child sequent with the given element name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param elementName
	 *            element name of the sequent
	 * @return a handle to a child sequent with the given element name
	 */
	public IPOSequent getSequent(String elementName) throws RodinDBException;
	
	/**
	 * Returns handle to the proof obligation with the given element name,
	 * and <code>null</code> if no such proof obligation exists in this component
	 * 
	 * @param name the element of the proof obligation
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the array of handles to the proof obligations
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	@Deprecated
	public IPOSequent getSequent(String name, IProgressMonitor monitor) throws RodinDBException;
}
