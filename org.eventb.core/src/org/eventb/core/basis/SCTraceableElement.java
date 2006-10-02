/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ITraceableElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class SCTraceableElement extends EventBElement implements
		ITraceableElement {

	public SCTraceableElement(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ITraceableElement#setSource(org.rodinp.core.IRodinElement)
	 */
	public void setSource(IRodinElement source, IProgressMonitor monitor) throws RodinDBException {
		CommonAttributesUtil.setSource(this, source, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ITraceableElement#getSource()
	 */
	public IRodinElement getSource(IProgressMonitor monitor) throws RodinDBException {
		return CommonAttributesUtil.getSource(this, monitor);
	}

}
