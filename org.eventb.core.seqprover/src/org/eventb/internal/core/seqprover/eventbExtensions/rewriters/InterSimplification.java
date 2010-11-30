package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.Expression;
import org.eventb.core.seqprover.eventbExtensions.DLib;

public class InterSimplification extends ExpressionSimplification {

	public InterSimplification(DLib lib) {
		super(lib);
	}

	@Override
	protected boolean eliminateDuplicate() {
		return true;
	}

	@Override
	protected boolean isNeutral(Expression formula) {
		return formula.isATypeExpression();

	}

	@Override
	protected boolean isDeterminant(Expression formula) {
		return formula.getTag() == Expression.EMPTYSET;
	}

	@Override
	protected Expression getNeutral(Expression formula) {
		return formula.getType().getBaseType()
				.toExpression(dLib.getFormulaFactory());
	}

}
