/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.POGPredicate;
import org.eventb.core.pog.POGSource;
import org.eventb.core.pog.state.IAbstractEventGuardTable;
import org.eventb.core.pog.state.IConcreteEventGuardTable;
import org.eventb.core.pog.state.IEventHypothesisManager;
import org.eventb.core.pog.state.IStatePOG;
import org.eventb.core.pog.state.ITypingState;
import org.eventb.core.state.IStateRepository;
import org.eventb.internal.core.pog.AbstractEventGuardTable;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventGuardModule extends UtilityModule {

	public static final String MACHINE_EVENT_GUARD_MODULE = 
		EventBPlugin.PLUGIN_ID + ".machineEventGuardModule";

//	private IModule[] modules;
//
//	public MachineEventGuardModule() {
//		IModuleManager manager = ModuleManager.getModuleManager();
//		modules = manager.getProcessorModules(MACHINE_EVENT_GUARD_MODULE);
//	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IProcessorModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IPOFile target,
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor)
			throws CoreException {
		
		ISCEvent event = (ISCEvent) element;
		String eventLabel = event.getLabel();
		
		List<ISCPredicateElement> guards = eventGuardTable.getElements();
		
		if(guards.size() == 0)
			return;
		
		List<Predicate> predicates = eventGuardTable.getPredicates();
		
		for (int i=0; i<guards.size(); i++) {
			String guardLabel = ((ISCGuard) guards.get(i)).getLabel();
			
			Predicate wdPredicate = predicates.get(i).getWDPredicate(factory);
			if(!wdPredicate.equals(btrue)) {
				createPO(
						target, 
						eventLabel + "/" + guardLabel + "/WD", 
						"Well-definedness of Guard",
						eventHypothesisManager.getHypothesis(target, guards.get(i), monitor),
						emptyPredicates,
						new POGPredicate(guards.get(i), wdPredicate),
						sources(new POGSource("guard", (ITraceableElement) guards.get(i))),
						emptyHints,
						monitor);
			}

		}
		
	}
	
	IEventHypothesisManager eventHypothesisManager;
	IConcreteEventGuardTable eventGuardTable;

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
		eventHypothesisManager = 
			(IEventHypothesisManager) repository.getState(IEventHypothesisManager.STATE_TYPE);
		ITypeEnvironment eventTypeEnvironment =
			((ITypingState) repository.getState(ITypingState.STATE_TYPE)).getTypeEnvironment();
		eventGuardTable = 
			(IConcreteEventGuardTable) repository.getState(IConcreteEventGuardTable.STATE_TYPE);

		ISCEvent abstractEvent = eventHypothesisManager.getFirstAbstractEvent();
		IAbstractEventGuardTable abstractEventGuardTable = 
			new AbstractEventGuardTable(
					(abstractEvent == null ? new ISCGuard[0] : abstractEvent.getSCGuards()),
					eventTypeEnvironment, 
					factory);
		repository.setState(abstractEventGuardTable);
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
		eventHypothesisManager = null;
		eventGuardTable = null;
		super.endModule(element, target, repository, monitor);
	}

}
