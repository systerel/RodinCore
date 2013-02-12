/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static org.eventb.core.ast.Formula.BOUND_IDENT;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.FUNIMAGE;
import static org.eventb.core.ast.Formula.MAPSTO;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.internal.core.seqprover.eventbExtensions.OnePointProcessorInference;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites.Level;

/**
 * Implements simplification of functional images through a set in comprehension
 * (typically a lambda).
 * <p>
 * The static method <code>rewrite(Expression expr, FormulaFactory ff)</code>
 * first checks that the given expression is a functional image through a set in
 * comprehension. Then, simplifies it or returns <code>null</code> if
 * simplification is not possible. The latter case can happen when the
 * comprehension set expression has not the same pattern as the argument of the
 * function.
 * </p>
 * <p>
 * The algorithm used for simplifying expression <code>{x.P|E}(y)</code> is
 * <ol>
 * <li>Create formula <code>y↦A ∈ {x.P|E}</code> where <code>A</code> is a fresh
 * intermediate variable.</li>
 * <li>Rewrite the previous formula to <code>∃x.y↦A = E</code>.</li>
 * <li>Apply the AutoRewriter to split maplets and apply the one point rule
 * until reaching a fixpoint.</li>
 * <li>If we obtain <code>A = E</code> then <code>E</code> is the simplification
 * we expect.</li>
 * </ol>
 * As intermediate variable <code>A</code> is introduced as a bound identifier,
 * great care must be taken to shift bound identifiers appropriately in formulas
 * <code>y</code> and <code>{x.P|E}</code>
 * </p>
 * <p>
 * It is OK to use the auto-rewriter for simplifying maplets, as this
 * computation is currently performed as part of the auto-rewriter itself.
 * </p>
 */
public class LambdaComputer {

	/**
	 * Returns the simplification of applying a comprehension set (typically a
	 * lambda) to an expression.
	 * 
	 * @param expr
	 *            the functional image
	 * @param ff
	 *            formula factory to use
	 * @return the simplification of the given functional image or
	 *         <code>null</code> in case of failure
	 */
	public static Expression rewrite(Expression expr, FormulaFactory ff) {
		final LambdaComputer lc = new LambdaComputer(expr, ff);
		if (!lc.verify()) {
			return null;
		}
		return lc.simplify();
	}

	private final Expression expr;
	private final FormulaFactory ff;

	// Derived fields computed by verify()
	private BinaryExpression funImg;
	private QuantifiedExpression cset;
	private Expression arg;

	private LambdaComputer(Expression expr, FormulaFactory ff) {
		this.expr = expr;
		this.ff = ff;
	}

	private boolean verify() {
		if (expr.getTag() != FUNIMAGE)
			return false;
		funImg = (BinaryExpression) expr;
		final Expression fun = funImg.getLeft();
		if (fun.getTag() != CSET)
			return false;
		cset = (QuantifiedExpression) fun;
		arg = funImg.getRight();
		return true;
	}

	private Expression simplify() {
		final BoundIdentDecl[] decls = cset.getBoundIdentDecls();

		// Intermediate variable A is bound just outside the existential
		final BoundIdentifier AInExists = ff.makeBoundIdentifier(decls.length,
				null, funImg.getType());

		// The argument needs to be pushed through A and the bound declaration.
		final Expression innerArg = arg.shiftBoundIdentifiers(1 + decls.length);
		final Expression yMapstoA = ff.makeBinaryExpression(MAPSTO, innerArg,
				AInExists, null);

		// The expression must be pushed through A, but in the context of the
		// comprehension set. Do not attempt to simplify the following lines!
		final QuantifiedExpression innerCset = (QuantifiedExpression) cset
				.shiftBoundIdentifiers(1);
		final Expression innerExpr = innerCset.getExpression();

		final Predicate equals = ff.makeRelationalPredicate(EQUAL, yMapstoA,
				innerExpr, null);
		final Predicate exists = ff.makeQuantifiedPredicate(EXISTS, decls,
				equals, null);
		final AutoRewriterImpl rewriter = new AutoRewriterImpl(Level.L0);
		boolean changed;
		Predicate pred = exists;
		do {
			final Predicate old = pred;
			pred = pred.rewrite(rewriter);
			pred = OnePointProcessorInference.rewrite(pred);
			changed = old != pred;
		} while (changed);
		if (hasExpectedFinalForm(pred)) {
			final Expression right = ((RelationalPredicate) pred).getRight();
			// Remove implicit quantification on A
			return right.shiftBoundIdentifiers(-1);
		}
		return null;
	}

	private boolean hasExpectedFinalForm(Predicate pred) {
		if (pred.getTag() != EQUAL)
			return false;
		final Expression left = ((RelationalPredicate) pred).getLeft();
		if (left.getTag() != BOUND_IDENT)
			return false;
		if (((BoundIdentifier) left).getBoundIndex() != 0)
			return false;
		return true;
	}

}
