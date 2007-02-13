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
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IAbstractEventGuardList;
import org.eventb.core.pog.state.IConcreteEventGuardTable;
import org.eventb.core.pog.state.IEventHypothesisManager;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.pog.state.IMachineInfo;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.pog.state.IPredicateTable;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class MachineEventGuardModule extends PredicateModule<ISCGuard> {

	@Override
	public void process(IRodinElement element, IPOGStateRepository repository, IProgressMonitor monitor) throws CoreException {
		if (!isApplicable())
			return;
		super.process(element, repository, monitor);
	}

	public static final String MACHINE_EVENT_GUARD_MODULE = 
		EventBPlugin.PLUGIN_ID + ".machineEventGuardModule";

	protected String eventLabel;
	protected IAbstractEventGuardList abstractEventGuardList;
	protected IMachineInfo machineInfo;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		ISCEvent event = (ISCEvent) element;
		eventLabel = event.getLabel();
		machineInfo = (IMachineInfo) repository.getState(IMachineInfo.STATE_TYPE);
		abstractEventGuardList = 
			(IAbstractEventGuardList) repository.getState(IAbstractEventGuardList.STATE_TYPE);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		eventLabel = null;
		machineInfo = null;
		abstractEventGuardList = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	protected IHypothesisManager getHypothesisManager(IPOGStateRepository repository) throws CoreException {
		return (IEventHypothesisManager) repository.getState(IEventHypothesisManager.STATE_TYPE);
	}

	@Override
	protected IPredicateTable<ISCGuard> getPredicateTable(IPOGStateRepository repository) throws CoreException {
		return (IConcreteEventGuardTable) repository.getState(IConcreteEventGuardTable.STATE_TYPE);
	}

	@Override
	protected void createWDProofObligation(
			IPOFile target, 
			String elementLabel, 
			ISCGuard predicateElement, 
			Predicate predicate, 
			int index,
			IProgressMonitor monitor) throws CoreException {
		
		if (isRedundantWDProofObligation(predicate, index))
			return;
		
		super.createWDProofObligation(target, elementLabel, predicateElement,
				predicate, index, monitor);
	}
	
	protected abstract boolean isApplicable();
	
	protected abstract boolean isRedundantWDProofObligation(Predicate predicate, int index);

	@Override
	protected String getWDProofObligationDescription() {
		return "Well-definedness of Guard";
	}

	@Override
	protected String getWDProofObligationName(String elementLabel) {
		return eventLabel + "/" + elementLabel + "/WD";
	}

}
