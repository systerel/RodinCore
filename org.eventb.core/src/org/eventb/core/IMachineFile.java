/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B (unchecked) machines.
 * <p>
 * A machine has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}.
 * </p>
 * <p>
 * The elements contained in an event-B machine are:
 * <ul>
 * <li>at most one refines clause (<code>IRefinesMachine</code>)</li>
 * <li>sees clauses (<code>ISeesContext</code>)</li>
 * <li>variables (<code>IVariable</code>)</li>
 * <li>invariants (<code>IInvariant</code>)</li>
 * <li>theorems (<code>ITheorem</code>)</li>
 * <li>events (<code>IEvent</code>)</li>
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
public interface IMachineFile extends IRodinFile {

	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".machineFile"; //$NON-NLS-1$

	/**
	 * Returns a handle to the checked version of this machine, that is the file
	 * produced when statically checking this machine.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the checked version of this machine
	 */
	ISCMachineFile getSCMachineFile();

	/**
	 * Returns a handle to the file containing proof obligations for this
	 * machine.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the PO file of this machine
	 */
	IPOFile getPOFile();

	/**
	 * Returns a handle to the file containing proofs for this machine.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the proof file of this machine
	 */
	IPRFile getPRFile();

	/**
	 * Returns the refines clause of this machine or <code>null</code> if this
	 * machine does not have an abstraction.
	 * 
	 * @return the refines clause of this machine or <code>null</code>
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IRefinesMachine getRefinesClause() throws RodinDBException;

	/**
	 * Returns an array of all sees clauses of this machine.
	 * 
	 * @return an array of sees clauses
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISeesContext[] getSeesClauses() throws RodinDBException;

	/**
	 * Returns an array containing all (global) variables of this machine.
	 * 
	 * @return an array of variables
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IVariable[] getVariables() throws RodinDBException;

	/**
	 * Returns an array containing all invariants of this machine.
	 * 
	 * @return an array of invariants
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IInvariant[] getInvariants() throws RodinDBException;

	/**
	 * Returns an array containing all theorems of this machine.
	 * 
	 * @return an array of theorems
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ITheorem[] getTheorems() throws RodinDBException;

	/**
	 * Returns an array containing all events of this machine.
	 * 
	 * @return an array of events
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IEvent[] getEvents() throws RodinDBException;
	
	/**
	 * Returns a handle to the variant of this machine.
	 * 
	 * @return a handle to the variant
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	IVariant getVariant() throws RodinDBException;

}
