/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;


import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for proof obligation status in Event-B Proof status (PS) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Farhad Mehta
 *
 */
public interface IPSstatusRename extends IInternalElement {
	
	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".status"; //$NON-NLS-1$

	/**
	 * Returns the name of this proof obligation in the RODIN database.
	 * 
	 * @return the name of this proof obligation.
	 * 
	 */
	public String getName();
	
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
	
	/**
	 * Returns the IPOSequent associated to this proof obligation from the
	 * RODIN database.
	 * 
	 * @return the IPOSequent associated to this proof obligation from the
	 * RODIN database, or <code>null</code> if none is associated to
	 * this proof obligation (error).
	 * 
	 * @throws RodinDBException
	 */
	IPOSequent getPOSequent() throws RodinDBException;
	
	boolean isProofValid() throws RodinDBException;
	
	// lock po & pr files before calling this method
	public void updateStatus() throws RodinDBException;
	
}
