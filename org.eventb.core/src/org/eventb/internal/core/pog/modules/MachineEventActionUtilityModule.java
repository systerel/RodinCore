/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IEvent;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IConcreteEventActionTable;
import org.eventb.core.pog.state.IEventHypothesisManager;
import org.eventb.core.pog.state.IMachineHypothesisManager;
import org.eventb.core.pog.state.IMachineInfo;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.pog.util.POGIntervalSelectionHint;
import org.eventb.core.pog.util.POGPredicate;
import org.eventb.core.pog.util.POGTraceablePredicate;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class MachineEventActionUtilityModule extends UtilityModule {

	protected IMachineInfo machineInfo;
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
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		machineInfo = (IMachineInfo) repository.getState(IMachineInfo.STATE_TYPE);
		machineHypothesisManager =
			(IMachineHypothesisManager) repository.getState(IMachineHypothesisManager.STATE_TYPE);
		eventHypothesisManager = 
			(IEventHypothesisManager) repository.getState(IEventHypothesisManager.STATE_TYPE);
		
		concreteEvent = (ISCEvent) element;
		concreteEventLabel = concreteEvent.getLabel();
		isInitialisation = concreteEventLabel.equals(IEvent.INITIALISATION);
		fullHypothesis = eventHypothesisManager.getFullHypothesis();
		
		concreteEventActionTable =
			(IConcreteEventActionTable) repository.getState(IConcreteEventActionTable.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		machineInfo = null;
		eventHypothesisManager = null;
		machineHypothesisManager = null;
		concreteEventActionTable = null;
		concreteEvent = null;
		concreteEventLabel = null;
		fullHypothesis = null;
		super.endModule(element, repository, monitor);
	}

	protected Set<FreeIdentifier> addAllFreeIdents(
			Set<FreeIdentifier> identSet, FreeIdentifier[] identifiers) {
		for (FreeIdentifier identifier : identifiers) {
			identSet.add(identifier);
		}
		return identSet;
	}
	
	protected Set<FreeIdentifier> newFreeIdentsFromPredicate(Predicate predicate) {
		FreeIdentifier[] identifiers = predicate.getFreeIdentifiers();
		HashSet<FreeIdentifier> identSet = 
			new HashSet<FreeIdentifier>(identifiers.length * 16 / 3 + 1);
		return addAllFreeIdents(identSet, identifiers);
	}

	protected ArrayList<POGPredicate> newLocalHypothesis() {
		int size = concreteEventActionTable.getNondetActions().size();
		return new ArrayList<POGPredicate>(size);
	}
	
	protected POGIntervalSelectionHint getLocalHypothesisSelectionHint(IPOFile target, String sequentName) throws RodinDBException {
		return new POGIntervalSelectionHint(
				eventHypothesisManager.getRootHypothesis(),
				getSequentHypothesis(target, sequentName));
	}

	protected void makeActionHypothesis(ArrayList<POGPredicate> hyp, Set<FreeIdentifier> freeIdents) {
		// create local hypothesis for nondeterministic assignments
		
		List<Predicate> nondetPredicates = concreteEventActionTable.getNondetPredicates();
		List<ISCAction> nondetActions = concreteEventActionTable.getNondetActions();
		
		for (int i=0; i<nondetPredicates.size(); i++) {
			Predicate baPredicate = nondetPredicates.get(i);
			for (FreeIdentifier ident : baPredicate.getFreeIdentifiers()) {
				if (ident.isPrimed() && freeIdents.contains(ident)) {
					hyp.add(new POGPredicate(
									baPredicate,
									nondetActions.get(i)));
					break;
				}
			}
		
		}
		
	}

	protected ArrayList<POGPredicate> makeActionHypothesis() {
		// create local hypothesis for nondeterministic assignments
		
		List<Assignment> nondetAssignments = concreteEventActionTable.getNondetAssignments();
		List<ISCAction> nondetActions = concreteEventActionTable.getNondetActions();
		
		ArrayList<POGPredicate> hyp = 
			new ArrayList<POGPredicate>(nondetAssignments.size());
		
		for (int i=0; i<nondetAssignments.size(); i++) {
			hyp.add(
					new POGTraceablePredicate(
							nondetAssignments.get(i).getBAPredicate(factory),
							nondetActions.get(i)));
		}
		return hyp;		
	}

	protected ArrayList<POGPredicate> makeActionHypothesis(Predicate predicate) {
		// create local hypothesis for nondeterministic assignments
		
		ArrayList<POGPredicate> hyp = newLocalHypothesis();
		Set<FreeIdentifier> freeIdents = newFreeIdentsFromPredicate(predicate);
		
		makeActionHypothesis(hyp, freeIdents);
		
		return hyp;		
	}

}
