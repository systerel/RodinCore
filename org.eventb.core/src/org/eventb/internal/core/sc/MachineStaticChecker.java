/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISeesContext;
import org.eventb.core.sc.ISCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.internal.core.sc.modules.MachineModule;
import org.eventb.internal.core.tool.IModuleFactory;
import org.eventb.internal.core.tool.SCModuleManager;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineStaticChecker extends StaticChecker {

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IAutomaticTool#run(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean run(IFile source, IFile file, IProgressMonitor monitor)
			throws CoreException {
		
		ISCMachineFile scMachineFile = (ISCMachineFile) RodinCore.valueOf(file).getMutableCopy();
		IMachineFile machineFile = (IMachineFile) scMachineFile.getMachineFile().getSnapshot();
		
		int size = machineFile.getChildren().length + 3;
		
		try {
			
			monitor.beginTask(
					Messages.bind(
							Messages.build_runningMSC, 
							StaticChecker.getStrippedComponentName(file.getName())), 
					size);

			scMachineFile.create(true, null);
			
			ISCStateRepository repository = createRepository(machineFile, monitor);
			
			machineFile.open(new SubProgressMonitor(monitor, 1));
			scMachineFile.open(new SubProgressMonitor(monitor, 1));
		
			IModuleFactory moduleFactory = 
				SCModuleManager.getInstance().getModuleFactory(DEFAULT_CONFIG);
			
			ISCProcessorModule rootModule = 
				(ISCProcessorModule) moduleFactory.getRootModule(MachineModule.MODULE_TYPE);
			
			runProcessorModules(
					rootModule,
					machineFile, 
					scMachineFile,
					repository,
					monitor);
		
			scMachineFile.save(new SubProgressMonitor(monitor, 1), true);
		
			// TODO delta checking
			// return repository.targetHasChanged();
		
			return true;
			
		} finally {
			monitor.done();
			scMachineFile.makeConsistent(null);
		}
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IExtractor#extract(org.eclipse.core.resources.IFile, org.rodinp.core.builder.IGraph)
	 */
	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		
		try {
			
			monitor.beginTask(Messages.bind(Messages.build_extracting, file.getName()), 1);
		
			IMachineFile source = (IMachineFile) RodinCore.valueOf(file);
			ISCMachineFile target = source.getSCMachineFile();
			ISeesContext[] seen = source.getSeesClauses();
			IRefinesMachine[] abstractMachines = source.getRefinesClauses();

			graph.addTarget(target.getResource());
			graph.addToolDependency(
					source.getResource(), 
					target.getResource(), true);	
		
			if (seen.length != 0) {
				for (ISeesContext seesContext : seen) {
					graph.addUserDependency(
							source.getResource(), 
							seesContext.getSeenSCContext().getResource(), 
							target.getResource(), 
							true);
				}
			}
		
			if (abstractMachines.length != 0) {
				graph.addUserDependency(
						source.getResource(), 
						abstractMachines[0].getAbstractSCMachine().getResource(), 
						target.getResource(), 
						true);
			}
		
		} finally {
			monitor.done();
		}

	}

}
