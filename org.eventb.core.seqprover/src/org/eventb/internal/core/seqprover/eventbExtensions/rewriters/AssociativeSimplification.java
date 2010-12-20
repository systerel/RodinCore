/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
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
import org.eventb.core.ast.AtomicExpression;
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

	public static Predicate simplifyLand(AssociativePredicate predicate,
			DLib dLib) {
		return new LandSimplification(predicate, dLib).simplify();
	}

	public static Predicate simplifyLor(AssociativePredicate predicate,
			DLib dLib) {
		return new LorSimplification(predicate, dLib).simplify();
	}

	public static Expression simplifyInter(AssociativeExpression expression,
			DLib dLib) {
		return new InterSimplification(expression, dLib).simplify();
	}

	public static Expression simplifyUnion(AssociativeExpression expression,
			DLib dLib) {
		return new UnionSimplification(expression, dLib).simplify();
	}

	public static Expression simplifyMult(AssociativeExpression expression,
			DLib dLib) {
		return new MultSimplification(expression, dLib).simplify();
	}

	public static Expression simplifyPlus(AssociativeExpression expression,
			DLib dLib) {
		return new PlusSimplification(expression, dLib).simplify();
	}

	public static Expression simplifyComp(AssociativeExpression expression,
			DLib dLib) {
		return new CompSimplification(expression, dLib).simplify();
	}

	public static Expression simplifyOvr(AssociativeExpression expression,
			DLib dLib) {
		return new OvrSimplification(expression, dLib).simplify();
	}

	private static abstract class PredicateSimplification extends
			AssociativeSimplification<Predicate> {

		PredicateSimplification(AssociativePredicate original, DLib dLib) {
			super(original, original.getChildren(), dLib);
		}

		@Override
		protected Predicate makeAssociativeFormula() {
			return ff.makeAssociativePredicate(original.getTag(), newChildren,
					null);
		}

		@Override
		protected boolean isContradicting(Predicate child) {
			final Predicate negation = dLib.makeNeg(child);
			return newChildren.contains(negation);
		}

		@Override
		protected Predicate getContradictionResult() {
			return getDeterminant();
		}

		@Override
		protected boolean eliminateDuplicate() {
			return true;
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

		LandSimplification(AssociativePredicate original, DLib dLib) {
			super(original, dLib);
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

		LorSimplification(AssociativePredicate original, DLib dLib) {
			super(original, dLib);
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

		ExpressionSimplification(AssociativeExpression original, DLib dLib) {
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
	 * The utility class {@link CompSpecialHandler} regroups the methods and
	 * data structures needed to perform the special simplifications described
	 * above.
	 */
	private static class CompSimplification extends ExpressionSimplification {

		private CompSpecialHandler specialHandler;

		CompSimplification(AssociativeExpression original, DLib dLib) {
			super(original, dLib);
			specialHandler = new CompSpecialHandler();
		}

		@Override
		protected void processChild(Expression child) {
			if (isNeutral(child)) {
				changed = true;
			} else if (isDeterminant(child)) {
				knownResult = getDeterminantResult(child);
			} else if (specialHandler.isProcessing()) {
				// processing consecutive special cases
				if (specialHandler.match(child)) {
					// continue processing
					specialHandler.process(child);
					assert !eliminateDuplicate();
					((ArrayList<Expression>) newChildren).set(
							newChildren.size() - 1,
							specialHandler.buildResult());
				} else {
					// stop processing
					specialHandler.stop();
					newChildren.add(child);
				}
			} else if (specialHandler.match(child)) {
				// start processing
				specialHandler.process(child);
				newChildren.add(child);
			} else {
				// no special handling
				newChildren.add(child);
			}
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

		private class CompSpecialHandler {

			// As long as consecutive special cases are met, this boolean holds
			// true
			private boolean isProcessing;

			private List<Expression> domResElements;
			private List<Expression> domSubElements;

			// The most recent occurrence of identity is stored, in order to be
			// able type the resulting expression correctly
			private AtomicExpression id;

			public CompSpecialHandler() {
				isProcessing = false;
				domResElements = new ArrayList<Expression>();
				domSubElements = new ArrayList<Expression>();
			}

			public boolean match(Expression expression) {
				if (expression.getTag() != DOMRES
						&& expression.getTag() != DOMSUB) {
					return false;
				}
				final BinaryExpression bExpr = (BinaryExpression) expression;
				if (bExpr.getRight().getTag() != KID_GEN) {
					return false;
				}
				return true;
			}

			public void process(Expression expr) {
				assert match(expr);
				isProcessing = true;
				final BinaryExpression bExpr = (BinaryExpression) expr;

				id = (AtomicExpression) bExpr.getRight();
				if (expr.getTag() == DOMRES) {
					domResElements.add(bExpr.getLeft());
				} else if (expr.getTag() == DOMSUB) {
					domSubElements.add(bExpr.getLeft());
				}
			}

			public Expression buildResult() {
				final Expression result;

				if (domResElements.isEmpty()) {
					// only domSub operators
					final Expression lhs = ff.makeAssociativeExpression(BUNION,
							domSubElements, null);
					result = ff.makeBinaryExpression(DOMSUB, lhs, id, null);
				} else if (domSubElements.isEmpty()) {
					// only domRes operators
					final Expression lhs = ff.makeAssociativeExpression(BINTER,
							domResElements, null);
					result = ff.makeBinaryExpression(DOMRES, lhs, id, null);
				} else {
					// both domSub and domRes operators are present
					final Expression inter;
					final Expression union;
					if (domResElements.size() == 1) {
						inter = domResElements.get(0);
					} else {
						inter = ff.makeAssociativeExpression(BINTER,
								domResElements, null);
					}
					if (domSubElements.size() == 1) {
						union = domSubElements.get(0);
					} else {
						union = ff.makeAssociativeExpression(BUNION,
								domSubElements, null);
					}
					// building the set subtraction
					final Expression lhs = ff.makeBinaryExpression(SETMINUS,
							inter, union, null);
					result = ff.makeBinaryExpression(DOMRES, lhs, id, null);
				}

				return result;
			}

			public void stop() {
				isProcessing = false;
				domResElements.clear();
				domSubElements.clear();
			}

			public boolean isProcessing() {
				return isProcessing;
			}

		}

	}

	private static class InterSimplification extends ExpressionSimplification {

		InterSimplification(AssociativeExpression original, DLib dLib) {
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

	/**
	 * In this class, in addition to neutral and determinant, we also process
	 * signs of arguments. When storing new children, we remove any obvious sign
	 * they could have (negative integer literal or unary minus).
	 */
	private static class MultSimplification extends ExpressionSimplification {

		private boolean positive = true;

		MultSimplification(AssociativeExpression original, DLib dLib) {
			super(original, dLib);
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

	}

	/**
	 * For simplifications over this operator, we use backward traversing. This
	 * allows a simpler implementation:
	 * <ul>
	 * <li>the traversing is stopped as soon as a type expression is encountered
	 * </li>
	 * <li>the handling of duplicates is correctly done from left to right</li>
	 * </ul>
	 */
	private static class OvrSimplification extends ExpressionSimplification {

		OvrSimplification(AssociativeExpression original, DLib dLib) {
			super(original, dLib);
		}

		@Override
		protected void processChildren() {
			if (children[0].isATypeExpression()) {
				// If the first element is a type, then the original expression
				// is returned. This is possible because there is no absorbing
				// element for operator ovr.
				knownResult = original;
			} else if (children[children.length - 1].isATypeExpression()) {
				// If the last element is a type, then only the last element is
				// returned.
				knownResult = children[children.length - 1];
			} else {
				// Otherwise, the elements are traversed backwards.
				for (int i = children.length - 1; i >= 0; i--) {
					processChild(children[i]);
					if (knownResult != null) {
						return;
					}
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
					final ArrayList<Expression> list = new ArrayList<Expression>(
							newChildren);
					// Reverting the order
					Collections.reverse(list);
					newChildren = list;
					knownResult = makeAssociativeFormula();
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
				final ArrayList<Expression> list = new ArrayList<Expression>(
						newChildren);
				// Reverting the order
				Collections.reverse(list);
				newChildren = list;
				return makeAssociativeFormula();
			}
			return original;
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
			return false;
		}

		@Override
		protected Expression getNeutral() {
			return ff.makeEmptySet(original.getType(), null);
		}

	}

	private static class PlusSimplification extends ExpressionSimplification {

		PlusSimplification(AssociativeExpression original, DLib dLib) {
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

	private static class UnionSimplification extends ExpressionSimplification {

		UnionSimplification(AssociativeExpression original, DLib dLib) {
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

	// Original associative formula to simplify
	protected final T original;
	protected final T[] children;

	// Children of the resulting formula, so far
	protected Collection<T> newChildren;

	// If non-null, this contains the result, ignore newChildren above
	protected T knownResult;

	// Set to true if a new result must be created
	protected boolean changed = false;

	protected final DLib dLib;
	protected FormulaFactory ff;

	protected AssociativeSimplification(T original, T[] children, DLib dLib) {
		this.original = original;
		this.children = children;
		this.dLib = dLib;
		this.ff = dLib.getFormulaFactory();

		if (eliminateDuplicate()) {
			this.newChildren = new LinkedHashSet<T>();
		} else {
			this.newChildren = new ArrayList<T>();
		}

	}

	protected abstract boolean eliminateDuplicate();

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
