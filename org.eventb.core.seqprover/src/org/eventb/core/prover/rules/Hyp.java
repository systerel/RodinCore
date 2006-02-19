package org.eventb.core.prover.rules;

import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

public final class Hyp extends ProofRule {

	public Hyp() {
		super("hyp");
	}
	
	public boolean isApplicable(IProverSequent S) {
		if (Hypothesis.containsPredicate(S.hypotheses(),S.goal())) return true;
		return false;
	}

	public IProverSequent[] apply(IProverSequent S) {
		if (isApplicable(S)) return new IProverSequent[0];
		return null;
	}

}
