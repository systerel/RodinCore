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
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCVariable;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.pog.IAbstractEventActionTable;
import org.eventb.core.pog.IConcreteEventActionTable;
import org.eventb.core.pog.IConcreteEventGuardTable;
import org.eventb.core.pog.IEventHypothesisManager;
import org.eventb.core.pog.IIdentifierTable;
import org.eventb.core.pog.IMachineHypothesisManager;
import org.eventb.core.pog.IMachineVariableTable;
import org.eventb.core.pog.IWitnessTable;
import org.eventb.core.pog.Module;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.ITypingState;
import org.eventb.internal.core.pog.AbstractEventActionTable;
import org.eventb.internal.core.pog.ConcreteEventActionTable;
import org.eventb.internal.core.pog.ConcreteEventGuardTable;
import org.eventb.internal.core.pog.EventHypothesisManager;
import org.eventb.internal.core.pog.IdentifierTable;
import org.eventb.internal.core.pog.WitnessTable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventHypothesisModule extends Module {

	IEventHypothesisManager eventHypothesisManager;
	IIdentifierTable eventIdentifierTable;
	ITypeEnvironment eventTypeEnvironment;
	FormulaFactory factory;

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IProcessorModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(IRodinElement element, IPOFile target,
			IStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		// all processing is done in the initModule() method

	}
	
	private void fetchActionsAndVariables(
			ISCEvent concreteEvent, 
			ISCEvent abstractEvent, 
			IStateRepository repository) throws CoreException {
		IMachineVariableTable machineVariableTable =
			(IMachineVariableTable) repository.getState(IMachineVariableTable.STATE_TYPE);
		
		IAbstractEventActionTable abstractEventActionTable = 
			new AbstractEventActionTable(
					(abstractEvent == null ? new ISCAction[0] : abstractEvent.getSCActions()), 
					eventTypeEnvironment, 
					machineVariableTable,
					factory);
		repository.setState(abstractEventActionTable);
		IConcreteEventActionTable concreteEventActionTable =
			new ConcreteEventActionTable(
					concreteEvent.getSCActions(), 
					eventTypeEnvironment, 
					machineVariableTable, 
					factory);
		repository.setState(concreteEventActionTable);
		
		if (abstractEvent != null)
			fetchVariables(abstractEvent.getSCVariables());
		
		fetchPostValueVariables(concreteEventActionTable.getAssignedVariables());
		fetchPostValueVariables(abstractEventActionTable.getAssignedVariables());
	}

	private void setEventHypothesisManager(
			IMachineHypothesisManager machineHypothesisManager, 
			ISCEvent event, ISCGuard[] guards, 
			IStateRepository repository) throws CoreException {
		eventHypothesisManager = new EventHypothesisManager(
				event, guards, machineHypothesisManager.getFullHypothesisName());
		
		eventHypothesisManager.setAbstractEvents(event.getAbstractSCEvents());
		
		repository.setState(eventHypothesisManager);
	}

	private void fetchGuards(
			ISCGuard[] guards, 
			IStateRepository repository) throws CoreException {
		IConcreteEventGuardTable eventGuardTable = 
			new ConcreteEventGuardTable(guards, eventTypeEnvironment, factory);
		repository.setState(eventGuardTable);
	}

	private void fetchPostValueVariables(Set<FreeIdentifier> identifiers) {
		for (FreeIdentifier identifier : identifiers) {
			FreeIdentifier primedIdentifier = identifier.withPrime(factory);
			if (eventTypeEnvironment.contains(primedIdentifier.getName()))
				continue;
			eventIdentifierTable.addIdentifier(primedIdentifier);
			eventTypeEnvironment.addName(primedIdentifier.getName(), primedIdentifier.getType());
		}
	}

	private void fetchVariables(ISCVariable[] variables) throws RodinDBException {
		for (ISCVariable variable : variables) {
			FreeIdentifier identifier = 
				factory.makeFreeIdentifier(
						variable.getIdentifierName(), null, 
						variable.getType(factory));
			eventTypeEnvironment.addName(identifier.getName(), identifier.getType());
			eventIdentifierTable.addIdentifier(identifier);
		}
	}

	private void fetchWitnesses(ISCEvent concreteEvent, IStateRepository repository, IProgressMonitor monitor) throws CoreException, RodinDBException {
		IWitnessTable witnessTable = 
			new WitnessTable(concreteEvent.getSCWitnesses(), eventTypeEnvironment, factory, monitor);
		repository.setState(witnessTable);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IPOFile target, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, target, repository, monitor);
		
		factory = repository.getFormulaFactory();
		
		eventIdentifierTable = new IdentifierTable();
		
		repository.setState(eventIdentifierTable);
	
		ITypingState typingState =
			(ITypingState) repository.getState(ITypingState.STATE_TYPE);
		eventTypeEnvironment = typingState.getTypeEnvironment();
		IMachineHypothesisManager machineHypothesisManager =
			(IMachineHypothesisManager) repository.getState(IMachineHypothesisManager.STATE_TYPE);
		
		ISCEvent concreteEvent = (ISCEvent) element;
		
		ISCGuard[] guards = concreteEvent.getSCGuards();
		
		fetchVariables(concreteEvent.getSCVariables());
		
		fetchGuards(guards, repository);
		
		setEventHypothesisManager(machineHypothesisManager, concreteEvent, guards, repository);
		
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
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.endModule(element, target, repository, monitor);
		
		eventHypothesisManager.createHypotheses(target, monitor);

		eventIdentifierTable = null;
		eventTypeEnvironment = null;
		factory = null;
	}

}
