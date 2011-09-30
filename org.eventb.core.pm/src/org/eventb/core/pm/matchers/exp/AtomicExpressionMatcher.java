package org.eventb.core.pm.matchers.exp;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.pm.ExpressionMatcher;
import org.eventb.core.pm.IBinding;

/**
 * @since 1.0
 * @author maamria
 *
 */
public class AtomicExpressionMatcher extends ExpressionMatcher<AtomicExpression> {

	public AtomicExpressionMatcher(){
		super(AtomicExpression.class);
	}
	
	@Override
	protected boolean gatherBindings(AtomicExpression form,
			AtomicExpression pattern, IBinding existingBinding){
		// no further action required
		return true;
	}
	
	@Override
	protected AtomicExpression getExpression(Expression e) {
		return (AtomicExpression) e;
	}
}
