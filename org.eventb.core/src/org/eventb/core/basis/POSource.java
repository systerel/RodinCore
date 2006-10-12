/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPOSource;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author halstefa
 *
 */
public class POSource extends InternalElement implements IPOSource {
	
	public POSource(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.basis.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	public String getSourceRole(IProgressMonitor monitor) throws RodinDBException {
		return getStringAttribute(EventBAttributes.ROLE_ATTRIBUTE, monitor);
	}
	
	public String getSourceHandleIdentifier() throws RodinDBException {
		return getContents();
	}

	public void setSource(IRodinElement source, IProgressMonitor monitor) throws RodinDBException {
		setContents(source.getHandleIdentifier());
	}

	public IRodinElement getSource(IProgressMonitor monitor) throws RodinDBException {
		return RodinCore.create(getContents());
	}

	public void setSourceRole(String role, IProgressMonitor monitor) throws RodinDBException {
		setStringAttribute(EventBAttributes.ROLE_ATTRIBUTE, role, monitor);
	}

}
