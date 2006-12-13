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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCWitness;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.POGPredicate;
import org.eventb.core.pog.state.IAbstractEventActionTable;
import org.eventb.core.pog.state.IAbstractEventGuardList;
import org.eventb.core.pog.state.IStatePOG;
import org.eventb.core.pog.state.IWitnessTable;
import org.eventb.core.state.IStateRepository;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class MachineEventRefinementModule extends MachineEventActionUtilityModule {
	
	protected IAbstractEventGuardList abstractEventGuardList;
	protected IAbstractEventActionTable abstractEventActionTable;
	protected IWitnessTable witnessTable;

	@Deprecated
	protected ArrayList<POGPredicate> makeActionHypothesis(HashSet<FreeIdentifier> freeIdents) {
		// create local hypothesis for nondeterministic assignments
		
		List<Assignment> nondetAssignments = concreteEventActionTable.getNondetAssignments();
		List<ISCAction> nondetActions = concreteEventActionTable.getNondetActions();
		
		ArrayList<POGPredicate> hyp = 
			new ArrayList<POGPredicate>(nondetAssignments.size());
		
		for (int i=0; i<nondetAssignments.size(); i++) {
			for (FreeIdentifier ident : nondetAssignments.get(i).getAssignedIdentifiers()) {
				if (freeIdents.contains(ident)) {
					hyp.add(
							new POGPredicate(nondetActions.get(i),
									nondetAssignments.get(i).getBAPredicate(factory)));
					break;
				}
			}
		
		}
		return hyp;
	}

	protected ArrayList<POGPredicate> makeActionHypothesis() {
		// create local hypothesis for nondeterministic assignments
		
		List<Assignment> nondetAssignments = concreteEventActionTable.getNondetAssignments();
		List<ISCAction> nondetActions = concreteEventActionTable.getNondetActions();
		
		ArrayList<POGPredicate> hyp = 
			new ArrayList<POGPredicate>(nondetAssignments.size());
		
		for (int i=0; i<nondetAssignments.size(); i++) {
			hyp.add(
					new POGPredicate(
							nondetActions.get(i),
							nondetAssignments.get(i).getBAPredicate(factory)));
		}
		return hyp;		
	}
	
	protected ArrayList<POGPredicate> makeWitnessHypothesis() {
		// create local hypothesis for nondeterministic assignments
		List<ISCWitness> nondetWitnesses = witnessTable.getNondetWitnesses();
		List<Predicate> nondetPredicates = witnessTable.getNondetPredicates();
		
		ArrayList<POGPredicate> hyp = 
			new ArrayList<POGPredicate>(nondetWitnesses.size());

		for (int i=0; i<nondetWitnesses.size(); i++) {
			hyp.add(
					new POGPredicate(nondetWitnesses.get(i),
							nondetPredicates.get(i)));
		}
		
		return hyp;
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
		abstractEventGuardList =
			(IAbstractEventGuardList) repository.getState(IAbstractEventGuardList.STATE_TYPE);
		abstractEventActionTable = 
			(IAbstractEventActionTable) repository.getState(IAbstractEventActionTable.STATE_TYPE);
		witnessTable =
			(IWitnessTable) repository.getState(IWitnessTable.STATE_TYPE);
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
		abstractEventGuardList = null;
		abstractEventActionTable = null;
		witnessTable = null;
		super.endModule(element, target, repository, monitor);
	}

}
