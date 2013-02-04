/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added simplification for OVR, removed checkForallOnePointRune
 *     Systerel - full refactoring around a hierarchy of classes
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BINTER;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.DOMRES;
import static org.eventb.core.ast.Formula.DOMSUB;
import static org.eventb.core.ast.Formula.EMPTYSET;
import static org.eventb.core.ast.Formula.INTLIT;
import static org.eventb.core.ast.Formula.KID_GEN;
import static org.eventb.core.ast.Formula.SETMINUS;
import static org.eventb.core.ast.Formula.UNMINUS;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.eventbExtensions.DLib;

/**
 * Framework for simplifying associative formulae. This class implements several
 * simplifications:
 * <ul>
 * <li>neutral element of the operator is removed</li>
 * <li>determinant element of the operator determines the result</li>
 * <li>duplicate elements are removed (depending on the operator)</li>
 * <li>contradiction determines the result (depending on the operator)</li>
 * <li>right determinant wipes out previous children (for overriding)</li>
 * <li>specific simplifications for multiplication</li>
 * <li>specific simplifications for fcomp and bcomp</li>
 * </ul>
 * <p>
 * This class provides one static method per implemented operator. It is
 * designed around a hierarchy of subclasses (mimicking the AST Formula
 * hierarchy), the leaves of which implement operators. This allows good code
 * factoring.
 * </p>
 * 
 * @author Laurent Voisin
 * @author Benoît Lucet
 * @author Thai Son Hoang
 */
public abstract class AssociativeSimplification<T extends Formula<T>> {

	public static Predicate simplifyLand(AssociativePredicate predicate, boolean doMulti) {
		return new LandSimplification(predicate, doMulti).simplify();
	}

	public static Predicate simplifyLor(AssociativePredicate predicate,
			boolean doMulti) {
		return new LorSimplification(predicate, doMulti).simplify();
	}

	public static Expression simplifyInter(AssociativeExpression expression) {
		return new InterSimplification(expression).simplify();
	}

	public static Expression simplifyUnion(AssociativeExpression expression) {
		return new UnionSimplification(expression).simplify();
	}

	public static Expression simplifyMult(AssociativeExpression expression) {
		return new MultSimplification(expression).simplify();
	}

	public static Expression simplifyPlus(AssociativeExpression expression) {
		return new PlusSimplification(expression).simplify();
	}

	public static Expression simplifyComp(AssociativeExpression expression) {
		return new CompSimplification(expression).simplify();
	}

	public static Expression simplifyOvr(AssociativeExpression expression) {
		return new OvrSimplification(expression).simplify();
	}

	private static abstract class PredicateSimplification extends
			AssociativeSimplification<Predicate> {

		// Tells whether to consider duplicates and contradictions.
		private final boolean doMulti;

		PredicateSimplification(AssociativePredicate original, boolean doMulti) {
			super(original, original.getChildren(), doMulti);
			this.doMulti = doMulti;
		}

		@Override
		protected Predicate makeAssociativeFormula() {
			return ff.makeAssociativePredicate(original.getTag(), newChildren,
					null);
		}

		@Override
		protected boolean isContradicting(Predicate child) {
			if (!doMulti) {
				return false;
			}
			final Predicate negation = DLib.makeNeg(child);
			return newChildren.contains(negation);
		}

		@Override
		protected Predicate getContradictionResult() {
			return getDeterminant();
		}

		@Override
		protected Predicate getNeutral() {
			return ff.makeLiteralPredicate(neutralTag(), null);
		}

		@Override
		protected boolean isNeutral(Predicate child) {
			return child.getTag() == neutralTag();
		}

		protected abstract int neutralTag();

		protected Predicate getDeterminant() {
			return ff.makeLiteralPredicate(determinantTag(), null);
		}

		@Override
		protected boolean isDeterminant(Predicate child) {
			return child.getTag() == determinantTag();
		}

		protected abstract int determinantTag();

	}

	private static class LandSimplification extends PredicateSimplification {

		LandSimplification(AssociativePredicate original, boolean doMulti) {
			super(original, doMulti);
		}

		@Override
		protected int neutralTag() {
			return BTRUE;
		}

		@Override
		protected int determinantTag() {
			return BFALSE;
		}

	}

	private static class LorSimplification extends PredicateSimplification {

		LorSimplification(AssociativePredicate original, boolean doMulti) {
			super(original, doMulti);
		}

		@Override
		protected int neutralTag() {
			return BFALSE;
		}

		@Override
		protected int determinantTag() {
			return BTRUE;
		}

	}

	private static abstract class ExpressionSimplification extends
			AssociativeSimplification<Expression> {

		ExpressionSimplification(AssociativeExpression original,
				boolean eliminateDuplicate) {
			super(original, original.getChildren(), eliminateDuplicate);
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
			return makeAssociativeFormula(newChildren);
		}

		protected Expression makeAssociativeFormula(
				Collection<Expression> children) {
			return ff.makeAssociativeExpression(original.getTag(), children,
					null);
		}

		protected boolean isIntegerValue(Expression formula, BigInteger integer) {
			return formula.getTag() == INTLIT
					&& ((IntegerLiteral) formula).getValue().equals(integer);
		}

	}

	/**
	 * This class handles operators Fcomp and Bcomp. In addition to neutral and
	 * determinant, we also process special cases:
	 * <ul>
	 * <li>sequences of the form (S ◁ id) ; (T ◁ id) which are simplified in (S
	 * ∩ T) ◁ id</li>
	 * <li>sequences of the form (S ⩤ id) ; (T ⩤ id) which are simplified in (S
	 * ∪ T) ⩤ id</li>
	 * <li>mixed sequences of the form (S ◁ id) ; (T ⩤ id) which are simplified
	 * in (S ∖ T) ◁ id</li>
	 * </ul>
	 * 
	 * The utility class {@link CompAccumulator} regroups the methods and data
	 * structures needed to perform the special simplifications described above.
	 */
	private static class CompSimplification extends ExpressionSimplification {

		private CompAccumulator accumulator;

		CompSimplification(AssociativeExpression original) {
			super(original, false);
			accumulator = new CompAccumulator(ff);
		}

		@Override
		protected void processChild(Expression child) {
			if (isNeutral(child)) {
				changed = true;
			} else if (isDeterminant(child)) {
				knownResult = getDeterminantResult(child);
			} else if (!accumulator.accumulate(child)) {
				finishAccumulating();
				newChildren.add(child);
			}
		}

		@Override
		protected void finishChildrenProcessing() {
			finishAccumulating();
		}

		private void finishAccumulating() {
			if (accumulator.isAccumulating()) {
				newChildren.add(accumulator.stopAccumulating());
			}
		}

		@Override
		protected boolean isNeutral(Expression child) {
			return child.getTag() == KID_GEN;
		}

		@Override
		protected boolean isDeterminant(Expression child) {
			return child.getTag() == EMPTYSET;
		}

		@Override
		protected Expression getNeutral() {
			return ff.makeAtomicExpression(KID_GEN, null, original.getType());
		}

		@Override
		protected Expression getDeterminantResult(Expression child) {
			return ff.makeEmptySet(original.getType(), null);
		}

		private static class CompAccumulator {

			private final FormulaFactory ff;

			// Count of children accumulated so far
			private int count = 0;

			private List<Expression> domResElements = new ArrayList<Expression>();
			private List<Expression> domSubElements = new ArrayList<Expression>();

			// The most recent occurrence of identity is stored, in order to be
			// able to construct the accumulated result
			private Expression id;

			// The most recent accumulated expression is stored to be returned
			// if nothing else is added afterwards
			private Expression lastChild;

			public CompAccumulator(FormulaFactory ff) {
				this.ff = ff;
				reset();
			}

			public boolean accumulate(Expression expr) {
				final int tag = expr.getTag();
				if (tag != DOMRES && tag != DOMSUB) {
					return false;
				}
				final BinaryExpression bExpr = (BinaryExpression) expr;
				final Expression left = bExpr.getLeft();
				final Expression right = bExpr.getRight();
				if (right.getTag() != KID_GEN) {
					return false;
				}

				++count;
				if (expr.getTag() == DOMRES) {
					domResElements.add(left);
				} else {
					domSubElements.add(left);
				}
				id = right;
				lastChild = expr;
				return true;
			}

			public Expression stopAccumulating() {
				final Expression result = getResult();
				reset();
				return result;
			}

			private void reset() {
				count = 0;
				domResElements.clear();
				domSubElements.clear();
				id = null;
				lastChild = null;
			}

			private Expression getResult() {
				assert isAccumulating();

				if (count == 1) {
					return lastChild;
				}

				final Expression restrictions = makeInter(domResElements);
				final Expression subtractions = makeUnion(domSubElements);
				if (subtractions == null) {
					return ff.makeBinaryExpression(DOMRES, restrictions, id,
							null);
				}
				if (restrictions == null) {
					return ff.makeBinaryExpression(DOMSUB, subtractions, id,
							null);
				}
				final Expression lhs = ff.makeBinaryExpression(SETMINUS,
						restrictions, subtractions, null);
				return ff.makeBinaryExpression(DOMRES, lhs, id, null);
			}

			private Expression makeInter(List<Expression> exprs) {
				return makeAssociativeExpr(BINTER, exprs);
			}

			private Expression makeUnion(List<Expression> exprs) {
				return makeAssociativeExpr(BUNION, exprs);
			}

			private Expression makeAssociativeExpr(int tag,
					List<Expression> exprs) {
				switch (exprs.size()) {
				case 0:
					return null;
				case 1:
					return exprs.get(0);
				default:
					return ff.makeAssociativeExpression(tag, exprs, null);
				}
			}

			public boolean isAccumulating() {
				return count != 0;
			}

		}

	}

	private static class InterSimplification extends ExpressionSimplification {

		InterSimplification(AssociativeExpression original) {
			super(original, true);
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
			return original.getType().getBaseType().toExpression();
		}

	}

	/**
	 * In this class, in addition to neutral and determinant, we also process
	 * signs of arguments. When storing new children, we remove any obvious sign
	 * they could have (negative integer literal or unary minus).
	 */
	private static class MultSimplification extends ExpressionSimplification {

		private boolean positive = true;

		MultSimplification(AssociativeExpression original) {
			super(original, false);
		}

		@Override
		protected void processChild(Expression child) {
			switch (child.getTag()) {
			case INTLIT:
				processIntegerLiteral((IntegerLiteral) child);
				break;
			case UNMINUS:
				processUnaryMinus((UnaryExpression) child);
				break;
			default:
				super.processChild(child);
				break;
			}
		}

		private void processIntegerLiteral(IntegerLiteral child) {
			final BigInteger val = child.getValue();
			if (val.signum() < 0) {
				negateResult();
				processChild(ff.makeIntegerLiteral(val.negate(), null));
				return;
			}
			super.processChild(child);
		}

		private void processUnaryMinus(UnaryExpression child) {
			negateResult();
			processChild(child.getChild());
		}

		private void negateResult() {
			changed = true;
			positive = !positive;
		}

		@Override
		protected Expression makeResult() {
			final Expression unsigned = super.makeResult();
			if (positive) {
				return unsigned;
			} else {
				return opposite(unsigned);
			}
		}

		private Expression opposite(Expression unsigned) {
			switch (unsigned.getTag()) {
			case INTLIT:
				final BigInteger value = ((IntegerLiteral) unsigned).getValue();
				return ff.makeIntegerLiteral(value.negate(), null);
			default:
				return ff.makeUnaryExpression(UNMINUS, unsigned, null);
			}
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

	}

	/**
	 * For simplifications over this operator, we use backward traversing. This
	 * allows a simpler implementation:
	 * <ul>
	 * <li>the traversing is stopped as soon as a type expression is encountered
	 * </li>
	 * <li>the handling of duplicates is correctly done from right to left</li>
	 * </ul>
	 */
	private static class OvrSimplification extends ExpressionSimplification {

		OvrSimplification(AssociativeExpression original) {
			super(original, true);
		}

		@Override
		protected void processChildren() {
			// Traverse the elements backwards.
			for (int i = children.length - 1; i >= 0; i--) {
				processChild(children[i]);
				if (knownResult != null) {
					return;
				}
			}
		}

		@Override
		protected void processChild(Expression child) {
			if (isNeutral(child)) {
				changed = true;
			} else {
				newChildren.add(child);
				if (child.isATypeExpression()) {
					// If a type expression is encountered, then the result is
					// known and the process stops.
					knownResult = makeResult();
				}
			}
		}

		@Override
		protected Expression makeResult() {
			if (knownResult != null) {
				return knownResult;
			}
			int size = newChildren.size();
			if (size == 0) {
				return getNeutral();
			} else if (size == 1) {
				return newChildren.iterator().next();
			} else if (changed || size != children.length) {
				final ArrayList<Expression> toReverse = new ArrayList<Expression>(
						newChildren);
				Collections.reverse(toReverse);
				return makeAssociativeFormula(toReverse);
			}
			return original;
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

	private static class PlusSimplification extends ExpressionSimplification {

		PlusSimplification(AssociativeExpression original) {
			super(original, false);
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

	private static class UnionSimplification extends ExpressionSimplification {

		UnionSimplification(AssociativeExpression original) {
			super(original, true);
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

	// Original associative formula to simplify
	protected final T original;
	protected final T[] children;

	// Children of the resulting formula, so far
	protected final Collection<T> newChildren;

	// If non-null, this contains the result, ignore newChildren above
	protected T knownResult;

	// Set to true if a new result must be created
	protected boolean changed = false;

	protected FormulaFactory ff;

	protected AssociativeSimplification(T original, T[] children,
			boolean eliminateDuplicate) {
		this.original = original;
		this.children = children;

		this.ff = original.getFactory();

		if (eliminateDuplicate) {
			this.newChildren = new LinkedHashSet<T>();
		} else {
			this.newChildren = new ArrayList<T>();
		}

	}

	protected T simplify() {
		processChildren();
		return makeResult();
	}

	protected void processChildren() {
		for (T child : children) {
			processChild(child);
			if (knownResult != null) {
				return;
			}
		}
		finishChildrenProcessing();
	}

	protected void finishChildrenProcessing() {
		// Do nothing by default
	}

	protected void processChild(T child) {
		if (isNeutral(child)) {
			changed = true;
		} else if (isDeterminant(child)) {
			knownResult = getDeterminantResult(child);
		} else if (isContradicting(child)) {
			knownResult = getContradictionResult();
		} else {
			newChildren.add(child);
		}
	}

	protected abstract boolean isNeutral(T child);

	protected abstract boolean isDeterminant(T child);

	protected abstract boolean isContradicting(T child);

	protected abstract T getContradictionResult();

	protected T makeResult() {
		if (knownResult != null) {
			return knownResult;
		}
		int size = newChildren.size();
		if (size == 0) {
			return getNeutral();
		} else if (size == 1) {
			return newChildren.iterator().next();
		} else if (changed || size != children.length) {
			return makeAssociativeFormula();
		}
		return original;
	}

	protected abstract T getNeutral();

	protected abstract T makeAssociativeFormula();

	// Default behaviour that is overriden is specific cases such as Fcomp
	// and Bcomp
	protected T getDeterminantResult(T child) {
		return child;
	}

}
