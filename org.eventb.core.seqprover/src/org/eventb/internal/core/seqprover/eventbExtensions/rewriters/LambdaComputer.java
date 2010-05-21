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
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
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
 * Class that computes the functional image of a lambda expression
 */
public class LambdaComputer {
	
	private static final FormulaFactory ff = FormulaFactory.getDefault();
	
	private final QuantifiedExpression lambda;
	private final BinaryExpression funImg;
	private final Expression arg;
	
	public LambdaComputer(BinaryExpression funImg, QuantifiedExpression lambda,
			Expression arg) {
		assert lambda.getTag() == Expression.CSET;
		assert lambda.getForm() == QuantifiedExpression.Form.Lambda;
		assert lambda.getExpression().getTag() == Expression.MAPSTO;
		this.lambda = lambda;
		this.funImg = funImg;
		this.arg = arg;
	}

	/**
	 * From (%x.P|E)(y), create formula #x.y|->A = x|->E and applies
	 * successively the rewriter SIMP_IN_COMPSET, and the One Point Rule to get
	 * a rewritten expression E which corresponds to the simplification of a
	 * functional image of lambda expression.
	 * 
	 * @return the simplification of a functional image of lambda expression
	 */
	public Expression rewrite() {
		final BoundIdentDecl[] decls = lambda.getBoundIdentDecls();
		final BoundIdentifier AInExists = ff.makeBoundIdentifier(decls.length,
				null, funImg.getType());
		final Expression yMapstoA = ff.makeBinaryExpression(MAPSTO, arg,
				AInExists, null);
		final Predicate equals = ff.makeRelationalPredicate(EQUAL, yMapstoA,
				lambda.getExpression(), null);
		final Predicate exists = ff.makeQuantifiedPredicate(EXISTS, decls,
				equals, null);	
		final AutoRewriterImpl rewriter = new AutoRewriterImpl();
		Predicate oldP = exists;
		Predicate newP = exists;
		do { 
			oldP = newP;
			newP = oldP.rewrite(rewriter);
			newP = OnePointSimplifier.rewrite(newP);
		} while (oldP != newP);
		if (newP.getTag() == EQUAL) {
			final RelationalPredicate resultEquals = (RelationalPredicate) newP;
			final Expression left = resultEquals.getLeft();
			final Expression right = resultEquals.getRight();
			assert left.getTag() == BOUND_IDENT
					&& ((BoundIdentifier) left).getBoundIndex() == 0;
			return right;
		}
		// Failed
		return null;
	}
	
}
