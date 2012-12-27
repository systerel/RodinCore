/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCParameter;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IAbstractEventActionTable;
import org.eventb.core.pog.state.IAbstractEventGuardList;
import org.eventb.core.pog.state.IAbstractEventGuardTable;
import org.eventb.core.pog.state.IConcreteEventActionTable;
import org.eventb.core.pog.state.IConcreteEventGuardTable;
import org.eventb.core.pog.state.IEventWitnessTable;
import org.eventb.core.pog.state.IMachineHypothesisManager;
import org.eventb.core.pog.state.IMachineVariableTable;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.pog.AbstractEventActionTable;
import org.eventb.internal.core.pog.AbstractEventGuardList;
import org.eventb.internal.core.pog.AbstractEventGuardTable;
import org.eventb.internal.core.pog.ConcreteEventActionTable;
import org.eventb.internal.core.pog.ConcreteEventGuardTable;
import org.eventb.internal.core.pog.EventHypothesisManager;
import org.eventb.internal.core.pog.EventWitnessTable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class FwdMachineEventHypothesesModule extends UtilityModule {

	public static final IModuleType<FwdMachineEventHypothesesModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".fwdMachineEventHypothesesModule"); //$NON-NLS-1$
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	EventHypothesisManager eventHypothesisManager;
	ITypeEnvironmentBuilder eventTypeEnvironment;
	IAbstractEventGuardList abstractEventGuardList;
	IEventWitnessTable witnessTable;
	IMachineVariableTable variableTable;

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IProcessorModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void process(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		// all processing is done in the initModule() method

	}
	
	private void fetchActionsAndVariables(
			ISCEvent concreteEvent, 
			IPOGStateRepository repository) throws CoreException {
		
		IMachineVariableTable machineVariableTable =
			(IMachineVariableTable) repository.getState(IMachineVariableTable.STATE_TYPE);
		
		IConcreteEventActionTable concreteEventActionTable =
			new ConcreteEventActionTable(
					concreteEvent.getSCActions(), 
					eventTypeEnvironment, 
					machineVariableTable, 
					factory);
		concreteEventActionTable.makeImmutable();
		repository.setState(concreteEventActionTable);
		
		IAbstractEventActionTable abstractEventActionTable = 
			fetchAbstractActions(
					concreteEventActionTable, 
					machineVariableTable, 
					repository);
		
		fetchPostValueVariables(concreteEventActionTable.getAssignedVariables());
		fetchPostValueVariables(abstractEventActionTable.getAssignedVariables());
	}

	private IAbstractEventActionTable fetchAbstractActions(
			IConcreteEventActionTable concreteEventActionTable, 
			IMachineVariableTable machineVariableTable, 
			IPOGStateRepository repository) throws RodinDBException, CoreException {
		ISCEvent abstractEvent = abstractEventGuardList.getFirstAbstractEvent();
		
		if (abstractEvent != null)
			fetchParameters(abstractEvent.getSCParameters());
		
		IAbstractEventActionTable abstractEventActionTable = 
			new AbstractEventActionTable(
					getAbstractSCActions(abstractEvent), 
					eventTypeEnvironment, 
					machineVariableTable,
					concreteEventActionTable,
					factory);
		abstractEventActionTable.makeImmutable();
		repository.setState(abstractEventActionTable);
		return abstractEventActionTable;
	}

	private ISCAction[] getAbstractSCActions(ISCEvent abstractEvent) throws RodinDBException {
		return (abstractEvent == null ? new ISCAction[0] : abstractEvent.getSCActions());
	}

	private void setEventHypothesisManager(
			IMachineHypothesisManager machineHypothesisManager, 
			ISCEvent event, 
			ISCGuard[] guards, 
			boolean accurate,
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		IPORoot target = repository.getTarget();
		
		IPOPredicateSet fullHypothesis = (event.getLabel().equals(IEvent.INITIALISATION)) ?
				machineHypothesisManager.getContextHypothesis() :
				machineHypothesisManager.getFullHypothesis();
		eventHypothesisManager = 
			new EventHypothesisManager(event, target, guards, accurate, fullHypothesis.getElementName());
		
		repository.setState(eventHypothesisManager);
	}

	private void fetchGuards(
			ISCEvent concreteEvent,
			ISCEvent[] abstractEvents,
			ISCGuard[] concreteGuards, 
			IPOGStateRepository repository) throws CoreException {
		
		fetchParameters(concreteEvent.getSCParameters());
		
		IConcreteEventGuardTable concreteEventGuardTable = 
			fetchConcreteGuards(concreteGuards, repository);
		
		List<IAbstractEventGuardTable> abstractGuardTables = 
			new ArrayList<IAbstractEventGuardTable>(abstractEvents.length);
		
		for (ISCEvent abstractEvent : abstractEvents) {
			
			fetchParameters(abstractEvent.getSCParameters());
		
			IAbstractEventGuardTable abstractEventGuardTable = 
				new AbstractEventGuardTable(
						abstractEvent.getSCGuards(),
						eventTypeEnvironment, 
						concreteEventGuardTable,
						factory);
			
			abstractGuardTables.add(abstractEventGuardTable);
		}
		
		abstractEventGuardList =
			new AbstractEventGuardList(abstractEvents, abstractGuardTables);
		
		repository.setState(abstractEventGuardList);
	}

	private IConcreteEventGuardTable fetchConcreteGuards(
			ISCGuard[] concreteGuards, 
			IPOGStateRepository repository) throws RodinDBException, CoreException {
		IConcreteEventGuardTable concreteEventGuardTable = 
			new ConcreteEventGuardTable(
					concreteGuards, 
					eventTypeEnvironment, 
					factory);
		repository.setState(concreteEventGuardTable);
		return concreteEventGuardTable;
	}

	private void fetchPostValueVariables(Collection<FreeIdentifier> identifiers) throws CoreException {
		for (FreeIdentifier identifier : identifiers) {
			FreeIdentifier primedIdentifier = identifier.withPrime(factory);
			if (eventTypeEnvironment.contains(primedIdentifier.getName()))
				continue;
			eventHypothesisManager.addIdentifier(primedIdentifier);
			eventTypeEnvironment.addName(primedIdentifier.getName(), primedIdentifier.getType());
		}
	}

	private void fetchParameters(ISCParameter[] parameters) throws CoreException {
		for (ISCParameter variable : parameters) {
			FreeIdentifier identifier = variable.getIdentifier(factory);
			eventTypeEnvironment.add(identifier);
			eventHypothesisManager.addIdentifier(identifier);
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		
		eventTypeEnvironment = repository.getTypeEnvironment();
		
		IMachineHypothesisManager machineHypothesisManager =
			(IMachineHypothesisManager) repository.getState(IMachineHypothesisManager.STATE_TYPE);
		
		ISCEvent concreteEvent = (ISCEvent) element;
		
		boolean accurate = concreteEvent.isAccurate();
		
		ISCGuard[] guards = concreteEvent.getSCGuards();
		
		ISCEvent[] abstractEvents = concreteEvent.getAbstractSCEvents();
		
		for (ISCEvent event : abstractEvents) {
			accurate &= event.isAccurate();
		}
		
		setEventHypothesisManager(
				machineHypothesisManager, concreteEvent, guards, accurate, repository, monitor);
		
		fetchGuards(
				concreteEvent,
				abstractEvents,
				guards, 
				repository);
		
		fetchActionsAndVariables(concreteEvent, repository);
		
		fetchWitnesses(concreteEvent, repository, monitor);

	}

	private void fetchWitnesses(ISCEvent concreteEvent,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException, RodinDBException {
		variableTable = (IMachineVariableTable) repository.getState(IMachineVariableTable.STATE_TYPE);

		ITypeEnvironmentBuilder largeEnv = eventTypeEnvironment.makeBuilder();
		List<FreeIdentifier> variables = variableTable.getVariables();
		for (FreeIdentifier identifier : variables) {
			largeEnv.add(identifier.withPrime(factory));
		}
		witnessTable = 
			new EventWitnessTable(concreteEvent.getSCWitnesses(), largeEnv, factory, monitor);
		
		List<Predicate> predicates = witnessTable.getPredicates();
		for (Predicate predicate : predicates) {
			FreeIdentifier[] identifiers = predicate.getFreeIdentifiers();
			for (FreeIdentifier identifier : identifiers) {
				if (identifier.isPrimed() && !eventTypeEnvironment.contains(identifier.getName())) {
					eventTypeEnvironment.add(identifier);
					eventHypothesisManager.addIdentifier(identifier);
				}
			}
		}
		
		witnessTable.makeImmutable();
		repository.setState(witnessTable);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		eventHypothesisManager.createHypotheses(monitor);

		eventHypothesisManager = null;
		abstractEventGuardList = null;
		eventTypeEnvironment = null;
		variableTable = null;
		factory = null;
		
		super.endModule(element, repository, monitor);
	}

}
