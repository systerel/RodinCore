/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - add variations
 *******************************************************************************/
package org.eventb.core.seqprover.reasoners;

import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;
import static org.eventb.internal.core.seqprover.eventbExtensions.utils.Variations.getStrongerPositive;

import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public class Hyp extends EmptyInputReasoner {

	private static final IAntecedent[] NO_ANTECEDENTS = new IAntecedent[0];

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".hyp";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	@ProverRule("HYP")
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		final Predicate goal = seq.goal();
		final Predicate hypothesis = getStrongerHypothesis(seq, goal);
		if (hypothesis == null) {
			return reasonerFailure(this, input, "Goal not in hypothesis");
		}
		return makeProofRule(this, input, goal, hypothesis, "hyp",
				NO_ANTECEDENTS);
	}

	/**
	 * Returns a hypothesis which implies the goal.
	 * 
	 * @param seq
	 *            the sequent to which this reasoner is applied
	 * @param pred
	 *            the goal
	 * @return a hypothesis or <code>null</code> if not found
	 */
	public static Predicate getStrongerHypothesis(IProverSequent seq,
			Predicate pred) {
		final List<Predicate> simPreds = getStrongerPositive(pred);
		for (final Predicate p : simPreds) {
			if (seq.containsHypothesis(p)) {
				return p;
			}
		}
		return null;
	}
}
