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

import org.eventb.core.ISCAction;
import org.eventb.core.ISCWitness;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.util.POGPredicate;

/**
 * @author Stefan Hallerstede
 *
 */
public class FwdMachineEventActionModule extends MachineEventActionModule {
	
	@Override
	protected boolean abstractHasNotSameAction(int k) {
		return abstractEventActionTable.getIndexOfCorrespondingAbstract(k) == -1;
	}

	@Override
	protected List<POGPredicate> makeAbstractActionHypothesis(Predicate baPredicate) {
		
		List<ISCAction> actions = abstractEventActionTable.getNondetActions();
		
		ArrayList<POGPredicate> hyp = new ArrayList<POGPredicate>(
				witnessTable.getNondetWitnesses().size() +
				actions.size());
		
		if (eventVariableWitnessPredicatesUnprimed(hyp)) {
			List<Predicate> predicates = abstractEventActionTable.getNondetPredicates();
			List<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
			substitution.addAll(witnessTable.getEventDetAssignments());
			substitution.addAll(witnessTable.getMachinePrimedDetAssignments());
			
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
		List<ISCWitness> witnesses = witnessTable.getWitnesses();
		
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
				hyp.add(new POGPredicate(predicate, witnesses.get(i)));
		}
		return true;
	}

	@Override
	protected boolean isApplicable() {
		return !machineInfo.isInitialMachine();
	}


}
