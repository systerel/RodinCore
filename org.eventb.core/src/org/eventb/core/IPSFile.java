/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;


/**
 * Common protocol for Event-B Proof status files.
 * <p>
 * The PS file records the status of the proof (in the PR file) each proof 
 * obligation (in the PO file)
 * </p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @author Farhad Mehta
 *
 */
public interface IPSFile extends IRodinFile{

	IFileElementType ELEMENT_TYPE =
		RodinCore.getFileElementType(EventBPlugin.PLUGIN_ID + ".psFile"); //$NON-NLS-1$
	
	/**
	 * Returns a handle to the unchecked version of the context for which this
	 * proof status file has been generated.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the unchecked version of the corresponding context
	 */
	IContextFile getContext();

	/**
	 * Returns a handle to the unchecked version of the machine for which this
	 * proof status file has been generated.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the unchecked version of the corresponding machine
	 */
	IMachineFile getMachine();

	/**
	 * Returns a handle to PO file containing proof obligations for this
	 * component.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the PO file of this component
	 */
	IPOFile getPOFile();
	
	/**
	 * Returns a handle to the PR file containing proofs for this
	 * component.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the PR file of this component
	 */
	IPRFile getPRFile();
	
	IPSstatus[] getStatus() throws RodinDBException;
	
	IPSstatus getStatusOf(String name) throws RodinDBException;
		
}