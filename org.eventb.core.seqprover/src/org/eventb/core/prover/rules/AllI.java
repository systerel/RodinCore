package org.eventb.core.prover.rules;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.sequent.IProverSequent;

public final class AllI implements Rule {

	public String name(){
		return "allI";
	}
	
	public boolean isApplicable(IProverSequent S) {
		return Lib.isUnivQuant(S.goal());
	}

	public IProverSequent[] apply(IProverSequent S) {
		if (! isApplicable(S)) return null;
		assert (S.goal() instanceof QuantifiedPredicate & S.goal().getTag() == Formula.FORALL);
		FormulaFactory ff = Lib.ff;
		QuantifiedPredicate UnivQ = (QuantifiedPredicate)S.goal();
		BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(UnivQ);
		ITypeEnvironment newITypeEnvironment = S.typeEnvironment().clone();
		FreeIdentifier[] freeIdents = ff.makeFreshIdentifiers(boundIdentDecls,newITypeEnvironment);
		assert boundIdentDecls.length == freeIdents.length;
		Predicate newGoal = UnivQ.instantiate(freeIdents,Lib.ff);	
		IProverSequent anticident = S;
		anticident = anticident.replaceGoal(newGoal,newITypeEnvironment);
		return new IProverSequent[] {anticident};
	}

}
