/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.resources.IFile;
import org.eventb.core.ISCMachine;
import org.rodinp.core.IRodinElement;

/**
 * @author halstefa
 *
 */
public class SCMachine extends Machine implements ISCMachine {

	/**
	 * @param file
	 * @param parent
	 */
	public SCMachine(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ISCMachine.ELEMENT_TYPE;
	}

}
