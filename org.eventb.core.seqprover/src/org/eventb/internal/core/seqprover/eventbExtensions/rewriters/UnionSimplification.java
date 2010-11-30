package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.Expression;
import org.eventb.core.seqprover.eventbExtensions.DLib;

public class UnionSimplification extends ExpressionSimplification {

	public UnionSimplification(DLib lib) {
		super(lib);
	}

	@Override
	protected boolean eliminateDuplicate() {
		return true;
	}

	@Override
	protected boolean isNeutral(Expression formula) {
		return formula.getTag() == Expression.EMPTYSET;
	}

	@Override
	protected boolean isDeterminant(Expression formula) {
		return formula.isATypeExpression();
	}

	@Override
	protected Expression getNeutral(Expression formula) {
		return dLib.getFormulaFactory().makeEmptySet(formula.getType(), null);
	}

}
