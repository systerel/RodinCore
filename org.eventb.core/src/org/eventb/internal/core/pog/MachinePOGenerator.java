/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.core.pog.IModuleManager;
import org.eventb.core.pog.IPOGProcessorModule;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachinePOGenerator extends ProofObligationGenerator {

	public static final String MACHINE_MODULE = EventBPlugin.PLUGIN_ID + ".machineModule"; //$NON-NLS-1$
	
	private IModuleManager manager;
	
	private IPOGProcessorModule[] machineModules = null;
	
	public MachinePOGenerator() {
		manager = ModuleManager.getModuleManager();
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IAutomaticTool#run(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean run(IFile source, IFile target, IProgressMonitor monitor)
			throws CoreException {

		IPOFile poFile = (IPOFile) RodinCore.valueOf(target).getMutableCopy();
		ISCMachineFile scMachineFile = (ISCMachineFile) poFile.getSCMachineFile().getSnapshot();
		
		try {
			
			monitor.beginTask(
					Messages.bind(
							Messages.build_runningMPO, 
							poFile.getComponentName()),
					10);
			
			if (machineModules == null) {
			
				machineModules = manager.getProcessorModules(MACHINE_MODULE);
			
			}
		
			poFile.create(true, null);

			IPOGStateRepository repository = createRepository(poFile, monitor);
		
			runModules(
					scMachineFile, 
					poFile,
					machineModules, 
					repository,
					monitor);
		
			poFile.save(monitor, true);
		
			// TODO delta checking
			// return repository.targetHasChanged();

			return true;
		} finally {
			monitor.done();
			poFile.makeConsistent(null);
		}
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IExtractor#extract(org.eclipse.core.resources.IFile, org.rodinp.core.builder.IGraph)
	 */
	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		
		ISCMachineFile source = (ISCMachineFile) RodinCore.valueOf(file);
		IPOFile target = source.getMachineFile().getPOFile();
		
		graph.addTarget(target.getResource());
		graph.addToolDependency(
				source.getResource(), 
				target.getResource(), true);
		
		ISCRefinesMachine[] refinesMachines = source.getSCRefinesClauses();
		if (refinesMachines.length != 0) {
			graph.addUserDependency(
					source.getMachineFile().getResource(), 
					refinesMachines[0].getAbstractSCMachine().getResource(), 
					target.getResource(), false);
		}

	}

}
