/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.IContextPointerArray;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.util.GraphProblem;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.ContextPointerArray;
import org.eventb.internal.core.sc.StaticChecker;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineContextClosureModule extends SCProcessorModule {

	public static final IModuleType<MachineContextClosureModule> MODULE_TYPE = 
		SCCore.getModuleType(EventBPlugin.PLUGIN_ID + ".machineContextClosureModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IProcessorModule#process(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalParent, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IInternalParent target,
			ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		IMachineFile machineFile = (IMachineFile) element;

		IRefinesMachine[] refinesMachines = machineFile.getRefinesClauses();
		
		if (refinesMachines.length == 0)
			return;
		
		ISCMachineFile scMachineFile = refinesMachines[0].getAbstractSCMachine();
		
		ISCInternalContext[] abstractContexts = scMachineFile.getSCSeenContexts();
		
		if (abstractContexts.length == 0)
			return;
		
		ContextPointerArray contextPointerArray = 
			(ContextPointerArray) repository.getState(IContextPointerArray.STATE_TYPE);

		List<ISCContext> validContexts = contextPointerArray.getValidContexts();
		
		HashSet<String> validContextNames = new HashSet<String>(validContexts.size() * 4 / 3 + 1);
		for (ISCContext context : validContexts) {
			validContextNames.add(context.getElementName());
		}
		
		for (ISCInternalContext context : abstractContexts) {
			String name = context.getElementName();
			if (validContextNames.contains(name))
				continue;
			else {
				
				createProblemMarker(
						refinesMachines[0], 
						EventBAttributes.TARGET_ATTRIBUTE, 
						GraphProblem.ContextOnlyInAbstractMachineWarning,
						StaticChecker.getStrippedComponentName(name));
				
				// repair
				// TODO delta checking
				context.copy(target, null, null, false, null);

			}
		}
		
	}

}
