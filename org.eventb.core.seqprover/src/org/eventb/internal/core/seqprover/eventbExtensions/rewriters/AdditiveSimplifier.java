/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static org.eventb.core.ast.Formula.MINUS;
import static org.eventb.core.ast.Formula.PLUS;
import static org.eventb.core.ast.Formula.UNMINUS;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.UnaryExpression;

/**
 * Implementation of the second pass of the additive simplifier. This pass
 * removes the canceled out terms of an additive expression or arithmetic
 * comparison. The list of terms to cancel has been defined by the first pass.
 * 
 * @author Laurent Voisin
 */
public class AdditiveSimplifier {

	class Accumulator {

		final Expression original;
		final List<Expression> children;

		boolean changed;

		Accumulator(AssociativeExpression original) {
			this.original = original;
			this.children = new ArrayList<Expression>();
		}

		public void add(Expression oldChild, Expression newChild) {
			if (oldChild == newChild) {
				children.add(oldChild);
				return;
			}
			changed = true;
			if (newChild == null) {
				return;
			}
			if (newChild.getTag() == PLUS) {
				// Flatten on the fly
				final AssociativeExpression aexpr = (AssociativeExpression) newChild;
				for (Expression grandChild : aexpr.getChildren()) {
					children.add(grandChild);
				}
				return;
			}
			final int count = children.size();
			if (newChild.getTag() == UNMINUS && count != 0) {
				// Coalesce with previous siblings --> binary minus
				final Expression left = makePLUS(children);
				children.clear();
				final Expression right = ((UnaryExpression) newChild)
						.getChild();
				children.add(makeMINUS(left, right));
				return;
			}
			// Default case
			children.add(newChild);
		}

		public Expression getResult() {
			if (!changed) {
				return original;
			}
			return makePLUS(children);
		}

	}

	final FormulaFactory ff;

	final Set<IPosition> positions;

	private AdditiveSimplifier(IPosition[] positions, FormulaFactory ff) {
		this.positions = new HashSet<IPosition>(Arrays.asList(positions));
		this.ff = ff;
	}

	private boolean contains(IPosition pos) {
		return positions.contains(pos);
	}

	/**
	 * Simplifies the given arithmetic expression by removing the terms
	 * specified by the given positions, while performing only minimal changes
	 * to the input formula.
	 * 
	 * @param expr
	 *            original expression (must be type-checked and arithmetic)
	 * @param positions
	 *            position of terms to be removed
	 * @param ff
	 *            formula factory to use for building the result
	 * @return a copy of the given expression with the given terms removed
	 */
	public static Expression simplify(Expression expr, IPosition[] positions,
			FormulaFactory ff) {
		assert expr.isTypeChecked() && expr.getType() instanceof IntegerType;
		final AdditiveSimplifier s = new AdditiveSimplifier(positions, ff);
		return s.simplifyExpr(expr, IPosition.ROOT);
	}

	/**
	 * Simplifies the given relational predicate by removing the terms specified
	 * by the given positions, while performing only minimal changes to the
	 * input formula.
	 * 
	 * @param pred
	 *            original predicate (must be type-checked and arithmetic)
	 * @param positions
	 *            position of terms to be removed
	 * @param ff
	 *            formula factory to use for building the result
	 * @return a copy of the original predicate with the given terms removed
	 */
	public static RelationalPredicate simplify(RelationalPredicate pred,
			IPosition[] positions, FormulaFactory ff) {
		assert pred.isTypeChecked();
		final AdditiveSimplifier s = new AdditiveSimplifier(positions, ff);
		final Expression left = pred.getLeft();
		final Expression right = pred.getRight();

		IPosition curPos = IPosition.ROOT.getFirstChild();
		final Expression newLeft = s.simplifyExpr(left, curPos);
		curPos = curPos.getNextSibling();
		final Expression newRight = s.simplifyExpr(right, curPos);

		if (left == newLeft && right == newRight) {
			return pred;
		}
		return ff.makeRelationalPredicate(pred.getTag(), newLeft, newRight,
				null);
	}

	private Expression simplifyExpr(Expression expr, IPosition curPos) {
		Expression result = this.internalSimplify(expr, curPos);
		if (result == null) {
			result = makeZERO();
		}
		return result;
	}

	private Expression internalSimplify(Expression expr, IPosition curPos) {
		if (contains(curPos)) {
			return null;
		}
		switch (expr.getTag()) {
		case PLUS:
			return simplifyPLUS((AssociativeExpression) expr, curPos);
		case MINUS:
			return simplifyMINUS((BinaryExpression) expr, curPos);
		}
		return expr;
	}

	private Expression simplifyPLUS(AssociativeExpression expr, IPosition curPos) {
		final Expression[] children = expr.getChildren();
		final Accumulator acc = new Accumulator(expr);
		IPosition childPos = curPos.getFirstChild();
		for (Expression child : children) {
			Expression newChild = internalSimplify(child, childPos);
			acc.add(child, newChild);
			childPos = childPos.getNextSibling();
		}
		return acc.getResult();
	}

	private Expression simplifyMINUS(BinaryExpression expr, IPosition curPos) {
		final Expression left = expr.getLeft();
		final Expression right = expr.getRight();

		final IPosition leftPos = curPos.getFirstChild();
		final Expression newLeft = internalSimplify(left, leftPos);
		final IPosition rightPos = leftPos.getNextSibling();
		final Expression newRight = internalSimplify(right, rightPos);

		if (left == newLeft && right == newRight) {
			return expr;
		}
		return makeMINUS(newLeft, newRight);
	}

	private Expression makePLUS(List<Expression> children) {
		switch (children.size()) {
		case 0:
			return null;
		case 1:
			return children.get(0);
		default:
			return ff.makeAssociativeExpression(PLUS, children, null);
		}
	}

	private Expression makeMINUS(Expression left, Expression right) {
		if (left == null) {
			if (right == null) {
				return null;
			}
			return opposite(right);
		}
		if (right == null) {
			return left;
		}
		if (right.getTag() == UNMINUS) {
			final Accumulator acc = new Accumulator(null);
			acc.add(null, left);
			acc.add(null, opposite(right));
			return acc.getResult();
		}
		return ff.makeBinaryExpression(MINUS, left, right, null);
	}

	private Expression opposite(Expression expr) {
		if (expr.getTag() == UNMINUS) {
			return ((UnaryExpression) expr).getChild();
		}
		return ff.makeUnaryExpression(UNMINUS, expr, null);
	}

	private Expression makeZERO() {
		return ff.makeIntegerLiteral(BigInteger.ZERO, null);
	}

}
