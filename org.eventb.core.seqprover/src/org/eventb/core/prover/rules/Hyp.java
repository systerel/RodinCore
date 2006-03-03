package org.eventb.core.prover.rules;

import org.eventb.core.prover.Lib;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

public final class Hyp extends ProofRule {

	public Hyp() {
		super("assm");
	}
	
	public boolean isApplicable(IProverSequent S) {
		if (S.goal().equals(Lib.True)) return true;
		// TODO merge these two tests efficiently.
		if (Hypothesis.containsPredicate(S.hypotheses(),S.goal())) return true;
		if (Hypothesis.containsPredicate(S.hypotheses(),Lib.False)) return true;
//		if (Lib.isNotEq(S.goal())) {
//			if (Lib.isEmptySet(Lib.eqRight(S.goal())) &&
//					Lib.eqLeft(S.goal()).isATypeExpression())
//				return true;
//			if (Lib.isEmptySet(Lib.eqLeft(S.goal())) &&
//					Lib.eqRight(S.goal()).isATypeExpression())
//				return true;
//		}
//		if (Lib.isInclusion(S.goal())){
//			
//		}
		return false;
	}

	public IProverSequent[] apply(IProverSequent S) {
		if (isApplicable(S)) return new IProverSequent[0];
		return null;
	}

}
