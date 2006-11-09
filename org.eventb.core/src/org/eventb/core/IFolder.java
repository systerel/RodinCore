/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;


import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * Common protocol for a folder in Event-B (Proof and Proof Status) files.
 * <p>
 * This is an internal element meant to serve as a container for other internal elements
 * that are no longer used by the system, but may be interesting to be reused in the future.
 * </p>
 * <p>
 * The structure of the internal element is not fixed and depends on where it occurs. For 
 * instance a trash folder at the level of a proof file may store old user generated proofs
 * </p>
 * 
 * @author Farhad Mehta
 */
public interface IFolder extends IInternalElement {
	
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".folder"); //$NON-NLS-1$

}
