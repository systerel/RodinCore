/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import java.util.ArrayList;

import org.eventb.core.IAxiom;
import org.eventb.core.ISCAxiomSet;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.UnnamedInternalElement;

/**
 * @author halstefa
 */
public class SCAxiomSet extends UnnamedInternalElement implements ISCAxiomSet {

	public SCAxiomSet(String type, IRodinElement parent) {
		super(type, parent);
	}


	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

	public IAxiom[] getAxioms() throws RodinDBException {
		ArrayList<IRodinElement> list = getChildrenOfType(IAxiom.ELEMENT_TYPE);
		IAxiom[] axioms = new Axiom[list.size()];
		list.toArray(axioms);
		return axioms; 
	}
	
}
