/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IWitness;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B witnesses as an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>IWitness</code>.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class Witness extends ExpressionElement implements IWitness {

	public Witness(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ILabeledElement#setLabel(java.lang.String)
	 */
	public void setLabel(String label) throws RodinDBException {
		LabeledElementUtil.setLabel(this, label);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ILabeledElement#getLabel()
	 */
	public String getLabel() throws RodinDBException {
		return LabeledElementUtil.getLabel(this);
	}

}
