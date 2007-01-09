/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCWitness;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IStateRepository;
import org.eventb.core.pog.util.POGHint;
import org.eventb.core.pog.util.POGIntervalSelectionHint;
import org.eventb.core.pog.util.POGPredicate;
import org.eventb.core.pog.util.POGSource;
import org.eventb.core.pog.util.POGTraceablePredicate;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventActionModule extends MachineEventRefinementModule {

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.state.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		ISCAction[] actions = concreteEventActionTable.getActions();
		
		if (actions.length == 0)
			return;
		
		IPOFile target = repository.getTarget();
		
		POGHint[] hints = hints(
				new POGIntervalSelectionHint(
						eventHypothesisManager.getRootHypothesis(), 
						eventHypothesisManager.getFullHypothesis()));
		
		Assignment[] assignments = concreteEventActionTable.getAssignments();
		
		for (int k=0; k<actions.length; k++) {
			ISCAction action = actions[k];
			Assignment assignment = assignments[k];
			
			POGSource[] sources = sources(new POGSource(IPOSource.DEFAULT_ROLE, action));
			
			if (abstractEventActionTable.getIndexOfCorrespondingAbstract(k) == -1) {
				
				Predicate baPredicate = assignment.getBAPredicate(factory);
				List<POGPredicate> hyp = makeGuardAndActionHypothesis(baPredicate);
				
				Predicate wdPredicate = assignment.getWDPredicate(factory);
				createProofObligation(target, hyp,
						wdPredicate, action, sources, hints, 
						"WD", "Well-definedness of action", monitor);
				
				Predicate fisPredicate = assignment.getFISPredicate(factory);
				createProofObligation(target, hyp,
						fisPredicate, action, sources, hints, 
						"FIS", "Feasibility of action", monitor);
				
			}
		}
	}

	private List<POGPredicate> makeGuardAndActionHypothesis(Predicate baPredicate) {
		
		List<ISCAction> actions = abstractEventActionTable.getNondetActions();
		
		ArrayList<POGPredicate> hyp = new ArrayList<POGPredicate>(
				witnessTable.getNondetWitnesses().size() +
				actions.size());
		
		if (eventVariableWitnessPredicatesUnprimed(hyp)) {
			List<Predicate> predicates = abstractEventActionTable.getNondetPredicates();
			List<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
			substitution.addAll(witnessTable.getEventDetAssignments());
			substitution.addAll(witnessTable.getMachinePrimedDetAssignments());
//			if (witnessTable.getPrimeSubstitution() != null)
//				substitution.add(witnessTable.getPrimeSubstitution());
			
			for (int i=0; i<actions.size(); i++) {
				Predicate predicate = predicates.get(i);
				predicate = predicate.applyAssignments(substitution, factory);
				hyp.add(new POGPredicate(predicate, actions.get(i)));
			}
			
			return hyp;
		} else
			return emptyPredicates;
	}

	private boolean eventVariableWitnessPredicatesUnprimed(List<POGPredicate> hyp) {
		
		List<FreeIdentifier> witnessIdents = witnessTable.getVariables();
		List<Predicate> witnessPreds = witnessTable.getPredicates();
		ISCWitness[] witnesses = witnessTable.getWitnesses();
		
		for (int i=0; i<witnessIdents.size(); i++) {
			Predicate predicate = witnessPreds.get(i);
			if ( ! witnessIdents.get(i).isPrimed()) {
				FreeIdentifier[] freeIdents = predicate.getFreeIdentifiers();
				for (FreeIdentifier freeIdent : freeIdents) {
					if (freeIdent.isPrimed())
						return false;
				}
			}
			if ( !witnessTable.isDeterministic(i))
				hyp.add(new POGPredicate(predicate, witnesses[i]));
		}
		return true;
	}

	private void createProofObligation(
			IPOFile target, 
			List<POGPredicate> hyp,
			Predicate predicate, 
			ISCAction action, 
			POGSource[] sources, 
			POGHint[] hints, 
			String suffix,
			String desc,
			IProgressMonitor monitor) throws RodinDBException {
		String sequentName = concreteEventLabel + "/" + action.getLabel() + "/" + suffix;
		if (!goalIsTrivial(predicate)) {
			createPO(
					target, 
					sequentName, 
					desc, 
					fullHypothesis, 
					hyp, 
					new POGTraceablePredicate(predicate, action), 
					sources, 
					hints, 
					monitor);
		} else {
			if (DEBUG_TRIVIAL)
				debugTraceTrivial(sequentName);
		}
	}
	
}
