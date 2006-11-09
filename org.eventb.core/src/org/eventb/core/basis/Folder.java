/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IFolder;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.InternalElement;

/**
 * Common protocol for a folder in Event-B (Proof and Proof Status) files.
 * <p>
 * This is an internal element meant to serve as a container for other internal elements
 * that are no longer used by the system, but may be interesting to be reused in the future.
 * </p>
 * <p>
 * The structure of the internal element is not fixed and depends on where it occurs. For 
 * instance the trash folder at the level of a proof file may store old user generated proofs
 * </p>
 * 
 * @author Farhad Mehta
 */
public class Folder extends InternalElement implements IFolder {

	public Folder(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}

}

