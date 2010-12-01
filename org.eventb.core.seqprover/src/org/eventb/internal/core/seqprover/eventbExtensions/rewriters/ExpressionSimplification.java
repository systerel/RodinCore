package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static org.eventb.core.ast.Formula.EMPTYSET;
import static org.eventb.core.ast.Formula.INTLIT;
import static org.eventb.core.ast.Formula.KID_GEN;

import java.math.BigInteger;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.seqprover.eventbExtensions.DLib;

public abstract class ExpressionSimplification extends
		FormulaSimplification<Expression> {

	// TODO implement BcompSimplification

	public static class FcompSimplification extends ExpressionSimplification {

		public FcompSimplification(AssociativeExpression original, DLib dLib) {
			super(original, dLib);
		}

		@Override
		protected boolean eliminateDuplicate() {
			return false;
		}

		@Override
		protected boolean isNeutral(Expression child) {
			return child.getTag() == KID_GEN;
		}

		@Override
		protected boolean isDeterminant(Expression child) {
			// TODO for empty set
			return false;
		}

		@Override
		protected Expression getNeutral() {
			return ff.makeAtomicExpression(KID_GEN, null, original.getType());
		}

	}

	public static class InterSimplification extends ExpressionSimplification {

		public InterSimplification(AssociativeExpression original, DLib dLib) {
			super(original, dLib);
		}

		@Override
		protected boolean eliminateDuplicate() {
			return true;
		}

		@Override
		protected boolean isNeutral(Expression child) {
			return child.isATypeExpression();

		}

		@Override
		protected boolean isDeterminant(Expression child) {
			return child.getTag() == EMPTYSET;
		}

		@Override
		protected Expression getNeutral() {
			return original.getType().getBaseType().toExpression(ff);
		}

	}

	public static class MultiplicationSimplification extends
			ExpressionSimplification {

		public MultiplicationSimplification(AssociativeExpression original,
				DLib dLib) {
			super(original, dLib);
		}

		@Override
		protected boolean eliminateDuplicate() {
			return false;
		}

		@Override
		protected boolean isNeutral(Expression child) {
			return isIntegerValue(child, ONE);
		}

		@Override
		protected boolean isDeterminant(Expression child) {
			return isIntegerValue(child, ZERO);
		}

		@Override
		protected Expression getNeutral() {
			return ff.makeIntegerLiteral(ONE, null);
		}

		/**
		 * TODO should implement additional processing as in
		 * {@link MultiplicationSimplifier}
		 */

	}

	public static class OverrideSimplification extends ExpressionSimplification {

		public OverrideSimplification(AssociativeExpression original, DLib dLib) {
			super(original, dLib);
		}

		@Override
		protected boolean eliminateDuplicate() {
			return false;
		}

		@Override
		protected boolean isNeutral(Expression child) {
			return child.getTag() == EMPTYSET;
		}

		@Override
		protected boolean isDeterminant(Expression child) {
			return false;
		}

		@Override
		protected Expression getNeutral() {
			return ff.makeEmptySet(original.getType(), null);
		}

	}

	public static class PlusSimplification extends ExpressionSimplification {

		public PlusSimplification(AssociativeExpression original, DLib dLib) {
			super(original, dLib);
		}

		@Override
		protected boolean eliminateDuplicate() {
			return false;
		}

		@Override
		protected boolean isNeutral(Expression child) {
			return isIntegerValue(child, ZERO);
		}

		@Override
		protected boolean isDeterminant(Expression child) {
			return false;
		}

		@Override
		protected Expression getNeutral() {
			return ff.makeIntegerLiteral(ZERO, null);
		}

	}

	public static class UnionSimplification extends ExpressionSimplification {

		public UnionSimplification(AssociativeExpression original, DLib dLib) {
			super(original, dLib);
		}

		@Override
		protected boolean eliminateDuplicate() {
			return true;
		}

		@Override
		protected boolean isNeutral(Expression child) {
			return child.getTag() == EMPTYSET;
		}

		@Override
		protected boolean isDeterminant(Expression child) {
			return child.isATypeExpression();
		}

		@Override
		protected Expression getNeutral() {
			return ff.makeEmptySet(original.getType(), null);
		}

	}

	public ExpressionSimplification(AssociativeExpression original, DLib dLib) {
		super(original, original.getChildren(), dLib);
	}

	@Override
	protected boolean isContradicting(Expression child) {
		return false;
	}

	@Override
	protected Expression getContradictionResult() {
		assert false;
		return null;
	}

	@Override
	protected Expression makeAssociativeFormula() {
		return ff.makeAssociativeExpression(original.getTag(), newChildren,
				null);
	}

	protected boolean isIntegerValue(Expression formula, BigInteger integer) {
		return formula.getTag() == INTLIT
				&& ((IntegerLiteral) formula).getValue().equals(integer);
	}

}
