/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;


/**
 * Common protocol for Event-B Proof Status (PS) files.
 * <p>
 * The PS file records the status of the proof (in the PR file) of each proof 
 * obligation (in the PO file). In order to be consistent with the PO and PR files, 
 * it must:
 * <ul>
 * <li> Contain one proof status element for each proof obligation in the PO file. These 
 * proof status elements and proof obligations must share the same names, and occur in the 
 * same order.
 * <li> Each proof status element must faithfully record the status of the proof in the PR file
 * associated to it (i.e. with the same element name)
 * </ul>
 * </p>
 * <p>
 * The convention used for associating proof obligations (IPOSequent) in the PO file to 
 * proof status elements (IPSStatus) in the PS file, and proofs (in the PR file) is that 
 * they all have the identical element names.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @author Farhad Mehta
 *
 * @since 1.0
 */
public interface IPSRoot extends IEventBRoot, IPOStampedElement {

	IInternalElementType<IPSRoot> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".psFile"); //$NON-NLS-1$
	
	/**
	 * Returns handles to all proof status elements in the PS file in the 
	 * order in which they occur.
	 * 
	 * @return an array of all status elements in the PS file
	 * @throws RodinDBException 
	 */
	IPSStatus[] getStatuses() throws RodinDBException;

	/**
	 * Returns a handle to the status element with the given element name.
	 * <p>
	 * This is a handle-only method. The status element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param name
	 *            name of the status element 
	 * @return a handle to a status element with the given name
	 */
	IPSStatus getStatus(String name);
		
}
