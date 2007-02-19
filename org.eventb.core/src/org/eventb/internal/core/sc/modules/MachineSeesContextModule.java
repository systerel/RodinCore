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
import org.eventb.core.EventBPlugin;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISeesContext;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IContextPointerArray;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.util.GraphProblem;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.ContextPointerArray;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineSeesContextModule extends ContextPointerModule {

	public static final IModuleType<MachineSeesContextModule> MODULE_TYPE = 
		SCCore.getModuleType(EventBPlugin.PLUGIN_ID + ".machineSeesContextModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	public void process(
			IRodinElement element, 
			IInternalParent target,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {

		IMachineFile machineFile = (IMachineFile) element;
		
		ISeesContext[] seesContexts = machineFile.getSeesClauses();
		
		ISCContextFile[] contextFiles = new ISCContextFile[seesContexts.length];
		
		for(int i=0; i<seesContexts.length; i++) {
			contextFiles[i] = seesContexts[i].getSeenSCContext();
		}
		
		ContextPointerArray contextPointerArray = 
			new ContextPointerArray(
					IContextPointerArray.PointerType.SEES_POINTER, 
					seesContexts, 
					contextFiles);
		repository.setState(contextPointerArray);
		
		// we need to do everything up to this point
		// produce a define repository state
		
		if (seesContexts.length == 0)
			return; // nothing to do
		
		monitor.subTask(Messages.bind(Messages.progress_MachineSees));
		
		fetchSCContexts(
				contextPointerArray,
				monitor);
		
		createInternalContexts(
				target, 
				contextPointerArray.getValidContexts(), 
				repository, 
				null);
		
	}

	@Override
	protected IRodinProblem getTargetContextNotFoundProblem() {
		return GraphProblem.SeenContextNotFoundError;
	}

	@Override
	protected ISCInternalContext getSCInternalContext(IInternalParent target, String elementName) {
		return ((ISCMachineFile) target).getSCSeenContext(elementName);
	}
		
}
