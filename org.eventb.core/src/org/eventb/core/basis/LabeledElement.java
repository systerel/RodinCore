/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.ILabeledElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class LabeledElement extends InternalElement implements ILabeledElement {

	public LabeledElement(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ILabeledElement#setLabel(java.lang.String)
	 */
	public void setLabel(String label) throws RodinDBException {
		CommonAttributesUtil.setLabel(this, label);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ILabeledElement#getLabel()
	 */
	public String getLabel() throws RodinDBException {
		return CommonAttributesUtil.getLabel(this);
	}

}
