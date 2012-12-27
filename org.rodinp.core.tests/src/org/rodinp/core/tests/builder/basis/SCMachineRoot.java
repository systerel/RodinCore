/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core.tests.builder.basis;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.tests.builder.IMachineRoot;
import org.rodinp.core.tests.builder.ISCMachineRoot;

public class SCMachineRoot extends ComponentRoot implements ISCMachineRoot {

	public SCMachineRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<ISCMachineRoot> getElementType() {
		return ELEMENT_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.tests.builder.ISCMachine#getUncheckedVersion()
	 */
	public IMachineRoot getUncheckedVersion() {
		return (MachineRoot) getAlternateVersion("mch");
	}

}
