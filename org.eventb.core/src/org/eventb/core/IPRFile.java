/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import java.util.Map;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;



/**
 * Common protocol for Event-B Prover (PR) files.
 * <p>
 * The structure of the PR file is identical to that if the proof obligation (PO)
 * file, but will additional proof trees included. 
 * </p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @author Stefan Hallerstede
 * @author Farhad Mehta
 *
 */
public interface IPRFile extends IRodinFile{

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
	 * Returns a handle to the snapshot of file containing proof obligations for this
	 * component.
	 * <p>
	 * This is a handle-only operation.
	 * </p>
	 * 
	 * @return a handle to snapshot of the PO file of this component
	 */
	IPOFile getPOFile();
	
	IPRSequent[] getSequents() throws RodinDBException;
	
	IPRSequent getSequent(String name) throws RodinDBException;
	
	/**
	 * Returns all the proof trees contained in this PR file, indexed according 
	 * to their name.
	 * <p>
	 * The name of a proof tree is identical to the proof obligation (IPRSequent)
	 * associated to it. In addition
	 * </p>
	 * 
	 * @return map containing all proof trees in this PR file, indexed by name.
	 * 
	 * @throws RodinDBException
	 */
	Map<String,IPRProofTree> getProofTrees() throws RodinDBException;
	
	
	/**
	 * Returns the proof tree with the given name from the PR file.
	 * 
	 * @param name
	 * Name of the proof tree to return.
	 * 
	 * @return the proof tree with the correcponging name, or <code>null</code> if not
	 * present.
	 * 
	 */
	IPRProofTree getProofTree(String name);
	
	/**
	 * Creates and returns a new initialised proof tree with the given name.
	 * 
	 * @param name
	 * 				Name of the proof tree to create.
	 * 
	 * @return the created proof tree
	 * 
	 */
	IPRProofTree createProofTree(String name) throws RodinDBException;
	
}
