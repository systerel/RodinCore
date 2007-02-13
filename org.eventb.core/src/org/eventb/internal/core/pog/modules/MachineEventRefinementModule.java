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
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ISCWitness;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IAbstractEventActionTable;
import org.eventb.core.pog.state.IAbstractEventGuardList;
import org.eventb.core.pog.state.IEventWitnessTable;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.pog.util.POGPredicate;
import org.eventb.core.pog.util.POGTraceablePredicate;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class MachineEventRefinementModule extends MachineEventActionUtilityModule {
	
	protected IAbstractEventGuardList abstractEventGuardList;
	protected IAbstractEventActionTable abstractEventActionTable;
	protected IEventWitnessTable witnessTable;

	protected ArrayList<POGPredicate> makeActionAndWitnessHypothesis(Predicate predicate) {
		// create local hypothesis for nondeterministic assignments
		
		ArrayList<POGPredicate> hyp = newLocalHypothesis();
		Set<FreeIdentifier> freeIdents = newFreeIdentsFromPredicate(predicate);
		
		makeWitnessHypothesis(hyp, freeIdents);
		addFreeIdentsFromHypothesis(freeIdents, hyp);
		
		makeActionHypothesis(hyp, freeIdents);
		
		return hyp;		
	}
	
	@Override
	protected ArrayList<POGPredicate> newLocalHypothesis() {
		int size = 
			witnessTable.getNondetWitnesses().size() +
			concreteEventActionTable.getNondetActions().size();
		return new ArrayList<POGPredicate>(size);
	}
	
	private void addFreeIdentsFromHypothesis(
			Set<FreeIdentifier> identSet, List<POGPredicate> hyp) {
		for (POGPredicate predicate : hyp) {
			addAllFreeIdents(identSet, predicate.getPredicate().getFreeIdentifiers());
		}
	}
	
	protected ArrayList<POGPredicate> makeWitnessHypothesis() {
		// create local hypothesis for nondeterministic assignments
		List<ISCWitness> nondetWitnesses = witnessTable.getNondetWitnesses();
		List<Predicate> nondetPredicates = witnessTable.getNondetPredicates();
		
		ArrayList<POGPredicate> hyp = 
			new ArrayList<POGPredicate>(nondetWitnesses.size());

		for (int i=0; i<nondetWitnesses.size(); i++) {
			hyp.add(
					new POGTraceablePredicate(nondetPredicates.get(i),
							nondetWitnesses.get(i)));
		}
		
		return hyp;
	}
	
	private void makeWitnessHypothesis(
			ArrayList<POGPredicate> hyp, 
			Set<FreeIdentifier> freeIdents) {
		// create local hypothesis for nondeterministic assignments
		List<ISCWitness> nondetWitnesses = witnessTable.getNondetWitnesses();
		List<FreeIdentifier> nondetLabels = witnessTable.getNondetVariables();
		List<Predicate> nondetPredicates = witnessTable.getNondetPredicates();
			
		for (int i=0; i<nondetWitnesses.size(); i++) {
			if (freeIdents.contains(nondetLabels.get(i))) {
				Predicate hypPred = nondetPredicates.get(i);
				hyp.add(new POGTraceablePredicate(
								hypPred,
								nondetWitnesses.get(i)));
			}
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
		abstractEventGuardList =
			(IAbstractEventGuardList) repository.getState(IAbstractEventGuardList.STATE_TYPE);
		abstractEventActionTable = 
			(IAbstractEventActionTable) repository.getState(IAbstractEventActionTable.STATE_TYPE);
		witnessTable =
			(IEventWitnessTable) repository.getState(IEventWitnessTable.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		abstractEventGuardList = null;
		abstractEventActionTable = null;
		witnessTable = null;
		super.endModule(element, repository, monitor);
	}

}
