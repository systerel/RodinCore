/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
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
@SuppressWarnings("unused")
public class ConvRewriterImpl extends DefaultRewriter {

	public ConvRewriterImpl() {
		super(true, FormulaFactory.getDefault());
	}
		
	%include {FormulaV2.tom}
	
	@ProverRule("DISTRI_CONVERSE_BUNION") 
	@Override
	public Expression rewrite(UnaryExpression expression) {
	    %match (Expression expression) {

			/**
	    	 * Set Theory : (p ∪ ... ∪ q)∼ == p∼ ∪ ... ∪ q∼
	    	 */
			Converse(BUnion(children)) -> {
				return makeConverseAssociative(Expression.BUNION, `children);
			}
			
			/**
	    	 * Set Theory : (p ∩ ... ∩ q)∼ == p∼ ∩ ... ∩ q∼
	    	 */
			Converse(BInter(children)) -> {
				return makeConverseAssociative(Expression.BINTER, `children);
			}
			
			/**
	    	 * Set Theory : (s ◁ r)∼ == r∼ ▷ s
	    	 */
			Converse(DomRes(s, r)) -> {
				Expression rConverse = ff.makeUnaryExpression(
						Expression.CONVERSE, `r, null);
				return ff.makeBinaryExpression(
						Expression.RANRES, rConverse, `s, null);
			}

			/**
	    	 * Set Theory : (s ⩤ r)∼ == r∼ ⩥ s
	    	 */
			Converse(DomSub(s, r)) -> {
				Expression rConverse = ff.makeUnaryExpression(
						Expression.CONVERSE, `r, null);
				return ff.makeBinaryExpression(
						Expression.RANSUB, rConverse, `s, null);
			}

			/**
	    	 * Set Theory : (r ▷ s)∼ == s ◁ r∼
	    	 */
			Converse(RanRes(r, s)) -> {
				Expression rConverse = ff.makeUnaryExpression(
						Expression.CONVERSE, `r, null);
				return ff.makeBinaryExpression(
						Expression.DOMRES, `s, rConverse, null);
			}

			/**
	    	 * Set Theory : (r ⩥ s)∼ == s ⩤ r∼
	    	 */
			Converse(RanSub(r, s)) -> {
				Expression rConverse = ff.makeUnaryExpression(
						Expression.CONVERSE, `r, null);
				return ff.makeBinaryExpression(
						Expression.DOMSUB, `s, rConverse, null);
			}

			/**
	    	 * Set Theory : (p;...;q)∼ == q∼;...;p∼
	    	 */
			Converse(Fcomp(children)) -> {
				int length = `children.length;
				Expression [] newChildren = new Expression[length];
				for (int i = 0; i < length; ++i) {
					newChildren[i] = ff.makeUnaryExpression(Expression.CONVERSE,
							`children[length - 1 - i], null);
				}
				return ff.makeAssociativeExpression(
						Expression.FCOMP, newChildren, null);
			}
	    }
	    return expression;
	}

	private Expression makeConverseAssociative(int tag, Expression [] children) {
		Expression [] newChildren = new Expression[children.length];
		for (int i = 0; i < children.length; ++i) {
			newChildren[i] = ff.makeUnaryExpression(
					Expression.CONVERSE, children[i], null);
		}
		return ff.makeAssociativeExpression(tag, newChildren, null);		
	}
}
