/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
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
import org.eventb.internal.core.seqprover.eventbExtensions.OnePointSimplifier;

/**
 * Class used to simplify the functional image of a set in comprehension
 * (especially a lambda).
 * <p>
 * The static method <code>rewrite(Expression expr, FormulaFactory ff)</code>
 * first checks that the given expression <code>expr</code> is a functional
 * image of a set in comprehension. Then, simplifies it or returns
 * <code>null</code> if the result has not the expected form.
 * </p>
 * <p>
 * In the simplification, from {x.P|E}(y), create formula #x.y|->A = x|->E where
 * A is an artificially bound variable. By applying rewriters, we split maplets
 * to get #x.y=x & A=E and apply the One Point Rule. If we obtain A = E then E
 * is the simplification we expect.
 * </p>
 */
public class LambdaComputer {

	/**
	 * Returns the simplification of applying a comprehension set (especially a
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

	/**
	 * From {x.P|E}(y), create formula #x.y|->A = x|->E where A is an
	 * artificially bound variable. Then, apply repeatedly the auto-rewriter (to
	 * split equalities between maplets) and the One Point Rule.
	 * <p>
	 * If these simplifications produce predicate A = E, then E is the result to
	 * return. Otherwise the simplification fails.
	 * </p>
	 * 
	 * @return the simplification of a functional image of lambda expression
	 */
	private Expression simplify() {
		final BoundIdentDecl[] decls = cset.getBoundIdentDecls();
		final BoundIdentifier AInExists = ff.makeBoundIdentifier(decls.length,
				null, funImg.getType());
		final Expression yMapstoA = ff.makeBinaryExpression(MAPSTO, arg,
				AInExists, null);
		final Predicate equals = ff.makeRelationalPredicate(EQUAL, yMapstoA,
				cset.getExpression(), null);
		final Predicate exists = ff.makeQuantifiedPredicate(EXISTS, decls,
				equals, null);
		final AutoRewriterImpl rewriter = new AutoRewriterImpl();
		boolean changed;
		Predicate pred = exists;
		do {
			final Predicate old = pred;
			pred = pred.rewrite(rewriter);
			pred = OnePointSimplifier.rewrite(pred);
			changed = old != pred;
		} while (changed);
		if (hasExpectedFinalForm(pred)) {
			return ((RelationalPredicate) pred).getRight();
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
