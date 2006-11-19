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
import org.eventb.core.EventBPlugin;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISeesContext;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.IProcessorModule;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.state.IStateRepository;
import org.eventb.internal.core.sc.symbolTable.MachineLabelSymbolTable;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineStaticChecker extends StaticChecker {

	private final static int LABEL_SYMTAB_SIZE = 2047;

	public static final String MACHINE_SC_TOOL_ID = EventBPlugin.PLUGIN_ID + ".machineSC"; //$NON-NLS-1$
	public static final String MACHINE_SC_REFINES_ID = EventBPlugin.PLUGIN_ID + ".machineSCRefines"; //$NON-NLS-1$
	public static final String MACHINE_SC_SEES_ID = EventBPlugin.PLUGIN_ID + ".machineSCSees"; //$NON-NLS-1$

	public static final String MACHINE_PROCESSOR = EventBPlugin.PLUGIN_ID + ".machineProcessor"; //$NON-NLS-1$
	
	private IModuleManager manager;
	
	private IProcessorModule[] machineProcessorModules = null;
	
	public MachineStaticChecker() {
		manager = ModuleManager.getModuleManager();
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IAutomaticTool#run(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean run(IFile file, IProgressMonitor monitor)
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

			if (machineProcessorModules == null) {
			
				machineProcessorModules = manager.getProcessorModules(MACHINE_PROCESSOR);
			
			}
		
			scMachineFile.create(true, null);
			
			IStateRepository<IStateSC> repository = createRepository(machineFile, monitor);
			
			machineFile.open(new SubProgressMonitor(monitor, 1));
			scMachineFile.open(new SubProgressMonitor(monitor, 1));
		
			runProcessorModules(
					machineFile, 
					scMachineFile,
					machineProcessorModules, 
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
	 * @see org.rodinp.core.builder.IExtractor#extract(org.eclipse.core.resources.IFile, org.rodinp.core.builder.IGraph)
	 */
	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		
		try {
			
			monitor.beginTask(Messages.bind(Messages.build_extracting, file.getName()), 1);
		
			IMachineFile source = (IMachineFile) RodinCore.valueOf(file);
			ISCMachineFile target = source.getSCMachineFile();
			ISeesContext[] seen = source.getSeesClauses();
			IRefinesMachine[] abstractMachines = source.getRefinesClauses();

			graph.openGraph();
			graph.addNode(target.getResource(), MACHINE_SC_TOOL_ID);
			graph.addToolDependency(
					source.getResource(), 
					target.getResource(), MACHINE_SC_TOOL_ID, true);	
		
			if (seen.length != 0) {
				for (ISeesContext seesContext : seen) {
					graph.addUserDependency(
							source.getResource(), 
							seesContext.getSeenSCContext().getResource(), 
							target.getResource(), 
							MACHINE_SC_SEES_ID, true);
				}
			}
		
			if (abstractMachines.length != 0) {
				graph.addUserDependency(
						source.getResource(), 
						abstractMachines[0].getAbstractSCMachine().getResource(), 
						target.getResource(), 
						MACHINE_SC_REFINES_ID, true);
			}
		
			graph.closeGraph();
			
		} finally {
			monitor.done();
		}

	}

	@Override
	protected IStateRepository<IStateSC> createRepository(
			IRodinFile file, 
			IProgressMonitor monitor) throws CoreException {
		IStateRepository<IStateSC> repository = super.createRepository(file, monitor);
		final MachineLabelSymbolTable labelSymbolTable = 
			new MachineLabelSymbolTable(LABEL_SYMTAB_SIZE);
		repository.setState(labelSymbolTable);		
		return repository;
	}

}
