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
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.tests.builder.IMachineRoot;
import org.rodinp.core.tests.builder.IReference;
import org.rodinp.core.tests.builder.ISCMachineRoot;

public class MachineRoot extends ComponentRoot implements IMachineRoot {

	public MachineRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IMachineRoot> getElementType() {
		return ELEMENT_TYPE;
	}

	public ISCMachineRoot getCheckedVersion() {
		return (ISCMachineRoot) getAlternateVersion("msc");
	}

	public ISCMachineRoot getReferencedMachine() throws RodinDBException {
		IReference[] refs = this.getChildrenOfType(IReference.ELEMENT_TYPE);
		
		assert refs.length <= 1;
		
		if (refs.length == 1) {
			final String depName = refs[0].getElementName();
			final IRodinProject rodinProject = getRodinProject();
			final IRodinFile rFile = rodinProject.getRodinFile(depName + ".msc");
			return (ISCMachineRoot) rFile.getRoot();
		}
		return null;
	}

}
