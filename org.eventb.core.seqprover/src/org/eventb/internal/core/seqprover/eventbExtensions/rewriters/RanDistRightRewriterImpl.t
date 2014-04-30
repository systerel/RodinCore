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
 *     Systerel - fixed rules DISTRI_RANSUB_BUNION_R and DISTRI_RANSUB_BINTER_R
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
public class RanDistRightRewriterImpl extends DefaultRewriter {

	public RanDistRightRewriterImpl() {
		super(true);
	}
		
	%include {FormulaV2.tom}
	
    @ProverRule( { "DISTRI_RANRES_BUNION_R", "DISTRI_RANRES_BINTER_R",
			       "DISTRI_RANSUB_BUNION_R", "DISTRI_RANSUB_BINTER_R" })
	@Override
	public Expression rewrite(BinaryExpression expression) {
	    %match (Expression expression) {

			/**
	    	 * Set Theory : r ▷ (s ∪ ... ∪ t) == (r ▷ s) ∪ ... ∪ (r ▷ t)
	    	 */
			RanRes(r, BUnion(children)) -> {
				return makeRangeAssociative(
						Expression.BUNION, Expression.RANRES, `r, `children);
			}
			
			/**
	    	 * Set Theory : r ▷ (s ∩ ... ∩ t) == (r ▷ s) ∩ ... ∩ (r ▷ t)
	    	 */
			RanRes(r, BInter(children)) -> {
				return makeRangeAssociative(
						Expression.BINTER, Expression.RANRES, `r, `children);
			}
			
			/**
	    	 * Set Theory : r ⩥ (s ∪ ... ∪ t) == (r ⩥ s) ∩ ... ∩ (r ⩥ t)
	    	 */
			RanSub(r, BUnion(children)) -> {
				return makeRangeAssociative(
						Expression.BINTER, Expression.RANSUB, `r, `children);
			}
			
			/**
	    	 * Set Theory : r ⩥ (s ∩ ... ∩ t) == (r ⩥ s) ∪ ... ∪ (r ⩥ t)
	    	 */
			RanSub(r, BInter(children)) -> {
				return makeRangeAssociative(
						Expression.BUNION, Expression.RANSUB, `r, `children);
			}
			
	    }
	    return expression;
	}

	private Expression makeRangeAssociative(int tag, int ranTag, Expression r,
			Expression [] children) {
		FormulaFactory ff = r.getFactory();
		Expression [] newChildren = new Expression[children.length];
		for (int i = 0; i < children.length; ++i) {
			newChildren[i] = ff.makeBinaryExpression(
					ranTag, r, children[i], null);
		}
		return ff.makeAssociativeExpression(tag, newChildren, null);		
	}

}
