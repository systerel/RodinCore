package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import org.eventb.core.ast.Expression;
import org.eventb.core.seqprover.eventbExtensions.DLib;

public class MultiplicationSimplification extends ExpressionSimplification {

	public MultiplicationSimplification(DLib lib) {
		super(lib);
	}

	@Override
	protected boolean eliminateDuplicate() {
		return false;
	}

	@Override
	protected boolean isNeutral(Expression formula) {
		return isIntegerValue(formula, ONE);
	}

	@Override
	protected boolean isDeterminant(Expression formula) {
		return isIntegerValue(formula, ZERO);
	}

	@Override
	protected Expression getNeutral(Expression formula) {
		return dLib.getFormulaFactory().makeIntegerLiteral(ONE, null);
	}

	/**
	 * TODO should implement additional processing as in
	 * {@link MultiplicationSimplifier}
	 */

}
