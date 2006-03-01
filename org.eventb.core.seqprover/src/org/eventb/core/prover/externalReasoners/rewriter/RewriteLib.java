package org.eventb.core.prover.externalReasoners.rewriter;



import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import static org.eventb.core.prover.Lib.*;

public class RewriteLib {
	
	public static Predicate removeNeg(ITypeEnvironment te,Predicate p){
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
	
	public static Predicate trivial(ITypeEnvironment te,Predicate p){
		if ((isNeg(p)) && (isNeg(negPred(p)))) 
			return  negPred(negPred(p));
		
		if (isEq(p) && eqLeft(p).equals(eqRight(p)))
			return True;
		
		if (isNotEq(p) && notEqLeft(p).equals(notEqRight(p)))
			return False;
		
		if (isInclusion(p) && isEmptySet(getSet(p)))
			return False;
		
		return null;
	}

}
