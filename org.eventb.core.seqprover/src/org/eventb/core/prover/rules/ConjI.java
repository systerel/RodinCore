package org.eventb.core.prover.rules;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.sequent.IProverSequent;

public final class ConjI implements Rule {

	public String name(){
		return "conjI";
	}
	
	public boolean isApplicable(IProverSequent S) {
		return Lib.isConj(S.goal());
	}

	public IProverSequent[] apply(IProverSequent S) {
		if (! isApplicable(S)) return null; 
		Predicate[] conjuncts = ((AssociativePredicate)S.goal()).getChildren();
		IProverSequent[] anticidents = new IProverSequent[conjuncts.length];
		for (int i = 0;i < conjuncts.length; i++){
			anticidents[i] = S.replaceGoal(conjuncts[i],null);
		}
		return anticidents;
	}

}
