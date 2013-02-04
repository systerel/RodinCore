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
import java.util.Collection;

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
import org.eventb.core.ast.PowerSetType;
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
@SuppressWarnings("unused")
public class SetMinusRewriterImpl extends DefaultRewriter {

	public SetMinusRewriterImpl() {
		super(true);
	}
		
	%include {FormulaV2.tom}
	
    @ProverRule( { "DERIV_TYPE_SETMINUS_BINTER", "DERIV_TYPE_SETMINUS_BUNION",
       	           "DERIV_TYPE_SETMINUS_SETMINUS" })
	@Override
	public Expression rewrite(BinaryExpression expression) {
		FormulaFactory ff = expression.getFactory();
	    %match (Expression expression) {

			/**
	    	 * Set Theory : U ∖ (S ∩ ... ∩ T) == (U ∖ S) ∪ ... ∪ (U ∖ T)
	    	 */
			SetMinus(U, BInter(children)) -> {
				if (`U.isATypeExpression()) {
					return makeSetMinusAssociative(Expression.BUNION, `U,
							`children);
				}
				return expression;
			}

			/**
	    	 * Set Theory : U ∖ (S ∪ ... ∪ T) == (U ∖ S) ∩ ... ∩ (U ∖ T)
	    	 */
			SetMinus(U, BUnion(children)) -> {
				if (`U.isATypeExpression()) {
					return makeSetMinusAssociative(Expression.BINTER, `U,
							`children);
				}
				return expression;
			}
			
			/**
	    	 * Set Theory : U ∖ (S ∖ T) == (U ∖ S) ∪ T
	    	 */
			SetMinus(U, SetMinus(S, T)) -> {
				if (`U.isATypeExpression()) {
					Expression uMinusS = ff.makeBinaryExpression(
							Expression.SETMINUS, `U, `S, null);
					Collection<Expression> expressions = new ArrayList<Expression>();
					expressions.add(uMinusS);
					expressions.add(`T);
					return ff.makeAssociativeExpression(Expression.BUNION,
							expressions, null);
				}
				return expression;
			}
	    }
	    return expression;
	}

	private Expression makeSetMinusAssociative(int tag, Expression U,
			Expression [] children) {
		FormulaFactory ff = U.getFactory();
		Expression [] newChildren = new Expression[children.length];
		for (int i = 0; i < children.length; ++i) {
			newChildren[i] = ff.makeBinaryExpression(
					Expression.SETMINUS, U, children[i], null);
		}
		return ff.makeAssociativeExpression(tag, newChildren, null);		
	}
}
