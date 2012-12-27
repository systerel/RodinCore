/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eventb.core.ISCMachineRoot;
import org.eventb.core.pog.state.IMachineInfo;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.rodinp.core.IRodinFile;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineInfo extends State implements IMachineInfo {

	@Override
	public String toString() {
		ISCMachineRoot root = (ISCMachineRoot) abstractMachine.getRoot();
		return root.getComponentName();
	}

	private final IRodinFile abstractMachine;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.state.IMachineInfo#getAbstractMachine()
	 */
	@Override
	public IRodinFile getAbstractMachine() {
		return abstractMachine;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.state.IMachineInfo#isInitialMachine()
	 */
	@Override
	public boolean isInitialMachine() {
		return abstractMachine == null;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.tool.state.IState#getStateType()
	 */
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	public MachineInfo(final IRodinFile abstractMachine) {
		super();
		this.abstractMachine = abstractMachine;
	}

}
