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
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCWitness;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IIdentifierTable;
import org.eventb.core.pog.POGPredicate;
import org.eventb.core.pog.POGSource;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventPreserveInvariantModule extends MachineEventInvariantModule {

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.pog.modules.MachineEventInvariantModule#isApplicable()
	 */
	@Override
	protected boolean isApplicable() {
//		 this POG module applies to refined events
		return abstractEvent != null;
	}
	
	@Override
	protected void createInvariantProofObligation(
			IPOFile target, 
			ISCInvariant invariant, 
			String invariantLabel, 
			Predicate invPredicate, 
			IIdentifierTable identifierTable,
			ArrayList<POGPredicate> hyp, 
			Set<FreeIdentifier> freeIdents,
			IProgressMonitor monitor) throws RodinDBException {
		
		LinkedList<POGPredicate> bighyp = new LinkedList<POGPredicate>();
		bighyp.addAll(hyp);
		
		ArrayList<Assignment> nondet = concreteEventActionTable.getNondetAssignments();
		ArrayList<ISCAction> nondetActions = concreteEventActionTable.getNondetActions();
		for (int k=0; k<nondet.size(); k++) {
			bighyp.add(
					new POGPredicate(
							nondetActions.get(k), 
							nondet.get(k).getBAPredicate(factory)));
		}
		ArrayList<Predicate> nondetWitPrds = witnessTable.getNondetPredicates();
		ArrayList<FreeIdentifier> witnessedVars = witnessTable.getNondetAssignedVariables();
		ArrayList<ISCWitness> nondetWits = witnessTable.getNondetWitnesses();
		for (int i=0; i<nondetWits.size(); i++) {
			FreeIdentifier ident = witnessedVars.get(i).isPrimed() ?
					witnessedVars.get(i).withoutPrime(factory) :
					witnessedVars.get(i);
			if (freeIdents.contains(ident))
				bighyp.add(
						new POGPredicate(
								nondetWits.get(i),
								nondetWitPrds.get(i)));
		}
		
		LinkedList<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
		if (concreteEventActionTable.getDeltaPrime() != null)
			substitution.add(concreteEventActionTable.getDeltaPrime());
		substitution.addAll(abstractEventActionTable.getDisappearingWitnesses());
		Predicate predicate = invPredicate.applyAssignments(substitution, factory);
		substitution.clear();		
		substitution.addAll(witnessTable.getEventDetAssignments());
		substitution.addAll(witnessTable.getMachineDetAssignments());
		if (witnessTable.getPrimeSubstitution() != null)
			substitution.add(witnessTable.getPrimeSubstitution());
		predicate = predicate.applyAssignments(substitution, factory);
		substitution.clear();
		if (concreteEventActionTable.getXiUnprime() != null)
			substitution.add(concreteEventActionTable.getXiUnprime());
		substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());
		predicate = predicate.applyAssignments(substitution, factory);
		
		createPO(
				target, 
				concreteEventLabel + "/" + invariantLabel + "/INV", 
				"Invariant " + (isInitialisation ? " establishment" : " preservation"),
				identifierTable,
				fullHypothesisName,
				bighyp,
				new POGPredicate(invariant, predicate),
				sources(
						new POGSource("abstract event", abstractEvent),
						new POGSource("concrete event", concreteEvent), 
						new POGSource("invariant", invariant)),
				emptyHints,
				monitor);
	}

}
