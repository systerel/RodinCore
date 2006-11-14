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
 * @author Farhad Mehta
 *
 */
public interface IPRFile extends IRodinFile{

	IFileElementType ELEMENT_TYPE = 
		RodinCore.getFileElementType(EventBPlugin.PLUGIN_ID + ".prFile"); //$NON-NLS-1$
	
	
	/**
	 * Returns all the proof trees contained in this PR file.
	 * 
	 * @return an array containing all proof trees in this PR file.
	 * 
	 * @throws RodinDBException
	 */
	IPRProofTree[] getProofTrees() throws RodinDBException;
	
	
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
	 * @param monitor TODO
	 * 
	 * @return the created proof tree
	 * 
	 */
	IPRProofTree createProofTree(String name, IProgressMonitor monitor) throws RodinDBException;
	
}
