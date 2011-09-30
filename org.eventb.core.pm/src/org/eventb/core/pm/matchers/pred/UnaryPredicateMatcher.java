package org.eventb.core.pm.matchers.pred;

import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.PredicateMatcher;

/**
 * @since 1.0
 * @author maamria
 *
 */
public class UnaryPredicateMatcher extends PredicateMatcher<UnaryPredicate> {

	public UnaryPredicateMatcher() {
		super(UnaryPredicate.class);
	}

	@Override
	protected boolean gatherBindings(UnaryPredicate upForm, UnaryPredicate upPattern,
			IBinding existingBinding){
		Predicate fChild = upForm.getChild();
		Predicate pChild = upPattern.getChild();
		if(pChild instanceof PredicateVariable){
			return existingBinding.putPredicateMapping((PredicateVariable) pChild, fChild);
		}
		return matchingFactory.match(fChild, pChild, existingBinding);
	}

	@Override
	protected UnaryPredicate getPredicate(Predicate p) {
		return (UnaryPredicate) p;
	}

}
