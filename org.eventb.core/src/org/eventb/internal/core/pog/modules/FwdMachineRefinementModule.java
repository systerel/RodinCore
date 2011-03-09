/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.POGProcessorModule;
import org.eventb.core.pog.state.IMachineInfo;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.pog.MachineInfo;
import org.eventb.internal.core.pog.Messages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author Stefan Hallerstede
 *
 */
public class FwdMachineRefinementModule extends POGProcessorModule {

	public static final IModuleType<FwdMachineRefinementModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".fwdMachineRefinementModule"); //$NON-NLS-1$
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	protected IMachineInfo machineInfo;
	
	@Override
	public void endModule(IRodinElement element, IPOGStateRepository repository, IProgressMonitor monitor) throws CoreException {
		machineInfo = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		IRodinFile rf = (IRodinFile) element;
		ISCMachineRoot elementRoot = (ISCMachineRoot) rf.getRoot();
		ISCRefinesMachine[] refinesMachines = elementRoot.getSCRefinesClauses();
		
		IRodinFile abstractMachine = null;
		
		if (refinesMachines.length == 1) {
			
			abstractMachine = refinesMachines[0].getAbstractSCMachine();
			
		} else if (refinesMachines.length > 1) {
			
				throw Util.newCoreException(Messages.pog_multipleRefinementError);
		}
		
		machineInfo = new MachineInfo(abstractMachine);
		repository.setState(machineInfo);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IPOGProcessorModule#process(org.rodinp.core.IRodinElement, org.eventb.core.pog.state.IPOGStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		// do nothing nothing

	}

}
