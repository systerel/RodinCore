/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - updated Javadoc
 *     Systerel - separation of file and root element
 ******************************************************************************/
package org.eventb.core;

import org.eventb.core.pm.IProofManager;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B Proof (PR) files. A PR file is composed of proof
 * elements (IPRProof) in a particular order.
 * <p>
 * Clients should use the Proof Manager API rather than direct access to this
 * Rodin database API.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IProofManager
 * 
 * @author Farhad Mehta
 * @since 1.0
 */
public interface IPRRoot extends IEventBRoot{

	IInternalElementType<IPRRoot> ELEMENT_TYPE = RodinCore
			.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prFile"); //$NON-NLS-1$

	/**
	 * Returns handles to all proof elements in this PR file in the order in
	 * which they occur.
	 * 
	 * @return an array of all proof elements in this PR file
	 * @throws RodinDBException
	 */
	IPRProof[] getProofs() throws RodinDBException;

	/**
	 * Returns a handle to the proof element with the given element name.
	 * <p>
	 * This is a handle-only method. The proof element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param name
	 *            element name of the proof
	 * @return a handle to a proof with the given element name
	 */
	IPRProof getProof(String name);

}
