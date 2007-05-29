package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInputReasoner;

public class HypOr extends SinglePredInputReasoner implements IReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".hypOr";

	public String getReasonerID() {
		return REASONER_ID;
	}

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		if (!(input instanceof SinglePredInput)) {
			return ProverFactory.reasonerFailure(this, input,
					"Input is not a single predicate input");
		}
		SinglePredInput mInput = (SinglePredInput) input;
		Predicate predicate = mInput.getPredicate();
		
		if (!seq.containsHypothesis(predicate)) {
			return ProverFactory.reasonerFailure(this, input,
					"Sequent does not contain predicate " + predicate);
		}
		
		Predicate goal = seq.goal();
		if (!Lib.isDisj(goal)) {
			return ProverFactory.reasonerFailure(this, input,
					"Goal is not a disjunctive predicate");
		}
		
		AssociativePredicate aPred = (AssociativePredicate) goal;
		Predicate[] children = aPred.getChildren();
		for (Predicate child : children) {
			if (child.equals(predicate)) {
				return ProverFactory.makeProofRule(
						this,input,
						null,
						predicate,
						"hyp with ∨ goal",
						new IAntecedent[0]);
			}
		}
		
		return ProverFactory.reasonerFailure(this, input,
				"Hypothesis with ∨ goal is not applicable for hypothesis "
						+ predicate);
	}


}
