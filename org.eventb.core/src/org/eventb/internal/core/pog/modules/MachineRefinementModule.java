/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.core.pog.POGProcessorModule;
import org.eventb.core.pog.state.IMachineInfo;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.pog.MachineInfo;
import org.eventb.internal.core.pog.Messages;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineRefinementModule extends POGProcessorModule {

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
		ISCRefinesMachine[] refinesMachines = ((ISCMachineFile) element).getSCRefinesClauses();
		
		ISCMachineFile abstractMachine = null;
		
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
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		// do nothing nothing

	}

}
