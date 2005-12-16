/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import java.util.ArrayList;

import org.eventb.core.IInvariant;
import org.eventb.core.ISCInvariantSet;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author halstefa
 *
 */
public class SCInvariantSet extends InternalElement implements ISCInvariantSet {

	public SCInvariantSet(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

	public IInvariant[] getInvariants() throws RodinDBException {
		ArrayList<IRodinElement> list = getChildrenOfType(IInvariant.ELEMENT_TYPE);
		IInvariant[] invariants = new Invariant[list.size()];
		list.toArray(invariants);
		return invariants; 
	}
}
