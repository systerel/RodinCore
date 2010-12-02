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
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.EMPTYSET;
import static org.eventb.core.ast.Formula.INTLIT;
import static org.eventb.core.ast.Formula.KID_GEN;
import static org.eventb.core.ast.Formula.MUL;
import static org.eventb.core.ast.Formula.UNMINUS;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.eventbExtensions.DLib;

/**
 * Framework for simplifying associative formulas. This class implements several
 * simplifications:
 * <ul>
 * <li>neutral element of the operator is removed</li>
 * <li>determinant element of the operator determines the result</li>
 * <li>duplicate elements are removed (depending on the operator)</li>
 * <li>contradiction determines the result (depending on the operator)</li>
 * </ul>
 * <p>
 * This class provides one static method per implemented operator. It is
 * designed around a hierarchy of subclasses (mimicking the AST Formula
 * hierarchy), the leafs of which implement operators. This allows good code
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

	public static Expression simplifyFcomp(AssociativeExpression expression,
			DLib dLib) {
		return new FcompSimplification(expression, dLib).simplify();
	}

	public static Expression simplifyBcomp(AssociativeExpression expression,
			DLib dLib) {
		return new BcompSimplification(expression, dLib).simplify();
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

	private static class BcompSimplification extends ExpressionSimplification {

		BcompSimplification(AssociativeExpression original, DLib dLib) {
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

	}

	private static class FcompSimplification extends ExpressionSimplification {

		FcompSimplification(AssociativeExpression original, DLib dLib) {
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

	private static class MultSimplification extends ExpressionSimplification {

		private boolean positive = true;
		private boolean changed = false;

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
				newChildren.add(child);
				break;
			}
		}

		private void processIntegerLiteral(IntegerLiteral child) {
			BigInteger val = ((IntegerLiteral) child).getValue();
			if (val.signum() == 0) {
				knownResult = child;
			}
			if (val.signum() < 0) {
				val = val.abs();
				negateResult();
			}
			if (val.equals(ONE)) {
				changed = true;
			} else {
				newChildren.add(ff.makeIntegerLiteral(val, null));
			}
		}

		private void processUnaryMinus(UnaryExpression child) {
			negateResult();
			processChild(child.getChild());
		}

		private void negateResult() {
			positive = !positive;
			changed = true;
		}

		@Override
		protected Expression makeResult() {
			if (knownResult != null) {
				return knownResult;
			}
			if (!changed) {
				return original;
			}
			if (positive) {
				return unsignedResult();
			} else {
				return opposite(unsignedResult());
			}
		}

		private Expression unsignedResult() {
			if (newChildren.isEmpty()) {
				return ff.makeIntegerLiteral(ONE, null);
			}
			if (newChildren.size() == 1) {
				return ((ArrayList<Expression>) newChildren).get(0);
			}
			return ff.makeAssociativeExpression(MUL, newChildren, null);
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

	private static class OvrSimplification extends ExpressionSimplification {

		OvrSimplification(AssociativeExpression original, DLib dLib) {
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
	protected final Collection<T> newChildren;

	// If non-null, this contains the result, ignore newChildren above
	protected T knownResult;

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


	private void processChildren() {
		for (T child : children) {
			processChild(child);
			if (knownResult != null) {
				return;
			}
		}
	}

	protected void processChild(T child) {
		if (isNeutral(child)) {
			// ignore
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
		} else if (size != children.length) {
			return makeAssociativeFormula();
		}
		return original;
	}

	protected abstract T getNeutral();

	protected abstract T makeAssociativeFormula();

	// default behaviour that is overriden is specific cases such as Fcomp
	// and Bcomp
	protected T getDeterminantResult(T child) {
		return child;
	}

}
