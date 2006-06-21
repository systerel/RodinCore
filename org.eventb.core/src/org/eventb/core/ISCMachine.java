/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B statically checked (SC) machine.
 * <p>
 * There are two kinds of such machines:
 * <ul>
 * <li>{@link org.eventb.core.ISCMachineFile} is a statically checked machine
 * that corresponds directly to a machine file
 * {@link org.eventb.core.IMachineFile}</li>
 * <li>{@link org.eventb.core.ISCInternalMachine} is a statically checked
 * machine that is stored inside another statically checked machine. It is
 * usually a copy of an {@link org.eventb.core.ISCMachineFile}.</li>
 * <ul>
 * </p>
 * <p>
 * Note, that SC events, variants and witnesses are not contained in the common
 * protocol. This is because these elements are specific to a certain level of
 * refinement and are not transitively inherited by the refining machine.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
public interface ISCMachine extends IRodinElement {

	/**
	 * Returns an array containing all SC variables of this SC machine.
	 * 
	 * @return an array of all SC variables
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCVariable[] getSCVariables() throws RodinDBException;

	/**
	 * Returns an array containing all SC invariants of this SC machine.
	 * 
	 * @return an array of all SC invariants
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCInvariant[] getSCInvariants() throws RodinDBException;

	/**
	 * Returns an array containing all SC theorems of this SC machine.
	 * 
	 * @return an array of all SC theorems
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	ISCTheorem[] getSCTheorems() throws RodinDBException;

}
