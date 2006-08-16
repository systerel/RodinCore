/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;


import org.eclipse.core.runtime.CoreException;
import org.eventb.core.seqprover.IProofTree;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for proof obligations in Event-B Prover (PR) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Farhad Mehta
 *
 */
public interface IPRSequent extends IPOSequent {
	
	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".prSequent"; //$NON-NLS-1$
	
	@ Deprecated
	boolean isClosed() throws RodinDBException;

	/**
	 * Returns the proof tree associated to this proof obligation from the
	 * RODIN database.
	 * 
	 * @return the proof tree associated to this proof obligation from the
	 * RODIN database, or <code>null</code> if no proof tree is associated to
	 * this proof obligation.
	 * 
	 * @throws RodinDBException
	 */
	IPRProofTree getProofTree() throws RodinDBException;
	
	IProofTree rebuildProofTree() throws RodinDBException;
	IProofTree makeFreshProofTree() throws RodinDBException;
	
	
	@ Deprecated
	boolean proofAttempted() throws RodinDBException;
	boolean isProofBroken() throws RodinDBException;
	
	@ Deprecated
	void setProofBroken(boolean broken) throws RodinDBException;
	
	void updateProofTree(IProofTree pt) throws RodinDBException, CoreException;

}
