package org.eventb.core.prover.rules;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

public class ImpI implements Rule {

	public String name(){
		return "ded";
	}
	
	public boolean isApplicable(IProverSequent S) {
		return Lib.isImp(S.goal());
	}

	private IProverSequent[] applySingle(IProverSequent S) {
		if (! isApplicable(S)) return null; 
		Predicate left = ((BinaryPredicate)S.goal()).getLeft();
		Predicate right = ((BinaryPredicate)S.goal()).getRight();
		IProverSequent anticident = S;
		anticident = anticident.addHyp(new Hypothesis(left),null).replaceGoal(right,null);
		anticident = anticident.selectHypotheses(Hypothesis.Hypotheses(left));
		return new IProverSequent[] {anticident};
	}
	
	private IProverSequent[] applyMultiple(IProverSequent S) {
		if (! isApplicable(S)) return null; 
		IProverSequent[] result = applySingle(S);
		assert result.length == 1;
		while (this.isApplicable(result[0])){
			result = applySingle(result[0]);
			assert result.length == 1;
		}
		return result;
	}
	
	// It may seem strange using multiple applications, but 
	// it makes proofs a lot more managable
	// conjI and allI work the same way
	public IProverSequent[] apply(IProverSequent S) {
		return this.applyMultiple(S);
	}
	
}
