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
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IAbstractEventGuardList;
import org.eventb.core.pog.state.IAbstractEventGuardTable;
import org.eventb.core.pog.state.IConcreteEventGuardTable;
import org.eventb.core.pog.state.IEventHypothesisManager;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.pog.state.IPredicateTable;
import org.eventb.core.pog.state.IPOGState;
import org.eventb.core.tool.state.IStateRepository;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventGuardModule extends PredicateModule {

	public static final String MACHINE_EVENT_GUARD_MODULE = 
		EventBPlugin.PLUGIN_ID + ".machineEventGuardModule";

	String eventLabel;
	IAbstractEventGuardList abstractEventGuardList;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IPOFile target, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, target, repository, monitor);
		ISCEvent event = (ISCEvent) element;
		eventLabel = event.getLabel();
		abstractEventGuardList = 
			(IAbstractEventGuardList) repository.getState(IAbstractEventGuardList.STATE_TYPE);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IPOFile target, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		eventLabel = null;
		abstractEventGuardList = null;
		super.endModule(element, target, repository, monitor);
	}

	@Override
	protected IHypothesisManager getHypothesisManager(IStateRepository<IPOGState> repository) throws CoreException {
		return (IEventHypothesisManager) repository.getState(IEventHypothesisManager.STATE_TYPE);
	}

	@Override
	protected IPredicateTable getPredicateTable(IStateRepository<IPOGState> repository) throws CoreException {
		return (IConcreteEventGuardTable) repository.getState(IConcreteEventGuardTable.STATE_TYPE);
	}

	@Override
	protected void createWDProofObligation(
			IPOFile target, 
			String elementLabel, 
			ISCPredicateElement predicateElement, 
			Predicate predicate, 
			int index,
			IProgressMonitor monitor) throws CoreException {
		
		if (isRedundantWDProofObligation(predicate, index))
			return;
		
		super.createWDProofObligation(target, elementLabel, predicateElement,
				predicate, index, monitor);
	}
	
	private boolean isRedundantWDProofObligation(Predicate predicate, int index) {
		
		List<IAbstractEventGuardTable> abstractEventGuardTables = 
			abstractEventGuardList.getAbstractEventGuardTables();
		
		for (IAbstractEventGuardTable abstractEventGuardTable : abstractEventGuardTables) {
		
			if (isFreshPOForAbstractGuard(predicate, index, abstractEventGuardTable))
				continue;
			
			return true;
		}
		return false;
	}

	private boolean isFreshPOForAbstractGuard(Predicate predicate, int index, IAbstractEventGuardTable abstractEventGuardTable) {
		int absIndex = abstractEventGuardTable.getPredicates().indexOf(predicate);

		if (absIndex == -1)
			return true;

		for (int k=0; k<absIndex; k++) {
		
			int indexOfConcrete = abstractEventGuardTable.getIndexOfCorrespondingConcrete(k);
		
			if (indexOfConcrete != -1 && indexOfConcrete < index)
				continue;
		
			return true;
		}
		return false;
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
