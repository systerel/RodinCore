/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.resources.IFile;
import org.eventb.core.ISCContext;
import org.rodinp.core.IRodinElement;

/**
 * @author halstefa
 *
 */
public class SCContext extends Context implements ISCContext {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCContext(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ISCContext.ELEMENT_TYPE;
	}

}
