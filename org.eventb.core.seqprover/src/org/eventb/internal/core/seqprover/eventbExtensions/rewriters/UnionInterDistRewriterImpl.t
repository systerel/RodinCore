/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
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
public class UnionInterDistRewriterImpl extends DefaultRewriter {

	private AssociativeExpression subExp;

	public UnionInterDistRewriterImpl(AssociativeExpression subExp, FormulaFactory ff) {
		super(true, ff);
		this.subExp = subExp;
	}
		
	%include {FormulaV2.tom}
	
    @ProverRule({"DISTRI_BUNION_BINTER", "DISTRI_BINTER_BUNION"})
	@Override
	public Expression rewrite(AssociativeExpression expression) {
	    %match (Expression expression) {

			/**
	    	 * Union/Inter distribution :
	    	 * S ∪ ... ∪ (T ∩ ... ∩ U) ∪ ... ∪ V ==  
	    	 *       (S ∪ ... ∪ T ∪ ... ∪ V) ∩ ... ∩ (S ∪ ... ∪ U ∪ ... ∪ V)
             *
	    	 * S ∩ ... ∩ (T ∪ ... ∪ U) ∩ ... ∩ V ==  
	    	 *       (S ∩ ... ∩ T ∩ ... ∩ V) ∪ ... ∪ (S ∩ ... ∩ U ∩ ... ∩ V)
	    	 */
			(BUnion | BInter)(children) -> {
				Collection<Expression> newChildren = new ArrayList<Expression>(
						subExp.getChildren().length);
				
				for (Expression toDistribute : subExp.getChildren()) {
					Expression [] subChildren = new Expression[`children.length];
					for (int i = 0; i < `children.length; ++i) {
						if (`children[i] == subExp) {
							subChildren[i] = toDistribute;
						}
						else {
							subChildren[i] = `children[i];
						}
					}
					newChildren.add(ff.makeAssociativeExpression(expression.getTag(),
							subChildren, null));
				}
				return ff.makeAssociativeExpression(
						subExp.getTag(), newChildren, null);
			}
			
	    }
	    return expression;
	}

}
