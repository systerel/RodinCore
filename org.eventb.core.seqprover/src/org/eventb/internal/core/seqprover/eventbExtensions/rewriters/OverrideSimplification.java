package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.Expression;
import org.eventb.core.seqprover.eventbExtensions.DLib;

public class OverrideSimplification extends ExpressionSimplification {

	public OverrideSimplification(DLib lib) {
		super(lib);
	}

	@Override
	protected boolean eliminateDuplicate() {
		return false;
	}

	@Override
	protected boolean isNeutral(Expression formula) {
		return formula.getTag() == Expression.EMPTYSET;
	}

	@Override
	protected boolean isDeterminant(Expression formula) {
		return false;
	}

	@Override
	protected Expression getNeutral(Expression formula) {
		return dLib.getFormulaFactory().makeEmptySet(formula.getType(), null);
	}
	
}
