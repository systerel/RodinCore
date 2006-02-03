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
import org.rodinp.core.basis.InternalElement;

/**
 * Implementation of Event-B SC axiom set as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>ISCAxiomSet</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 */
public class SCAxiomSet extends InternalElement implements ISCAxiomSet {

	public SCAxiomSet(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

	public IAxiom[] getAxioms() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IAxiom.ELEMENT_TYPE);
		IAxiom[] axioms = new Axiom[list.size()];
		list.toArray(axioms);
		return axioms; 
	}
	
}
