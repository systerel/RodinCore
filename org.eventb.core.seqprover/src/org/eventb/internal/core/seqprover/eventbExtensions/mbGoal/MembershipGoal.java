/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.mbGoal;

import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.seqprover.ProverLib.PM;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.reasonerInputs.HypothesesReasoner;

/**
 * Discharge a sequent such as :
 * 
 * <pre>
 * <code>H, x∈A, A⊆B ... C⊂D ⊢ x∈D</code> iff A⊆B⊂ ... ⊆ ... ⊂ ... ⊆C⊂D
 * </pre>
 * 
 * @author Emmanuel Billaud
 */
public class MembershipGoal extends HypothesesReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".mbGoal";

	// TODO Trace extractor functioning and research progress
	public static boolean DEBUG = false;


	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		final IProofMonitor myPM = (pm == null) ? PM: pm;
		final Predicate goal = seq.goal();
		final FormulaFactory ff = seq.getFormulaFactory();
		if (goal.getTag() != IN) {
			return ProverFactory.reasonerFailure(this, input,
					"Goal must be a membership.");
		}
		if (!(input instanceof HypothesesReasoner.Input)) {
			return ProverFactory.reasonerFailure(this, input,
					"The input must be a HypothesesReasoner Input.");
		}
		final Set<Predicate> neededHyps = verifyInput(
				(HypothesesReasoner.Input) input, seq);
		if (neededHyps == null) {
			return ProverFactory.reasonerFailure(this, input,
					"Given predicate is not a hypothesis of the sequent.");
		}
		final MembershipGoalImpl mbGoalImpl = new MembershipGoalImpl(goal,
				neededHyps, ff, myPM);
		final Rationale search = mbGoalImpl.search();
		if (search == null) {
			return ProverFactory.reasonerFailure(this, input,
					"Cannot discharge the goal.");
		}
		final Rule<?> rule = search.makeRule();
		assert rule.getConsequent().equals(goal);
		return ProverFactory.makeProofRule(this, input, goal, neededHyps,
				"Membership in goal", new IAntecedent[0]);
	}

	private Set<Predicate> verifyInput(HypothesesReasoner.Input input,
			IProverSequent seq) {
		final Predicate[] hyps = input.getPred();
		final List<Predicate> listHyps = Arrays.asList(hyps);
		if (!seq.containsHypotheses(listHyps)) {
			return null;
		}
		return new HashSet<Predicate>(listHyps);
	}

}