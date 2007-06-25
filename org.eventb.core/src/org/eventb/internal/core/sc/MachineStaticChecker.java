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
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISeesContext;
import org.eventb.core.sc.ISCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
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
		
		final ISCMachineFile scMachineFile = (ISCMachineFile) RodinCore.valueOf(file).getMutableCopy();
		final IMachineFile machineFile = (IMachineFile) scMachineFile.getMachineFile().getSnapshot();
		final ISCMachineFile scTmpFile = (ISCMachineFile) getTmpSCFile(scMachineFile);
		
		final int totalWork = machineFile.getChildren().length + 4;
		
		try {
			
			monitor.beginTask(
					Messages.bind(
							Messages.build_runningMSC, 
							scMachineFile.getComponentName()), 
					totalWork);

			scTmpFile.create(true, null);
			
			ISCStateRepository repository = createRepository(machineFile, monitor);
			
			machineFile.open(new SubProgressMonitor(monitor, 1));
			scTmpFile.open(new SubProgressMonitor(monitor, 1));
		
			IModuleFactory moduleFactory = 
				SCModuleManager.getInstance().getModuleFactory(DEFAULT_CONFIG);
			
			printModuleTree(machineFile, moduleFactory);
			
			ISCProcessorModule rootModule = 
				(ISCProcessorModule) moduleFactory.getRootModule(IMachineFile.ELEMENT_TYPE);
			
			runProcessorModules(
					rootModule,
					machineFile, 
					scTmpFile,
					repository,
					monitor);
		
			return compareAndSave(scMachineFile, scTmpFile, monitor);

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
		
			for (ISeesContext seesContext : seen) {
				if (seesContext.hasSeenContextName()) {
					ISCContextFile seenSCContext = seesContext.getSeenSCContext();
					graph.addUserDependency(
							source.getResource(), 
							seenSCContext.getResource(), 
							target.getResource(), 
							true);
				}
			}
		
			if (abstractMachines.length != 0 && abstractMachines[0].hasAbstractMachineName()) {
				ISCMachineFile abstractSCMachine = abstractMachines[0].getAbstractSCMachine();
				graph.addUserDependency(
						source.getResource(), 
						abstractSCMachine.getResource(), 
						target.getResource(), 
						true);
			}
		
		} finally {
			monitor.done();
		}

	}

}
