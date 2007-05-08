package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.Predicate;

import static org.eventb.core.seqprover.eventbExtensions.Lib.*;

/**
 * @author fmehta
 *	
 * @deprecated use {@link RemoveNegation} instead
 */
@Deprecated
public class RemoveNegationRewriter implements Rewriter {

	public String getRewriterID() {
		return "removeNegation";
	}
	
	public String getName() {
		return "rewrite ¬";
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
		
		// - T == F
		if (isTrue(negP))
			return False;
		// - F == T
		if (isFalse(negP))
			return True;
		// - - P == P
		if (isNeg(negP))
			return negPred(negP);
		// - (P & Q &..)  = (-P or -Q or ..) 
		if (isConj(negP))
			return makeDisj(makeNeg(conjuncts(negP)));
		// - (P or Q &..) = (-P & -Q &..)
		if (isDisj(negP))
			return makeConj(makeNeg(disjuncts(negP)));
		// - ( P => Q) = ( P & -Q)
		if (isImp(negP))
			return makeConj(impLeft(negP),makeNeg(impRight(negP)));
		// -(#x . Px) == !x. -Px
		if (isExQuant(negP))
			return makeUnivQuant(getBoundIdents(negP),
					makeNeg(getBoundPredicate(negP)));
		// -(!x. Px) == #x. - Px
		if (isUnivQuant(negP))
			return makeExQuant(getBoundIdents(negP),
					makeNeg(getBoundPredicate(negP)));
		// -(a=b) == a/=b
		if (isEq(negP))
			return makeNotEq(eqLeft(negP),eqRight(negP));
		// -(a/=b) == (a=b)
		if (isNotEq(negP))
			return makeEq(notEqLeft(negP),notEqRight(negP));
		// -(a:A) == a/:A
		if (isInclusion(negP))
			return makeNotInclusion(getElement(negP),getSet(negP));
		// -(a/:A) == a:A
		if (isNotInclusion(negP))
			return makeInclusion(getElement(negP),getSet(negP));
		
		return null;
	}

}
