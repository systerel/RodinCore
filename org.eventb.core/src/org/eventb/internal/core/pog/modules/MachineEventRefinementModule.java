/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ISCWitness;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGPredicate;
import org.eventb.core.pog.state.IAbstractEventActionTable;
import org.eventb.core.pog.state.IAbstractEventGuardList;
import org.eventb.core.pog.state.IEventWitnessTable;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class MachineEventRefinementModule extends MachineEventActionUtilityModule {
	
	protected IAbstractEventGuardList abstractEventGuardList;
	protected IAbstractEventActionTable abstractEventActionTable;
	protected IEventWitnessTable witnessTable;

	protected ArrayList<IPOGPredicate> makeActionAndWitnessHypothesis(Predicate predicate) 
	throws RodinDBException {
		// create local hypothesis for nondeterministic assignments
		
		ArrayList<IPOGPredicate> hyp = newLocalHypothesis();
		Set<FreeIdentifier> freeIdents = newFreeIdentsFromPredicate(predicate);
		
		makeWitnessHypothesis(hyp, freeIdents);
		addFreeIdentsFromHypothesis(freeIdents, hyp);
		
		makeActionHypothesis(hyp, freeIdents);
		
		return hyp;		
	}
	
	@Override
	protected ArrayList<IPOGPredicate> newLocalHypothesis() {
		int size = 
			witnessTable.getNondetWitnesses().size() +
			concreteEventActionTable.getNondetActions().size();
		return new ArrayList<IPOGPredicate>(size);
	}
	
	private void addFreeIdentsFromHypothesis(
			Set<FreeIdentifier> identSet, List<IPOGPredicate> hyp) {
		for (IPOGPredicate predicate : hyp) {
			addAllFreeIdents(identSet, predicate.getPredicate().getFreeIdentifiers());
		}
	}
	
	protected ArrayList<IPOGPredicate> makeWitnessHypothesis() throws RodinDBException {
		// create local hypothesis for nondeterministic assignments
		List<ISCWitness> nondetWitnesses = witnessTable.getNondetWitnesses();
		List<Predicate> nondetPredicates = witnessTable.getNondetPredicates();
		
		ArrayList<IPOGPredicate> hyp = 
			new ArrayList<IPOGPredicate>(nondetWitnesses.size());

		for (int i=0; i<nondetWitnesses.size(); i++) {
			hyp.add(
					makePredicate(nondetPredicates.get(i),
							nondetWitnesses.get(i).getSource()));
		}
		
		return hyp;
	}
	
	private void makeWitnessHypothesis(
			ArrayList<IPOGPredicate> hyp, 
			Set<FreeIdentifier> freeIdents) throws RodinDBException {
		// create local hypothesis for nondeterministic assignments
		List<ISCWitness> nondetWitnesses = witnessTable.getNondetWitnesses();
		List<FreeIdentifier> nondetLabels = witnessTable.getNondetVariables();
		List<Predicate> nondetPredicates = witnessTable.getNondetPredicates();
		
		List<BecomesEqualTo> concDetAssignments = concreteEventActionTable.getPrimedDetAssignments();
		
		List<BecomesEqualTo> subst = new ArrayList<BecomesEqualTo>(concDetAssignments.size() + 1);
		subst.addAll(concDetAssignments);
		if (concreteEventActionTable.getXiUnprime() != null)
			subst.add(concreteEventActionTable.getXiUnprime());
		
		for (int i=0; i<nondetWitnesses.size(); i++) {
			if (freeIdents.contains(nondetLabels.get(i))) {
				Predicate hypPred = nondetPredicates.get(i);
				hypPred = hypPred.applyAssignments(subst);
				hyp.add(makePredicate(
								hypPred,
								nondetWitnesses.get(i).getSource()));
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
