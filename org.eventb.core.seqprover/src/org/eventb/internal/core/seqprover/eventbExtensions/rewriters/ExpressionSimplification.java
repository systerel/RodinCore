package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.math.BigInteger;
import java.util.Collection;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.seqprover.eventbExtensions.DLib;

public abstract class ExpressionSimplification extends
		FormulaSimplification<Expression> {

	public ExpressionSimplification(DLib lib) {
		super(lib);
	}

	@Override
	protected boolean isContradicting(Expression formula,
			Collection<Expression> formulas) {
		return false;
	}

	@Override
	protected Expression getContradictionResult() {
		assert false;
		return null;
	}

	@Override
	protected Expression makeAssociativeFormula(int tag,
			Collection<Expression> formulas) {
		return dLib.getFormulaFactory().makeAssociativeExpression(tag,
				formulas, null);
	}

	protected boolean isIntegerValue(Expression formula, BigInteger integer) {
		return formula.getTag() == Expression.INTLIT
				&& ((IntegerLiteral) formula).getValue().equals(integer);
	}

}
