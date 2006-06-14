package org.eventb.core.prover.reasoners.rewriter;

import org.eventb.core.ast.Predicate;
import static org.eventb.core.prover.Lib.*;

public class RemoveNegation implements Rewriter{

	public String getRewriterID() {
		return "removeNegation";
	}
	
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

	public Predicate apply(Predicate p) {
		if (! (isNeg(p))) return null;
		Predicate negP = negPred(p);
		
		if (isTrue(negP))
			return False;
		if (isFalse(negP))
			return True;
		if (isNeg(negP))
			return negPred(negP);
		if (isConj(negP))
			return makeDisj(makeNeg(conjuncts(negP)));
		if (isDisj(negP))
			return makeConj(makeNeg(disjuncts(negP)));
		if (isImp(negP))
			return makeConj(impLeft(negP),makeNeg(impRight(negP)));
		if (isExQuant(negP))
			return makeUnivQuant(getBoundIdents(negP),
					makeNeg(getBoundPredicate(negP)));
		if (isUnivQuant(negP))
			return makeExQuant(getBoundIdents(negP),
					makeNeg(getBoundPredicate(negP)));
		
		if (isEq(negP))
			return makeNotEq(eqLeft(negP),eqRight(negP));
		if (isNotEq(negP))
			return makeEq(notEqLeft(negP),notEqRight(negP));
		
		if (isInclusion(negP))
			return makeNotInclusion(getElement(negP),getSet(negP));
		if (isNotInclusion(negP))
			return makeInclusion(getElement(negP),getSet(negP));
		
		return null;
	}

}
