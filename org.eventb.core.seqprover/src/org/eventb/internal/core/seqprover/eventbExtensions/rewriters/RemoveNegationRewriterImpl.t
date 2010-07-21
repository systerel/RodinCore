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

	public RemoveNegationRewriterImpl(FormulaFactory ff) {
		super(ff);
	}

	%include {FormulaV2.tom}
	
	@ProverRule( { "DEF_SPECIAL_NOT_EQUAL", "DISTRI_NOT_AND", "DISTRI_NOT_OR",
			       "DERIV_NOT_IMP", "DERIV_NOT_FORALL", "DERIV_NOT_EXISTS" })
	@Override
	public Predicate rewrite(UnaryPredicate predicate) {
		Predicate newPredicate = super.rewrite(predicate);
		if (!newPredicate.equals(predicate))
			return newPredicate;
			
	    %match (Predicate predicate) {

			/**
			 * DEF_SPECIAL_NOT_EQUAL
			 * Negation: ¬(S = ∅) == ∃x·x ∈ S
			 */
			Not(Equal(S, EmptySet())) -> {
				return new FormulaUnfold(ff).makeExistantial(`S);
			}
			
			/**
			 * DEF_SPECIAL_NOT_EQUAL
			 * Negation: ¬(∅ = S) == ∃x·x ∈ S
			 */
			Not(Equal(EmptySet(), S)) -> {
				return new FormulaUnfold(ff).makeExistantial(`S);
			}
			
			/**
			 * DISTRI_NOT_AND
			 * Negation: ¬(P ∧ ... ∧ Q) == ¬P ⋁ ... ⋁ ¬Q
			 */
			Not(Land(children)) -> {
				return new FormulaUnfold(ff).deMorgan(Formula.LOR, `children);
			}
			
			/**
			 * DISTRI_NOT_OR
			 * Negation: ¬(P ⋁ ... ⋁ Q) == ¬P ∧ ... ∧ ¬Q
			 */
			Not(Lor(children)) -> {
				return new FormulaUnfold(ff).deMorgan(Formula.LAND, `children);
			}
			
			/**
			 * DERIV_NOT_IMP
			 * Negation: ¬(P ⇒ Q) == P ∧ ¬Q
			 */
			Not(Limp(P, Q)) -> {
				return new FormulaUnfold(ff).negImp(`P, `Q);
			}
			
			/**
			 * DERIV_NOT_FORALL
			 * Negation: ¬(∀x·P) == ∃x·¬P
			 */
			Not(ForAll(idents, P)) -> {
				return new FormulaUnfold(ff).negQuant(Formula.EXISTS, `idents, `P);
			}
			
			/**
			 * DERIV_NOT_EXISTS
			 * Negation: ¬(∃x·P) == ∀x·¬P
			 */
			Not(Exists(idents, P)) -> {
				return new FormulaUnfold(ff).negQuant(Formula.FORALL, `idents, `P);
			}
	    }
	    return predicate;
	}

}
