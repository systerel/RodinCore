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
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.ISCEvent;
import org.eventb.core.pog.state.IConcreteEventActionTable;
import org.eventb.core.pog.state.IEventHypothesisManager;
import org.eventb.core.pog.state.IMachineHypothesisManager;
import org.eventb.core.pog.state.IStatePOG;
import org.eventb.core.state.IStateRepository;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class MachineEventActionModule extends UtilityModule {

	protected IMachineHypothesisManager machineHypothesisManager;
	protected IEventHypothesisManager eventHypothesisManager;
	protected ISCEvent concreteEvent;
	protected String concreteEventLabel;
	protected boolean isInitialisation;
	protected IPOPredicateSet fullHypothesis;
	
	protected IConcreteEventActionTable concreteEventActionTable;
	
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
		machineHypothesisManager =
			(IMachineHypothesisManager) repository.getState(IMachineHypothesisManager.STATE_TYPE);
		eventHypothesisManager = 
			(IEventHypothesisManager) repository.getState(IEventHypothesisManager.STATE_TYPE);
		
		concreteEvent = (ISCEvent) element;
		concreteEventLabel = concreteEvent.getLabel(monitor);
		isInitialisation = concreteEventLabel.equals("INITIALISATION");
		fullHypothesis = eventHypothesisManager.getFullHypothesis(target);
		
		concreteEventActionTable =
			(IConcreteEventActionTable) repository.getState(IConcreteEventActionTable.STATE_TYPE);
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
		machineHypothesisManager = null;
		concreteEventActionTable = null;
		concreteEvent = null;
		concreteEventLabel = null;
		fullHypothesis = null;
		super.endModule(element, target, repository, monitor);
	}

}
