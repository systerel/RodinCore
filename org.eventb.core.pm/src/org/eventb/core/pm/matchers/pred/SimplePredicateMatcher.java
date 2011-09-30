package org.eventb.core.pm.matchers.pred;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.PredicateMatcher;

/**
 * @since 1.0
 * @author maamria
 *
 */
public class SimplePredicateMatcher extends PredicateMatcher<SimplePredicate> {

	public SimplePredicateMatcher() {
		super(SimplePredicate.class);
	}

	@Override
	protected boolean gatherBindings(SimplePredicate spForm,
			SimplePredicate spPattern, IBinding existingBinding){
		Expression fExp = spForm.getExpression();
		Expression pExp = spPattern.getExpression();
		if(pExp instanceof FreeIdentifier){
			return existingBinding.putExpressionMapping((FreeIdentifier) pExp, fExp);
		}
		return matchingFactory.match(fExp, pExp, existingBinding);
	}

	@Override
	protected SimplePredicate getPredicate(Predicate p) {
		return (SimplePredicate) p;
	}
}
