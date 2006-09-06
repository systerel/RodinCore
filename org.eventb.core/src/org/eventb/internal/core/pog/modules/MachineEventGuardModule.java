/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ISCVariable;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.pog.IAbstractEventGuardTable;
import org.eventb.core.pog.IConcreteEventGuardTable;
import org.eventb.core.pog.IEventHypothesisManager;
import org.eventb.core.pog.IIdentifierTable;
import org.eventb.core.pog.IModule;
import org.eventb.core.pog.IModuleManager;
import org.eventb.core.pog.IWitnessTable;
import org.eventb.core.pog.Module;
import org.eventb.core.pog.POGHint;
import org.eventb.core.pog.POGPredicate;
import org.eventb.core.pog.POGSource;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.ITypingState;
import org.eventb.internal.core.pog.AbstractEventGuardTable;
import org.eventb.internal.core.pog.ModuleManager;
import org.eventb.internal.core.pog.WitnessTable;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventGuardModule extends Module {

	public static final String MACHINE_EVENT_GUARD_MODULE = 
		EventBPlugin.PLUGIN_ID + ".machineEventGuardModule";

	private Predicate btrue;
	private List<POGPredicate> emptyPredicates;
	private POGHint[] emptyHints;

	private IModule[] modules;

	public MachineEventGuardModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		modules = manager.getProcessorModules(MACHINE_EVENT_GUARD_MODULE);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IProcessorModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IPOFile target,
			IStateRepository repository, 
			IProgressMonitor monitor)
			throws CoreException {
		
		ISCEvent event = (ISCEvent) element;
		String eventLabel = event.getLabel(monitor);
		
		List<ISCPredicateElement> guards = eventGuardTable.getElements();
		
		if(guards.size() == 0)
			return;
		
		List<Predicate> predicates = eventGuardTable.getPredicates();
		
		for (int i=0; i<guards.size(); i++) {
			String guardLabel = ((ISCGuard) guards.get(i)).getLabel(monitor);
			
			Predicate wdPredicate = predicates.get(i).getWDPredicate(factory);
			if(!wdPredicate.equals(btrue)) {
				createPO(
						target, 
						eventLabel + "/" + guardLabel + "/WD", 
						"Well-definedness of Guard",
						eventIdentifierTable,
						eventHypothesisManager.getHypothesisName(guards.get(i), monitor),
						emptyPredicates,
						new POGPredicate(guards.get(i), wdPredicate),
						sources(new POGSource("guard", guards.get(i))),
						emptyHints,
						monitor);
			}

		}
		
	}
	
	private ISCEvent getAbstractEvent(ISCEvent event) throws CoreException {
		ISCEvent[] events = event.getAbstractSCEvents();
		
		if (events.length == 0)
			return null;
		
		return events[0];
	}
	
	ISCEvent abstractEvent;
	IEventHypothesisManager eventHypothesisManager;
	ITypeEnvironment eventTypeEnvironment;
	IConcreteEventGuardTable eventGuardTable;
	IAbstractEventGuardTable abstractEventGuardTable;
	IIdentifierTable eventIdentifierTable;
	IWitnessTable witnessTable;
	FormulaFactory factory;

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(IRodinElement element, IPOFile target, IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		super.initModule(element, target, repository, monitor);
		ISCEvent event = (ISCEvent) element;
		eventHypothesisManager = 
			(IEventHypothesisManager) repository.getState(IEventHypothesisManager.STATE_TYPE);
		eventTypeEnvironment =
			((ITypingState) repository.getState(ITypingState.STATE_TYPE)).getTypeEnvironment();
		eventGuardTable = 
			(IConcreteEventGuardTable) repository.getState(IConcreteEventGuardTable.STATE_TYPE);
		eventIdentifierTable =
			(IIdentifierTable) repository.getState(IIdentifierTable.STATE_TYPE);
		factory = repository.getFormulaFactory();
		btrue = factory.makeLiteralPredicate(Formula.BTRUE, null);
		emptyPredicates = new ArrayList<POGPredicate>(0);
		abstractEvent = getAbstractEvent(event);
		
		if (abstractEvent != null) {
			ISCVariable[] abstractVariables = abstractEvent.getSCVariables();
			
			for (ISCVariable variable : abstractVariables) {
				String name = variable.getIdentifierName();
				if (eventTypeEnvironment.contains(name))
					continue;
				Type type = variable.getType(factory);
				eventIdentifierTable.addIdentifier(factory.makeFreeIdentifier(name, null, type));
				eventTypeEnvironment.addName(name, type);
			}
		}
		
		abstractEventGuardTable = 
			new AbstractEventGuardTable(
					(abstractEvent == null ? new ISCGuard[0] : abstractEvent.getSCGuards()),
					eventTypeEnvironment, 
					factory);
		repository.setState(abstractEventGuardTable);
		
		witnessTable = 
			new WitnessTable(event.getSCWitnesses(), eventTypeEnvironment, factory, monitor);
		repository.setState(witnessTable);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(IRodinElement element, IPOFile target, IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		eventHypothesisManager = null;
		eventTypeEnvironment = null;
		eventGuardTable = null;
		eventIdentifierTable = null;
		witnessTable = null;
		factory = null;
		btrue = null;
		super.endModule(element, target, repository, monitor);
	}

}
