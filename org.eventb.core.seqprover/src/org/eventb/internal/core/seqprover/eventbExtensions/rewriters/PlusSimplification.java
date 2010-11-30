package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static java.math.BigInteger.ZERO;

import org.eventb.core.ast.Expression;
import org.eventb.core.seqprover.eventbExtensions.DLib;

public class PlusSimplification extends ExpressionSimplification {

	public PlusSimplification(DLib lib) {
		super(lib);
	}

	@Override
	protected boolean eliminateDuplicate() {
		return false;
	}

	@Override
	protected boolean isNeutral(Expression formula) {
		return isIntegerValue(formula, ZERO);
	}

	@Override
	protected boolean isDeterminant(Expression formula) {
		return false;
	}

	@Override
	protected Expression getNeutral(Expression formula) {
		return dLib.getFormulaFactory().makeIntegerLiteral(ZERO, null);
	}

}
