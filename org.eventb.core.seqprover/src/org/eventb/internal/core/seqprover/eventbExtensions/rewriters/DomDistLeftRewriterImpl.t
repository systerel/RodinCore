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
 *     Systerel - fixed rules DISTRI_DOMSUB_BUNION_L and DISTRI_DOMSUB_BINTER_L
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
@SuppressWarnings("unused")
public class DomDistLeftRewriterImpl extends DefaultRewriter {

	public DomDistLeftRewriterImpl() {
		super(true);
	}
		
	%include {FormulaV2.tom}
	
	@ProverRule( { "DISTRI_DOMRES_BUNION_L", "DISTRI_DOMRES_BINTER_L",
			"DISTRI_DOMSUB_BUNION_L", "DISTRI_DOMSUB_BINTER_L" })
	@Override
	public Expression rewrite(BinaryExpression expression) {
		final FormulaFactory ff = expression.getFactory();
	    %match (Expression expression) {

			/**
	    	 * Set Theory : (s ∪ ... ∪ t) ◁ r == (s ◁ r) ∪ ... ∪ (t ◁ r)
	    	 */
			DomRes(BUnion(children), r) -> {
				return makeDomainAssociative(
						Expression.BUNION, Expression.DOMRES, `r, `children);
			}
			
			/**
	    	 * Set Theory : (s ∩ ... ∩ t) ◁ r == (s ◁ r) ∩ ... ∩ (t ◁ r)
	    	 */
			DomRes(BInter(children), r) -> {
				return makeDomainAssociative(
						Expression.BINTER, Expression.DOMRES, `r, `children);
			}
			
			/**
	    	 * Set Theory : (s ∪ ... ∪ t) ⩤ r == (s ⩤ r) ∩ ... ∩ (t ⩤ r)
	    	 */
			DomSub(BUnion(children), r) -> {
				return makeDomainAssociative(
						Expression.BINTER, Expression.DOMSUB, `r, `children);
			}
			
			/**
	    	 * Set Theory : (s ∩ ... ∩ t) ⩤ r == (s ⩤ r) ∪ ... ∪ (t ⩤ r)
	    	 */
			DomSub(BInter(children), r) -> {
				return makeDomainAssociative(
						Expression.BUNION, Expression.DOMSUB, `r, `children);
			}
			
	    }
	    return expression;
	}

	private Expression makeDomainAssociative(int tag, int domTag, Expression r,
			Expression [] children) {
		final FormulaFactory ff = r.getFactory();
		Expression [] newChildren = new Expression[children.length];
		for (int i = 0; i < children.length; ++i) {
			newChildren[i] = ff.makeBinaryExpression(
					domTag, children[i], r, null);
		}
		return ff.makeAssociativeExpression(tag, newChildren, null);		
	}
}
