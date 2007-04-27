/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.sc.state.IAbstractMachineInfo;
import org.eventb.core.tool.state.IStateType;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public class AbstractMachineInfo extends State implements
		IAbstractMachineInfo {
	
	@Override
	public String toString() {
		return machineFile.getComponentName();
	}

	private final ISCMachineFile machineFile;
	private final IRefinesMachine refinesMachine;

	public AbstractMachineInfo(ISCMachineFile machineFile, IRefinesMachine refinesMachine) {
		
		assert (machineFile == null || refinesMachine != null);
		
		this.machineFile = machineFile;
		this.refinesMachine = refinesMachine;
		makeImmutable();
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
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	public IRefinesMachine getRefinesClause() {
		return refinesMachine;
	}

}
