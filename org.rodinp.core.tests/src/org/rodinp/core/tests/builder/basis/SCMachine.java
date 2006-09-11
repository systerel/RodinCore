/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.builder.basis;

import org.eclipse.core.resources.IFile;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.tests.builder.IMachine;
import org.rodinp.core.tests.builder.ISCMachine;

/**
 * @author Stefan Hallerstede
 *
 */
public class SCMachine extends Component implements ISCMachine {

	public SCMachine(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.basis.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.tests.builder.ISCMachine#getUncheckedVersion()
	 */
	public IMachine getUncheckedVersion() {
		return (Machine) getAlternateVersion("mch");
	}

}
