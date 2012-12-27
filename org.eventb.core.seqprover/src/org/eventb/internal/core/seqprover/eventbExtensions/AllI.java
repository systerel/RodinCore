/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;
import org.eventb.internal.core.seqprover.eventbExtensions.utils.FreshInstantiaton;

/**
 * Generates the introduntion rule for universal quantification.
 * 
 * <p>
 * This reasoner frees all universally quantified variables in a goal.
 * </p>
 * 
 * @author Farhad Mehta
 * 
 */
public class AllI extends EmptyInputReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".allI";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule("ALL_R")
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {

		final Predicate goal = seq.goal();
		if (!Lib.isUnivQuant(goal)) {
			return ProverFactory.reasonerFailure(this, input,
					"Goal is not universally quantified");
		}
		final QuantifiedPredicate univQ = (QuantifiedPredicate) goal;
		final ISealedTypeEnvironment typenv = seq.typeEnvironment();
		final FreshInstantiaton inst = new FreshInstantiaton(univQ, typenv);
		final FreeIdentifier[] freshIdents = inst.getFreshIdentifiers();
		final IAntecedent[] antecedents = new IAntecedent[] {//
		makeAntecedent(inst.getResult(), null, freshIdents, null),//
		};
		final IProofRule reasonerOutput = makeProofRule(this, input, goal,
				"∀ goal (frees " + displayFreeIdents(freshIdents) + ")",
				antecedents);
		return reasonerOutput;
	}

	private String displayFreeIdents(FreeIdentifier[] freeIdents) {
		final StringBuilder sb = new StringBuilder();
		String sep = "";
		for (int i = 0; i < freeIdents.length; i++) {
			sb.append(sep);
			sep = ",";
			sb.append(freeIdents[i].toString());
		}
		return sb.toString();
	}

}
