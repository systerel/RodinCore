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
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.pog.IModule;
import org.eventb.core.pog.IModuleManager;
import org.eventb.core.pog.state.IMachineHypothesisManager;
import org.eventb.core.pog.state.IStatePOG;
import org.eventb.core.pog.state.ITypingState;
import org.eventb.core.state.IStateRepository;
import org.eventb.internal.core.pog.ModuleManager;
import org.eventb.internal.core.pog.TypingState;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventModule extends UtilityModule {

	public static final String MACHINE_EVENT_MODULE = 
		EventBPlugin.PLUGIN_ID + ".machineEventModule";

	private IModule[] modules;

	public MachineEventModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		modules = manager.getProcessorModules(MACHINE_EVENT_MODULE);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IProcessorModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IPOFile target,
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor)
			throws CoreException {
		
		ISCMachineFile scMachineFile =  (ISCMachineFile) element;
		
		ISCEvent[] events = scMachineFile.getSCEvents();
		
		if (events.length == 0)
			return;
		
		for (ISCEvent event : events) {
			
			ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
			typeEnvironment.addAll(machineTypeEnvironment);
			
			repository.setState(new TypingState(typeEnvironment));
			
			initModules(event, target, modules, repository, monitor);
			
			processModules(modules, event, target, repository, monitor);
			
			endModules(event, target, modules, repository, monitor);
		}

	}
	
	ITypingState typingState;
	ITypeEnvironment machineTypeEnvironment;
	IMachineHypothesisManager machineHypothesisManager;
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IPOFile target, 
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, target, repository, monitor);
		typingState =
			(ITypingState) repository.getState(ITypingState.STATE_TYPE);
		machineTypeEnvironment = typingState.getTypeEnvironment();
		machineHypothesisManager =
			(IMachineHypothesisManager) repository.getState(IMachineHypothesisManager.STATE_TYPE);
		factory = repository.getFormulaFactory();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IPOFile target, 
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor) throws CoreException {
		super.endModule(element, target, repository, monitor);
		repository.setState(typingState);
		typingState = null;
		machineTypeEnvironment = null;
		machineHypothesisManager = null;
		factory = null;
	}

}
