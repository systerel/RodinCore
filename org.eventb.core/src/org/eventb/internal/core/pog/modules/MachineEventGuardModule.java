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
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IAbstractEventGuardTable;
import org.eventb.core.pog.state.IConcreteEventGuardTable;
import org.eventb.core.pog.state.IEventHypothesisManager;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.pog.state.IPredicateTable;
import org.eventb.core.pog.state.IStatePOG;
import org.eventb.core.pog.state.ITypingState;
import org.eventb.core.state.IStateRepository;
import org.eventb.internal.core.pog.AbstractEventGuardTable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventGuardModule extends PredicateModule {

	public static final String MACHINE_EVENT_GUARD_MODULE = 
		EventBPlugin.PLUGIN_ID + ".machineEventGuardModule";

	String eventLabel;
	IAbstractEventGuardTable abstractEventGuardTable;
	
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
		ISCEvent event = (ISCEvent) element;
		eventLabel = event.getLabel();
		ITypeEnvironment eventTypeEnvironment =
			((ITypingState) repository.getState(ITypingState.STATE_TYPE)).getTypeEnvironment();

		ISCEvent abstractEvent = 
			((IEventHypothesisManager) hypothesisManager).getFirstAbstractEvent();
		abstractEventGuardTable = 
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
		eventLabel = null;
		abstractEventGuardTable = null;
		super.endModule(element, target, repository, monitor);
	}

	@Override
	protected IHypothesisManager getHypothesisManager(IStateRepository<IStatePOG> repository) throws CoreException {
		return (IEventHypothesisManager) repository.getState(IEventHypothesisManager.STATE_TYPE);
	}

	@Override
	protected IPredicateTable getPredicateTable(IStateRepository<IStatePOG> repository) throws CoreException {
		return (IConcreteEventGuardTable) repository.getState(IConcreteEventGuardTable.STATE_TYPE);
	}

	@Override
	protected void createWDProofObligation(
			IPOFile target, 
			String elementLabel, 
			ISCPredicateElement predicateElement, 
			Predicate predicate, 
			IProgressMonitor monitor) throws RodinDBException {
		
		if (abstractEventGuardTable.contains(predicate)) // already proved in abstraction
			return;
		
		super.createWDProofObligation(target, elementLabel, predicateElement,
				predicate, monitor);
	}

	@Override
	protected String getWDProofObligationDescription() {
		return "Well-definedness of Guard";
	}

	@Override
	protected String getWDProofObligationName(String elementLabel) {
		return eventLabel + "/" + elementLabel + "/WD";
	}

}
