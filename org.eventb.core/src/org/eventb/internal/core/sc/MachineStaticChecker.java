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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISeesContext;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.IProcessorModule;
import org.eventb.core.sc.IStateRepository;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineStaticChecker extends StaticChecker {

	public static final String MACHINE_SC_TOOL_ID = EventBPlugin.PLUGIN_ID + ".machineSC"; //$NON-NLS-1$
	public static final String MACHINE_SC_REFINES_ID = EventBPlugin.PLUGIN_ID + ".machineSCRefines"; //$NON-NLS-1$
	public static final String MACHINE_SC_SEES_ID = EventBPlugin.PLUGIN_ID + ".machineSCSees"; //$NON-NLS-1$

	public static final String MACHINE_PROCESSOR = EventBPlugin.PLUGIN_ID + ".machineProcessor"; //$NON-NLS-1$
	
	private IModuleManager manager;
	
	private IProcessorModule[] machineModules = null;
	
	public MachineStaticChecker() {
		manager = ModuleManager.getModuleManager();
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IAutomaticTool#run(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean run(IFile file, IProgressMonitor monitor)
			throws CoreException {
		
		ISCMachineFile scMachineFile = (ISCMachineFile) RodinCore.create(file).getMutableCopy();
		IMachineFile machineFile = (IMachineFile) scMachineFile.getMachineFile().getSnapshot();
		
		int size = machineFile.getChildren().length + 3;
		
		try {
			
			monitor.beginTask(
					Messages.bind(
							Messages.build_runningMSC, 
							StaticChecker.getStrippedComponentName(file.getName())), 
					size);

			if (machineModules == null) {
			
				machineModules = manager.getProcessorModules(MACHINE_PROCESSOR);
			
			}
		
			IRodinProject project = (IRodinProject) scMachineFile.getParent();
			project.createRodinFile(scMachineFile.getElementName(), true, null);
			
			IStateRepository repository = createRepository(machineFile, monitor);
			
			machineFile.open(new SubProgressMonitor(monitor, 1));
			scMachineFile.open(new SubProgressMonitor(monitor, 1));
		
			runProcessorModules(
					machineFile, 
					scMachineFile,
					machineModules, 
					repository,
					monitor);
		
			scMachineFile.save(new SubProgressMonitor(monitor, 1), true);
		
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
		
		try {
		
			monitor.beginTask(Messages.bind(Messages.build_cleaning, file.getName()), 1);
			
			file.delete(true, monitor);
			
		} finally {
			monitor.done();
		}

	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IExtractor#extract(org.eclipse.core.resources.IFile, org.rodinp.core.builder.IGraph)
	 */
	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		
		try {
			
			monitor.beginTask(Messages.bind(Messages.build_extracting, file.getName()), 1);
		
			IMachineFile source = (IMachineFile) RodinCore.create(file);
			ISCMachineFile target = source.getSCMachineFile();
			ISeesContext[] seen = source.getSeesClauses();
			IRefinesMachine abstractMachine = source.getRefinesClause();

			IPath sourcePath = source.getPath();
			IPath targetPath = target.getPath();
		
			graph.addNode(targetPath, MACHINE_SC_TOOL_ID);
			graph.putToolDependency(sourcePath, targetPath, MACHINE_SC_TOOL_ID, true);	
		
			if (seen.length != 0) {
				for (ISeesContext seesContext : seen) {
					IPath seenPath = seesContext.getSeenSCContext().getPath();
					graph.putUserDependency(
							sourcePath, 
							seenPath, 
							targetPath, 
							MACHINE_SC_SEES_ID, true);
				}
			}
		
			if (abstractMachine != null) {
				IPath abstractPath = abstractMachine.getAbstractSCMachine().getPath();
				graph.putUserDependency(
						sourcePath, 
						abstractPath, 
						targetPath, 
						MACHINE_SC_REFINES_ID, true);
			}
		
			graph.updateGraph();
			
		} finally {
			monitor.done();
		}

	}

}
