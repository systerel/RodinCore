/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.eventb.core.IPOHint;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class POHint extends EventBElement implements IPOHint {

	public POHint(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.basis.RodinElement#getElementType()
	 */
	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}
	
//	public void setHint(String value, IProgressMonitor monitor) throws RodinDBException {
//		setAttributeValue(EventBAttributes.POHINT_ATTRIBUTE, value, monitor);
//	}
//
//	public String getHint(IProgressMonitor monitor) throws RodinDBException {
//		return getAttributeValue(EventBAttributes.POHINT_ATTRIBUTE, monitor);
//	}

}
