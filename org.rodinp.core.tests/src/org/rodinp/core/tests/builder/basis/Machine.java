/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.builder.basis;

import org.eclipse.core.resources.IFile;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.tests.builder.IMachine;
import org.rodinp.core.tests.builder.IReference;
import org.rodinp.core.tests.builder.ISCMachine;

/**
 * @author Stefan Hallerstede
 *
 */
public class Machine extends Component implements IMachine {

	public Machine(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.basis.RodinElement#getElementType()
	 */
	@Override
	public IFileElementType<IMachine> getElementType() {
		return ELEMENT_TYPE;
	}

	public ISCMachine getCheckedVersion() {
		return (ISCMachine) getAlternateVersion("msc");
	}

	public ISCMachine getReferencedMachine() throws RodinDBException {
		IReference[] refs = this.getChildrenOfType(IReference.ELEMENT_TYPE);
		
		assert refs.length <= 1;
		
		if (refs.length == 1) {
			final String depName = refs[0].getElementName();
			final IRodinProject rodinProject = getRodinProject();
			return (ISCMachine) rodinProject.getRodinFile(depName + ".msc");
		}
		return null;
	}

}
