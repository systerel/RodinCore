/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
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
 * Basic manual rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class RemoveNegationRewriterImpl extends AutoRewriterImpl {
	
	private final boolean isRewrite; 
	private Predicate rewrittenPredicate;

	/**
	 * Default constructor.
	 */
	public RemoveNegationRewriterImpl(FormulaFactory ff) {
		this(ff, true);
	}
	
	/**
	 * Constructor using the flag <code>isRewrite</code> telling if the rewriter
	 * should give the result of rewriting, or just tell if the rewriting is
	 * possible.
	 */
	public RemoveNegationRewriterImpl(FormulaFactory ff, boolean isRewrite) {
		super(ff);
		this.isRewrite = isRewrite;
	} 
	
	%include {FormulaV2.tom}
	
	public boolean isApplicableOrRewrite(Predicate predicate) {
		
		%match (Predicate predicate) {

			/**
			 * DEF_SPECIAL_NOT_EQUAL
			 * Negation: ¬(S = ∅) == ∃x·x ∈ S
			 */
			Not(Equal(S, EmptySet())) -> {
				if (isRewrite) {
					rewrittenPredicate = new FormulaUnfold(ff).makeExistantial(`S);
				}
				return true;
			}
			
			/**
			 * DEF_SPECIAL_NOT_EQUAL
			 * Negation: ¬(∅ = S) == ∃x·x ∈ S
			 */
			Not(Equal(EmptySet(), S)) -> {
				if (isRewrite) {
					rewrittenPredicate = new FormulaUnfold(ff).makeExistantial(`S);
				}
				return true;
			}
			
			/**
			 * DISTRI_NOT_AND
			 * Negation: ¬(P ∧ ... ∧ Q) == ¬P ⋁ ... ⋁ ¬Q
			 */
			Not(Land(children)) -> {
				if (isRewrite) {
					rewrittenPredicate = new FormulaUnfold(ff).deMorgan(Formula.LOR, `children);
				}
				return true;
			}
			
			/**
			 * DISTRI_NOT_OR
			 * Negation: ¬(P ⋁ ... ⋁ Q) == ¬P ∧ ... ∧ ¬Q
			 */
			Not(Lor(children)) -> {
				if (isRewrite) {
					rewrittenPredicate = new FormulaUnfold(ff).deMorgan(Formula.LAND, `children);
				}
				return true;
			}
			
			/**
			 * DERIV_NOT_IMP
			 * Negation: ¬(P ⇒ Q) == P ∧ ¬Q
			 */
			Not(Limp(P, Q)) -> {
				if (isRewrite) {
					rewrittenPredicate = new FormulaUnfold(ff).negImp(`P, `Q);
				}
				return true;
			}
			
			/**
			 * DERIV_NOT_FORALL
			 * Negation: ¬(∀x·P) == ∃x·¬P
			 */
			Not(ForAll(idents, P)) -> {
				if (isRewrite) {
					rewrittenPredicate = new FormulaUnfold(ff).negQuant(Formula.EXISTS, `idents, `P);
				}
				return true;
			}
			
			/**
			 * DERIV_NOT_EXISTS
			 * Negation: ¬(∃x·P) == ∀x·¬P
			 */
			Not(Exists(idents, P)) -> {
				if (isRewrite) {
					rewrittenPredicate = new FormulaUnfold(ff).negQuant(Formula.FORALL, `idents, `P);
				}
				return true;
			}
	    }
		rewrittenPredicate = predicate;
	    return false;
	}
		
	@ProverRule( { "DEF_SPECIAL_NOT_EQUAL", "DISTRI_NOT_AND", "DISTRI_NOT_OR",
			       "DERIV_NOT_IMP", "DERIV_NOT_FORALL", "DERIV_NOT_EXISTS" })
	@Override
	public Predicate rewrite(UnaryPredicate predicate) {
		Predicate newPredicate = super.rewrite(predicate);
		if (!newPredicate.equals(predicate))
			return newPredicate;
			
	    isApplicableOrRewrite(predicate);
	    return rewrittenPredicate;
	}

}
