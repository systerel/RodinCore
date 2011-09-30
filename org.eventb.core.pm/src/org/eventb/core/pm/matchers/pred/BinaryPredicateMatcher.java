package org.eventb.core.pm.matchers.pred;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.PredicateMatcher;

/**
 * @since 1.0
 * @author maamria
 *
 */
public class BinaryPredicateMatcher extends PredicateMatcher<BinaryPredicate> {

	public BinaryPredicateMatcher() {
		super(BinaryPredicate.class);
	}

	@Override
	protected boolean gatherBindings(BinaryPredicate bpForm,
			BinaryPredicate bpPattern, IBinding existingBinding){
		Predicate fLeft = bpForm.getLeft();
		Predicate pLeft = bpPattern.getLeft();
		if(pLeft instanceof PredicateVariable){
			return existingBinding.putPredicateMapping((PredicateVariable)pLeft, fLeft);
		}
		else if(!matchingFactory.match(fLeft, pLeft, existingBinding)){
			return false;
		}
		
		Predicate fRight = bpForm.getRight();
		Predicate pRight = bpPattern.getRight();
		if(pRight instanceof PredicateVariable){
			return existingBinding.putPredicateMapping((PredicateVariable)pRight, fRight);
		}
		return matchingFactory.match(fRight, pRight, existingBinding);
	}

	@Override
	protected BinaryPredicate getPredicate(Predicate p) {
		return (BinaryPredicate) p;
	}

}
