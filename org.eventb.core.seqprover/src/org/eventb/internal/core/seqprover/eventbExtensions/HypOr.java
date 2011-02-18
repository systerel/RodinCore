package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public class HypOr extends EmptyInputReasoner implements IReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".hypOr";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule("HYP_OR")
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {

		Predicate goal = seq.goal();
		if (!Lib.isDisj(goal)) {
			return ProverFactory.reasonerFailure(this, input,
					"Goal is not a disjunctive predicate");
		}
		
		AssociativePredicate aPred = (AssociativePredicate) goal;
		Predicate[] children = aPred.getChildren();
		for (Predicate child : children) {
			if (seq.containsHypothesis(child)) {
				return ProverFactory.makeProofRule(
						this,input,
						goal,
						child,
						"∨ goal in hyp",
						new IAntecedent[0]);
			}
		}
		
		return ProverFactory.reasonerFailure(this, input,
				"Hypotheses contain no disjunct in goal");
	}


}
