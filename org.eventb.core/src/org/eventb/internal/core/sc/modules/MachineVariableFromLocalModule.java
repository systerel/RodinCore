/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.IAbstractEventTable;
import org.eventb.core.sc.state.IAbstractMachineInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.StaticChecker;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineVariableFromLocalModule extends SCFilterModule {

	public static final IModuleType<MachineVariableFromLocalModule> MODULE_TYPE = 
		SCCore.getModuleType(EventBPlugin.PLUGIN_ID + ".machineVariableFromLocalModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private IAbstractEventTable abstractEventTable;
	private IAbstractMachineInfo abstractMachineInfo;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#initModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		abstractEventTable = (IAbstractEventTable) 
			repository.getState(IAbstractEventTable.STATE_TYPE);
		abstractMachineInfo = (IAbstractMachineInfo)
			repository.getState(IAbstractMachineInfo.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAcceptorModule#accept(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean accept(
			IRodinElement element, 
			ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		IIdentifierElement identifierElement = (IIdentifierElement) element;
		
		String variableName = identifierElement.getIdentifierString();
		
		if (abstractEventTable.isLocalVariable(variableName)) {
			
			String abstractName = 
				StaticChecker.getStrippedComponentName(
						abstractMachineInfo.getAbstractMachine().getElementName());
			
			createProblemMarker(
					identifierElement, 
					EventBAttributes.IDENTIFIER_ATTRIBUTE, 
					GraphProblem.VariableIsLocalInAbstractMachineError, 
					variableName,
					abstractName);
			
			return false;
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#endModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		abstractEventTable = null;
		abstractMachineInfo = null;
	}

}
