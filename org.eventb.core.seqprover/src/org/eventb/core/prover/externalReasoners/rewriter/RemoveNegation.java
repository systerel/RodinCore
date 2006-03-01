package org.eventb.core.prover.externalReasoners.rewriter;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import static org.eventb.core.prover.Lib.*;

public class RemoveNegation implements Rewriter{

	public boolean isApplicable(Predicate p) {
		if (! (isNeg(p))) return false;
		Predicate negP = negPred(p);
		
		if (isTrue(negP))
			return true;
		if (isFalse(negP))
			return true;
		if (isNeg(negP))
			return true;
		if (isConj(negP))
			return true;
		if (isDisj(negP))
			return true;
		if (isImp(negP))
			return true;
		if (isExQuant(negP))
			return true;
		if (isUnivQuant(negP))
			return true;
		
		if (isEq(negP))
			return true;
		if (isNotEq(negP))
			return true;
		
		if (isInclusion(negP))
			return true;
		if (isNotInclusion(negP))
			return true;
		
		return false;
	}

	public Predicate apply(ITypeEnvironment te, Predicate p) {
		if (! (isNeg(p))) return null;
		Predicate negP = negPred(p);
		
		if (isTrue(negP))
			return False;
		if (isFalse(negP))
			return True;
		if (isNeg(negP))
			return negP;
		if (isConj(negP))
			return makeDisj(te,makeNeg(te,conjuncts(negP)));
		if (isDisj(negP))
			return makeConj(te,makeNeg(te,disjuncts(negP)));
		if (isImp(negP))
			return makeConj(te,impLeft(negP),makeNeg(te,impRight(negP)));
		if (isExQuant(negP))
			return makeUnivQuant(te,getBoundIdents(negP),
					makeUncheckedNeg(getBoundPredicate(negP)));
		if (isUnivQuant(negP))
			return makeExQuant(te,getBoundIdents(negP),
					makeUncheckedNeg(getBoundPredicate(negP)));
		
		if (isEq(negP))
			return makeNotEq(te,eqLeft(negP),eqRight(negP));
		if (isNotEq(negP))
			return makeEq(te,notEqLeft(negP),notEqRight(negP));
		
		if (isInclusion(negP))
			return makeNotInclusion(te,getElement(negP),getSet(negP));
		if (isNotInclusion(negP))
			return makeInclusion(te,getElement(negP),getSet(negP));
		
		return null;
	}

}
