/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
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
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 */
public interface IMachine extends IRodinFile {
	
	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".machine"; //$NON-NLS-1$
	
	/**
	 * Returns a handle to the checked version of this machine, that is the file
	 * produced when statically checking this machine.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the checked version of this machine
	 */
	ISCMachine getCheckedMachine();

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

	IVariable[] getVariables() throws RodinDBException;
	ITheorem[] getTheorems() throws RodinDBException;
	IInvariant[] getInvariants() throws RodinDBException;
	IEvent[] getEvents() throws RodinDBException;
	ISees[] getSees() throws RodinDBException;
	
	/**
	 * Returns an array of handles to the contexts seen by this machine.
	 * <p>
	 * The handles returned correspond to the checked version of the context.
	 * </p>
	 * 
	 * @return an array of handles to the contexts seen by this machine.
	 */
	ISCContext[] getSeenContexts() throws RodinDBException;
	
}
