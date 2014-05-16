/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.ProverRule;

/**
 * Basic automated rewriter for the Event-B sequent prover.
 */
@SuppressWarnings({"unused", "cast"})
public class RanCompRewriterImpl extends DefaultRewriter {

	private BinaryExpression subExp;

	public RanCompRewriterImpl(BinaryExpression subExp) {
		super(true);
		this.subExp = subExp;
	}
		
	%include {FormulaV2.tom}
	
	@ProverRule( { "DERIV_FCOMP_RANRES", "DERIV_FCOMP_RANSUB" })
	@Override
	public Expression rewrite(AssociativeExpression expression) {
		FormulaFactory ff = expression.getFactory();
		if (subExp.getTag() != Expression.RANRES &&
				subExp.getTag() != Expression.RANSUB)
			return expression;

	    %match (Expression expression) {

			/**
	    	 * Set Theory : p;...;(q ▷ S);r;...;s == ((p;...;q) ▷ S);r;...;s
	    	 *              p;...;(q ⩥ S);r;...;s == ((p;...;q) ⩥ S);r;...;s
	    	 */
			Fcomp(children) -> {
				List<Expression> newChildren = new ArrayList<Expression>();
				List<Expression> pToQ = new ArrayList<Expression>();
				Expression S = null;
				boolean found = false;
				for (Expression child : `children) {
					if (found)
						newChildren.add(child);
					else if (child == subExp) {
						found = true;						
						pToQ.add(((BinaryExpression) subExp).getLeft());
						S = ((BinaryExpression) subExp).getRight();
					}
					else {
						pToQ.add(child);
					}
				}
				
				if (pToQ.size() <= 1)
					return expression;
				
				Expression pToQComp = makeCompIfNeccessary(pToQ);
				
				Expression ranMan = ff.makeBinaryExpression(
						subExp.getTag(), pToQComp, S, null);

				List<Expression> exps =
						new ArrayList<Expression>(newChildren.size() + 1);
				exps.add(ranMan);
				exps.addAll(newChildren);

				return makeCompIfNeccessary(exps);
			}
			
	    }
	    return expression;
	}

	private Expression makeCompIfNeccessary(List<Expression> children) {
		final Expression first = children.get(0);
		if (children.size() == 1)
			return first;
		final FormulaFactory ff = first.getFactory();
		return ff.makeAssociativeExpression(Expression.FCOMP, children, null);
	}
}
