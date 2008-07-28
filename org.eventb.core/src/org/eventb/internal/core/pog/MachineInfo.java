/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eventb.core.ISCMachineFile;
import org.eventb.core.pog.state.IMachineInfo;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineInfo extends State implements IMachineInfo {

	@Override
	public String toString() {
		return abstractMachine.getComponentName();
	}

	private final ISCMachineFile abstractMachine;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.state.IMachineInfo#getAbstractMachine()
	 */
	public ISCMachineFile getAbstractMachine() {
		return abstractMachine;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.state.IMachineInfo#isInitialMachine()
	 */
	public boolean isInitialMachine() {
		return abstractMachine == null;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.tool.state.IState#getStateType()
	 */
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	public MachineInfo(final ISCMachineFile abstractMachine) {
		super();
		this.abstractMachine = abstractMachine;
	}

}
