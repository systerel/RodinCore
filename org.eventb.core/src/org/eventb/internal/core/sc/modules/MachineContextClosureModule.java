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
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.sc.IContextPointerArray;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.ProcessorModule;
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
			IStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		IMachineFile machineFile = (IMachineFile) element;

		IRefinesMachine refinesMachine = machineFile.getRefinesClause();
		
		if (refinesMachine == null)
			return;
		
		ISCMachineFile scMachineFile = refinesMachine.getAbstractSCMachine();
		
		ISCInternalContext[] abstractContexts = scMachineFile.getSCInternalContexts();
		
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
				
				issueMarker(
						IMarkerDisplay.SEVERITY_WARNING, 
						refinesMachine, 
						StaticChecker.getStrippedComponentName(name));
				
				// repair
				// TODO delta checking
				context.copy(target, null, null, false, monitor);

			}
		}
		
	}

}
