/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.eventbExtensions.DLib.mDLib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInputReasoner;

public class He extends SinglePredInputReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".he";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule("EQL_RL")
	public IReasonerOutput apply(IProverSequent seq,
			IReasonerInput reasonerInput, IProofMonitor pm) {

		SinglePredInput input = (SinglePredInput) reasonerInput;

		Predicate eqHyp = input.getPredicate();

		if (!seq.containsHypothesis(eqHyp))
			return ProverFactory.reasonerFailure(this, input,
					"Nonexistent hypothesis:" + eqHyp);
		if (!Lib.isEq(eqHyp))
			return ProverFactory.reasonerFailure(this, input,
					"Hypothesis is not an equality:" + eqHyp);

		Expression from;
		Expression to;

		from = Lib.eqRight(eqHyp);
		to = Lib.eqLeft(eqHyp);

		// This code is common to the reasoners Eq and He
		
		List<IHypAction> rewrites = new ArrayList<IHypAction>();
		Set<Predicate> toDeselect = new LinkedHashSet<Predicate>();
		// Set<Predicate> toSelect = new LinkedHashSet<Predicate>();
		// toDeselect.add(eqHyp);
		
		final DLib lib = mDLib(seq.getFormulaFactory());
		for (Predicate shyp : seq.selectedHypIterable()){
			if (!shyp.equals(eqHyp)) {
				Predicate rewritten = (lib.rewrite(shyp,from,to));
				// if (rewritten != shyp)
				if (! seq.containsHypothesis(rewritten))
				{
					rewrites.add(ProverFactory.makeForwardInfHypAction(
							Collections.singleton(shyp),
							Collections.singleton(rewritten)));
					toDeselect.add(shyp);
					// toSelect.add(rewritten);
				}
			}
		}

		Predicate rewrittenGoal = lib.rewrite(seq.goal(),from,to);
		
		if (rewrittenGoal == seq.goal() && rewrites.isEmpty())
			return ProverFactory.reasonerFailure(this,input,
					"Nothing to do");

		Predicate goalDep = seq.goal();
		Predicate newGoal = rewrittenGoal;
		
		// remove goal dependency if goal is not rewritten
		if (rewrittenGoal == seq.goal()){
			goalDep = null;
			newGoal = null;
		}
		rewrites.add(ProverFactory.makeDeselectHypAction(toDeselect));
		// rewrites.add(ProverFactory.makeSelectHypAction(toSelect));
		IAntecedent[] anticidents = new IAntecedent[1];
		anticidents[0] = ProverFactory.makeAntecedent(
				newGoal,null,null,
				rewrites);
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
		this,input,
		goalDep,
		Collections.singleton(eqHyp),
		null,
		"he ("+eqHyp+")",
		anticidents);

		return reasonerOutput;
		
//		
//		List<IHypAction> rewrites = new ArrayList<IHypAction>();
//		Set<Predicate> toDeselect = new HashSet<Predicate>();
//		toDeselect.add(eqHyp);
//
//		for (Predicate shyp : seq.selectedHypIterable()) {
//			if (!shyp.equals(eqHyp)) {
//				Predicate rewritten = (Lib.rewrite(shyp, from, to));
//				if (rewritten != shyp) {
//					rewrites.add(ProverFactory.makeForwardInfHypAction(
//							Collections.singleton(shyp), Collections
//									.singleton(rewritten)));
//					toDeselect.add(shyp);
//				}
//			}
//		}
//
//		Predicate rewrittenGoal = Lib.rewrite(seq.goal(), from, to);
//
//		if (rewrittenGoal == seq.goal() && rewrites.isEmpty())
//			return ProverFactory.reasonerFailure(this, input,
//					"No rewriting in selected hypotheses and goal");
//
//		Predicate goalDep = seq.goal();
//		Predicate newGoal = rewrittenGoal;
//
//		// remove goal dependency if goal is not rewritten
//		if (rewrittenGoal == seq.goal()) {
//			goalDep = null;
//			newGoal = null;
//		}
//		rewrites.add(ProverFactory.makeDeselectHypAction(toDeselect));
//		IAntecedent[] anticidents = new IAntecedent[1];
//		anticidents[0] = ProverFactory.makeAntecedent(newGoal, null, null,
//				rewrites);
//
//		IProofRule reasonerOutput = ProverFactory.makeProofRule(this, input,
//				goalDep, Collections.singleton(eqHyp), null, "he (" + eqHyp
//						+ ")", anticidents);
//
//		return reasonerOutput;
	}

}
