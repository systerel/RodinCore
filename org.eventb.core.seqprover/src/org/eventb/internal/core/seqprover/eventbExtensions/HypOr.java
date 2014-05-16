/*******************************************************************************
 * Copyright (c) 2007, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - add variations
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;
import static org.eventb.core.seqprover.reasoners.Hyp.getStrongerHypothesis;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public class HypOr extends EmptyInputReasoner implements IReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".hypOr";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	@ProverRule("HYP_OR")
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {

		final Predicate goal = seq.goal();
		if (!Lib.isDisj(goal)) {
			return reasonerFailure(this, input,
					"Goal is not a disjunctive predicate");
		}

		final AssociativePredicate aPred = (AssociativePredicate) goal;
		final Predicate[] children = aPred.getChildren();
		for (Predicate child : children) {
			final Predicate hyp = getStrongerHypothesis(seq, child);
			if (hyp == null) {
				continue;
			}
			return makeProofRule(this, input, goal, hyp, "∨ goal in hyp",
					new IAntecedent[0]);
		}

		return reasonerFailure(this, input,
				"Hypotheses contain no disjunct of goal");
	}

}
