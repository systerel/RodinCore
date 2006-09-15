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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.pog.IModule;
import org.eventb.core.pog.IModuleManager;
import org.eventb.core.sc.IStateRepository;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachinePOGenerator extends ProofObligationGenerator {

	public static final String MACHINE_POG_TOOL_ID = EventBPlugin.PLUGIN_ID + ".machinePOG"; //$NON-NLS-1$

	public static final String MACHINE_MODULE = EventBPlugin.PLUGIN_ID + ".machineModule"; //$NON-NLS-1$
	
	private IModuleManager manager;
	
	private IModule[] machineModules = null;
	
	public MachinePOGenerator() {
		manager = ModuleManager.getModuleManager();
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IAutomaticTool#run(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean run(IFile file, IProgressMonitor monitor)
			throws CoreException {

		IPOFile poFile = (IPOFile) RodinCore.create(file);
		ISCMachineFile scMachineFile = poFile.getSCMachine();
		
		try {
			
			monitor.beginTask(
					Messages.bind(
							Messages.build_runningMPO, 
							EventBPlugin.getComponentName(file.getName())),
					1);
			
			if (machineModules == null) {
			
				machineModules = manager.getProcessorModules(MACHINE_MODULE);
			
			}
		
			IRodinProject project = (IRodinProject) poFile.getParent();
			project.createRodinFile(poFile.getElementName(), true, null);

			IStateRepository repository = createRepository(scMachineFile, monitor);
		
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
		}
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IAutomaticTool#clean(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void clean(IFile file, IProgressMonitor monitor)
			throws CoreException {
		file.delete(true, monitor);

	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IExtractor#extract(org.eclipse.core.resources.IFile, org.rodinp.core.builder.IGraph)
	 */
	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		
		ISCMachineFile source = (ISCMachineFile) RodinCore.create(file);
		IPOFile target = source.getMachineFile().getPOFile();
		
		IPath sourcePath = source.getPath();
		IPath targetPath = target.getPath();
		
		graph.addNode(targetPath, MACHINE_POG_TOOL_ID);
		graph.putToolDependency(sourcePath, targetPath, MACHINE_POG_TOOL_ID, true);
		graph.updateGraph();

	}

}
