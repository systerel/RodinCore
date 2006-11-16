/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCVariable;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.pog.Module;
import org.eventb.core.pog.state.IAbstractEventActionTable;
import org.eventb.core.pog.state.IConcreteEventActionTable;
import org.eventb.core.pog.state.IConcreteEventGuardTable;
import org.eventb.core.pog.state.IEventHypothesisManager;
import org.eventb.core.pog.state.IMachineHypothesisManager;
import org.eventb.core.pog.state.IMachineVariableTable;
import org.eventb.core.pog.state.IStatePOG;
import org.eventb.core.pog.state.ITypingState;
import org.eventb.core.pog.state.IWitnessTable;
import org.eventb.core.state.IStateRepository;
import org.eventb.internal.core.pog.AbstractEventActionTable;
import org.eventb.internal.core.pog.ConcreteEventActionTable;
import org.eventb.internal.core.pog.ConcreteEventGuardTable;
import org.eventb.internal.core.pog.EventHypothesisManager;
import org.eventb.internal.core.pog.WitnessTable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventHypothesisModule extends Module {

	IEventHypothesisManager eventHypothesisManager;
	ITypeEnvironment eventTypeEnvironment;
	FormulaFactory factory;

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IProcessorModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IPOFile target,
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor)
			throws CoreException {
		// all processing is done in the initModule() method

	}
	
	private void fetchActionsAndVariables(
			ISCEvent concreteEvent, 
			ISCEvent abstractEvent, 
			IStateRepository<IStatePOG> repository) throws CoreException {
		IMachineVariableTable machineVariableTable =
			(IMachineVariableTable) repository.getState(IMachineVariableTable.STATE_TYPE);
		
		IAbstractEventActionTable abstractEventActionTable = 
			new AbstractEventActionTable(
					(abstractEvent == null ? new ISCAction[0] : abstractEvent.getSCActions(null)), 
					eventTypeEnvironment, 
					machineVariableTable,
					factory);
		repository.setState(abstractEventActionTable);
		IConcreteEventActionTable concreteEventActionTable =
			new ConcreteEventActionTable(
					concreteEvent.getSCActions(null), 
					eventTypeEnvironment, 
					machineVariableTable, 
					factory);
		repository.setState(concreteEventActionTable);
		
		if (abstractEvent != null)
			fetchVariables(abstractEvent.getSCVariables(null));
		
		fetchPostValueVariables(concreteEventActionTable.getAssignedVariables());
		fetchPostValueVariables(abstractEventActionTable.getAssignedVariables());
	}

	private void setEventHypothesisManager(
			IPOFile target,
			IMachineHypothesisManager machineHypothesisManager, 
			ISCEvent event, ISCGuard[] guards, 
			IStateRepository<IStatePOG> repository,
			IProgressMonitor monitor) throws CoreException {
		IPOPredicateSet fullHypothesis = (event.getLabel(monitor).equals("INITIALISATION")) ?
				machineHypothesisManager.getContextHypothesis(target) :
				machineHypothesisManager.getFullHypothesis(target);
		eventHypothesisManager = new EventHypothesisManager(event, guards, fullHypothesis.getElementName());
		
		eventHypothesisManager.setAbstractEvents(event.getAbstractSCEvents(null));
		
		repository.setState(eventHypothesisManager);
	}

	private void fetchGuards(
			ISCGuard[] guards, 
			IStateRepository<IStatePOG> repository) throws CoreException {
		IConcreteEventGuardTable eventGuardTable = 
			new ConcreteEventGuardTable(guards, eventTypeEnvironment, factory);
		repository.setState(eventGuardTable);
	}

	private void fetchPostValueVariables(Set<FreeIdentifier> identifiers) {
		for (FreeIdentifier identifier : identifiers) {
			FreeIdentifier primedIdentifier = identifier.withPrime(factory);
			if (eventTypeEnvironment.contains(primedIdentifier.getName()))
				continue;
			eventHypothesisManager.addIdentifier(primedIdentifier);
			eventTypeEnvironment.addName(primedIdentifier.getName(), primedIdentifier.getType());
		}
	}

	private void fetchVariables(ISCVariable[] variables) throws RodinDBException {
		for (ISCVariable variable : variables) {
			FreeIdentifier identifier = 
				factory.makeFreeIdentifier(
						variable.getIdentifierString(null), null, 
						variable.getType(factory, null));
			eventTypeEnvironment.addName(identifier.getName(), identifier.getType());
			eventHypothesisManager.addIdentifier(identifier);
		}
	}

	private void fetchWitnesses(
			ISCEvent concreteEvent, 
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor) throws CoreException, RodinDBException {
		IWitnessTable witnessTable = 
			new WitnessTable(concreteEvent.getSCWitnesses(null), eventTypeEnvironment, factory, monitor);
		repository.setState(witnessTable);
	}

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
		
		factory = repository.getFormulaFactory();
		
		ITypingState typingState =
			(ITypingState) repository.getState(ITypingState.STATE_TYPE);
		eventTypeEnvironment = typingState.getTypeEnvironment();
		IMachineHypothesisManager machineHypothesisManager =
			(IMachineHypothesisManager) repository.getState(IMachineHypothesisManager.STATE_TYPE);
		
		ISCEvent concreteEvent = (ISCEvent) element;
		
		ISCGuard[] guards = concreteEvent.getSCGuards(null);
		
		setEventHypothesisManager(
				target, machineHypothesisManager, concreteEvent, guards, repository, monitor);
		
		fetchVariables(concreteEvent.getSCVariables(null));
		
		fetchGuards(guards, repository);
		
		ISCEvent abstractEvent = eventHypothesisManager.getFirstAbstractEvent();
		
		fetchActionsAndVariables(concreteEvent, abstractEvent, repository);
		
		fetchWitnesses(concreteEvent, repository, monitor);
	
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
		
		eventHypothesisManager.createHypotheses(target, monitor);

		eventTypeEnvironment = null;
		factory = null;
	}

}
