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
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.ProcessorModule;
import org.eventb.core.sc.state.IContextPointerArray;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.state.IStateRepository;
import org.eventb.internal.core.sc.StaticChecker;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineContextClosureModule extends ProcessorModule {

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IProcessorModule#process(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalParent, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository<IStateSC> repository,
			IProgressMonitor monitor) throws CoreException {
		
		IMachineFile machineFile = (IMachineFile) element;

		IRefinesMachine refinesMachine = machineFile.getRefinesClause(null);
		
		if (refinesMachine == null)
			return;
		
		ISCMachineFile scMachineFile = refinesMachine.getAbstractSCMachine(null);
		
		ISCInternalContext[] abstractContexts = scMachineFile.getSCSeenContexts(null);
		
		if (abstractContexts.length == 0)
			return;
		
		IContextPointerArray contextPointerArray = 
			(IContextPointerArray) repository.getState(IContextPointerArray.STATE_TYPE);

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
						refinesMachine, 
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
