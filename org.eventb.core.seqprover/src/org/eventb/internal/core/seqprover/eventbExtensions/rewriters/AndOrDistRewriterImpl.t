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
public class AndOrDistRewriterImpl extends DefaultRewriter {

	private AssociativePredicate subPred;

	public AndOrDistRewriterImpl(AssociativePredicate subPred, FormulaFactory ff) {
		super(true, ff);
		this.subPred = subPred;
	}
		
	%include {FormulaV2.tom}
	
	@ProverRule( { "DISTRI_AND_OR", "DISTRI_OR_AND" })
	@Override
	public Predicate rewrite(AssociativePredicate predicate) {
	    %match (Predicate predicate) {

			/**
	    	 * And/Or distribution :
	    	 * P ∨ ... ∨ (Q ∧ ... ∧ R) ∨ ... ∨ S  ==  
	    	 *       (P ∨ ... ∨ Q ∨ ... ∨ S) ∧ ... ∧ (P ∨ ... ∨ R ∨ ... ∨ S)
             *
	    	 * P ∧ ... ∧ (Q ∨ ... ∨ R) ∧ ... ∧ S  ==  
	    	 *       (P ∧ ... ∧ Q ∧ ... ∧ S) ∨ ... ∨ (P ∧ ... ∧ R ∧ ... ∧ S)
	    	 */
			(Lor | Land)(children) -> {
				Collection<Predicate> newChildren = new ArrayList<Predicate>(
						subPred.getChildren().length);
				
				for (Predicate toDistribute : subPred.getChildren()) {
					Predicate [] subChildren = new Predicate[`children.length];
					for (int i = 0; i < `children.length; ++i) {
						if (`children[i] == subPred) {
							subChildren[i] = toDistribute;
						}
						else {
							subChildren[i] = `children[i];
						}
					}
					newChildren.add(ff.makeAssociativePredicate(predicate.getTag(),
							subChildren, null));
				}
				return ff.makeAssociativePredicate(
						subPred.getTag(), newChildren, null);
			}
			
	    }
	    return predicate;
	}

}
