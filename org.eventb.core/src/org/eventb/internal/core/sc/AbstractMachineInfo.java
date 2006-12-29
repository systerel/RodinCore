/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import org.eventb.core.ISCMachineFile;
import org.eventb.core.sc.state.IAbstractMachineInfo;
import org.eventb.internal.core.tool.state.ToolState;

/**
 * @author Stefan Hallerstede
 *
 */
public class AbstractMachineInfo extends ToolState implements
		IAbstractMachineInfo {
	
	private ISCMachineFile machineFile;

	public AbstractMachineInfo(ISCMachineFile machineFile) {
		this.machineFile = machineFile;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.state.IAbstractMachineInfo#getAbstractMachine()
	 */
	public ISCMachineFile getAbstractMachine() {
		return machineFile;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.tool.state.IToolState#getStateType()
	 */
	public String getStateType() {
		return STATE_TYPE;
	}

}
