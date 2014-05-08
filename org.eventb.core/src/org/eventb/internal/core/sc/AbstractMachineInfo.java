/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.sc;

import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.sc.state.IAbstractMachineInfo;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public class AbstractMachineInfo extends State implements
		IAbstractMachineInfo {
	
	@Override
	public String toString() {
		return machineFile == null ? "null" : machineFile.getComponentName();
	}

	private final ISCMachineRoot machineFile;
	private final IRefinesMachine refinesMachine;

	public AbstractMachineInfo(ISCMachineRoot machineFile, IRefinesMachine refinesMachine) {
		
		assert (machineFile == null || refinesMachine != null);
		
		this.machineFile = machineFile;
		this.refinesMachine = refinesMachine;
		makeImmutable();
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.state.IAbstractMachineInfo#getAbstractMachine()
	 */
	@Override
	public ISCMachineRoot getAbstractMachine() {
		return machineFile;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.tool.state.IToolState#getStateType()
	 */
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	@Override
	public IRefinesMachine getRefinesClause() {
		return refinesMachine;
	}

}
