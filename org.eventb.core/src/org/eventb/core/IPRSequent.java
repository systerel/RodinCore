/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;


import org.eventb.core.prover.IProofTree;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for proof obligations and their status in Event-B Prover (PR) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @author Stefan Hallerstede
 * @author Farhad Mehta
 *
 */
public interface IPRSequent extends IPOSequent {
	
	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".prSequent"; //$NON-NLS-1$
	
	boolean isDischarged() throws RodinDBException;

	IProofTree makeProofTree() throws RodinDBException;
	IProof getProof() throws RodinDBException;
	void updateStatus(IProofTree pt) throws RodinDBException;

}
