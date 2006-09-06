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
import org.eventb.core.IPOFile;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCVariable;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.pog.IConcreteEventGuardTable;
import org.eventb.core.pog.IEventHypothesisManager;
import org.eventb.core.pog.IIdentifierTable;
import org.eventb.core.pog.IMachineHypothesisManager;
import org.eventb.core.pog.Module;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.ITypingState;
import org.eventb.internal.core.pog.ConcreteEventGuardTable;
import org.eventb.internal.core.pog.EventHypothesisManager;
import org.eventb.internal.core.pog.IdentifierTable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class EventHypothesisModule extends Module {

	IMachineHypothesisManager machineHypothesisManager;
	IEventHypothesisManager eventHypothesisManager;
	IIdentifierTable identifierTable;
	IConcreteEventGuardTable eventGuardTable;
	ITypeEnvironment eventTypeEnvironment;
	FormulaFactory factory;

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IProcessorModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(IRodinElement element, IPOFile target,
			IStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub

	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(IRodinElement element, IPOFile target, IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		super.initModule(element, target, repository, monitor);
		
		factory = repository.getFormulaFactory();
		
		identifierTable = new IdentifierTable();
		
		repository.setState(identifierTable);
	
		ITypingState typingState =
			(ITypingState) repository.getState(ITypingState.STATE_TYPE);
		eventTypeEnvironment = typingState.getTypeEnvironment();
		machineHypothesisManager =
			(IMachineHypothesisManager) repository.getState(IMachineHypothesisManager.STATE_TYPE);
		
		ISCEvent event = (ISCEvent) element;
		
		fetchVariables(eventTypeEnvironment, event.getSCVariables());
		
		ISCGuard[] guards = event.getSCGuards();
		
		eventGuardTable = new ConcreteEventGuardTable(guards, eventTypeEnvironment, factory);
		repository.setState(eventGuardTable);
		
		eventHypothesisManager = new EventHypothesisManager(
				event, guards, machineHypothesisManager.getFullHypothesisName());
		
		repository.setState(eventHypothesisManager);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(IRodinElement element, IPOFile target, IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		super.endModule(element, target, repository, monitor);
		
		eventHypothesisManager.createHypotheses(target, monitor);

		identifierTable = null;
		eventTypeEnvironment = null;
		machineHypothesisManager = null;
		factory = null;
	}
	
	void fetchVariables(
			ITypeEnvironment typeEnvironment, 
			ISCVariable[] variables) throws RodinDBException {
		for (ISCVariable variable : variables) {
			FreeIdentifier identifier = 
				factory.makeFreeIdentifier(
						variable.getIdentifierName(), null, 
						variable.getType(factory));
			typeEnvironment.addName(identifier.getName(), identifier.getType());
			identifierTable.addIdentifier(identifier);
		}
	}

}
