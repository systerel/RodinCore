/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import java.util.Map;

import org.rodinp.core.RodinDBException;



/**
 * Common protocol for Event-B Prover (PR) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @author Stefan Hallerstede
 * @author Farhad Mehta
 *
 */
public interface IPRFile extends IPOFile {

	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".prFile"; //$NON-NLS-1$
	
	/**
	 * Returns a handle to the unchecked version of the context for which this
	 * proof file has been generated.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the unchecked version of the corresponding context
	 */
	IContextFile getContext();

	/**
	 * Returns a handle to the unchecked version of the machine for which this
	 * proof file has been generated.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the unchecked version of the corresponding machine
	 */
	IMachineFile getMachine();

	/**
	 * Returns a handle to the file containing proof obligations for this
	 * component.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to the PO file of this component
	 */
	IPOFile getPOFile();
	
	Map<String,IProof> getProofs() throws RodinDBException;
	
	IProof getProof(String name) throws RodinDBException;
	
}
