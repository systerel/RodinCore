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
 * </ul>
 * </p>
 * <p>
 * The internal contexts are a local copy of the contents of the contexts seen,
 * directly or indirectly, by this machine. The other child elements of this
 * machine are the SC versions of the elements of the unchecked version of this
 * machine. They are manipulated by means of {@link org.eventb.core.ISCMachine}.
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
public interface ISCMachineFile extends IRodinFile, ISCMachine {

	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".scMachineFile"; //$NON-NLS-1$

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
	 */
	ISCMachineFile getAbstractSCMachine() throws RodinDBException;

	/**
	 * Returns the internal SC contexts that are (transitively) seen by this SC
	 * machine.
	 * 
	 * @return an array of all internal contexts
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCInternalContext[] getSCInternalContexts() throws RodinDBException;

	/**
	 * Returns the internal SC machine that are (transitively) refined by this
	 * SC machine.
	 * 
	 * @return the array of internal machines
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	// TODO remove this method?
	ISCInternalMachine[] getSCInternalMachines() throws RodinDBException;

	/**
	 * Returns the array containing all SC events of this SC machine.
	 * 
	 * @return the array of all SC events
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCEvent[] getSCEvents() throws RodinDBException;

}
