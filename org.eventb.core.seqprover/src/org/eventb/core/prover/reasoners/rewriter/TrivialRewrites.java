package org.eventb.core.prover.reasoners.rewriter;

import org.eventb.core.ast.Predicate;
import static org.eventb.core.prover.Lib.*;

public class TrivialRewrites implements Rewriter{

	public String getRewriterID() {
		return "trivialRewrites";
	}
	
	public boolean isApplicable(Predicate p) {
		if ((isNeg(p)) && (isNeg(negPred(p)))) 
			return  true;
		
		if (isEq(p) && eqLeft(p).equals(eqRight(p)))
			return true;
		
		if (isNotEq(p) && notEqLeft(p).equals(notEqRight(p)))
			return true;
		
		if (isInclusion(p) && isEmptySet(getSet(p)))
			return true;
		if (isNotInclusion(p) && isEmptySet(getSet(p)))
			return true;
		
		return false;
	}

	public Predicate apply(Predicate p) {
		if ((isNeg(p)) && (isNeg(negPred(p)))) 
			return  negPred(negPred(p));
		
		if (isEq(p) && eqLeft(p).equals(eqRight(p)))
			return True;	
		if (isNotEq(p) && notEqLeft(p).equals(notEqRight(p)))
			return False;
		
		if (isInclusion(p) && isEmptySet(getSet(p)))
			return False;
		
		if (isNotInclusion(p) && isEmptySet(getSet(p)))
			return True;
		
		return null;
	}

}
