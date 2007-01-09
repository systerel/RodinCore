/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.eventbExtensions.Lib;

/**
 * Basic automated rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class AutoRewriterImpl extends DefaultRewriter {

	public AutoRewriterImpl() {
		super(true, FormulaFactory.getDefault());
	}

	%include {Formula.tom}
	
	@Override
	public Predicate rewrite(AssociativePredicate predicate) {
	    %match (Predicate predicate) {
	    	/**
	    	 * Conjunction 1: P ∧ ... ∧ ⊤ ∧ ... ∧ Q  == P ∧ ... ∧ Q
	    	 * Conjunction 2: P ∧ ... ∧ ⊥ ∧ ... ∧ Q  == ⊥
	    	 * Disjunction 1: P ⋁ ... ⋁ ⊤ ⋁ ... ⋁ Q  == ⊤
	    	 * Disjunction 2: P ⋁ ... ⋁ ⊥ ⋁ ... ⋁ Q  == P ⋁ ... ⋁ Q
	    	 */
	    	(Land | Lor) (children) -> {
				boolean isAnd = predicate.getTag() == Formula.LAND;

				return FormulaSimplification.simplifiedAssociativePredicate(predicate, `children, isAnd ? Lib.True : Lib.False,
    				isAnd ? Lib.False : Lib.True);
			}
	    }
	    return predicate;
	}

	@Override
	public Predicate rewrite(BinaryPredicate predicate) {
	    %match (Predicate predicate) {
	    	/**
	    	 * Implication 1: ⊤ ⇒ P == P
	    	 */
	    	Limp(BTRUE(), P) -> {
	    		return `P;
	    	}

	    	/**
	    	 * Implication 2: ⊥ ⇒ P == ⊤
	    	 */
	    	Limp(BFALSE(), _) -> {
	    		return Lib.True;
	    	}

	    	/**
	    	 * Implication 3: P ⇒ ⊤ == ⊤
	    	 */
	    	Limp(_, BTRUE()) -> {
	    		return predicate.getRight();
	    	}
	    	
	    	/**
	    	 * Implication 4: P ⇒ ⊥ == ¬P
	    	 */
	    	Limp(P, BFALSE()) -> {
	    		return Lib.makeNeg(`P);
	    	}

	    	/**
	    	 * Implication 5: P ⇒ P == ⊤
	    	 */
	    	Limp(P, P) -> {
	    		return Lib.True;
	    	}

	    	/**
	    	 * Equivalent 1: P ⇔ ⊤ == P
	    	 */
	    	Leqv(P, BTRUE()) -> {
	    		return `P;
	    	}

	    	/**
	    	 * Equivalent 2: ⊤ ⇔ P = P
	    	 */
	    	Leqv(BTRUE(), P) -> {
	    		return `P;
	    	}

	    	/**
	    	 * Equivalent 3: P ⇔ ⊥ = ¬P
	    	 */
	    	Leqv(P, BFALSE()) -> {
	    		return Lib.makeNeg(`P);
	    	}

	    	/**
	    	 * Equivalent 4: ⊥ ⇔ P == ¬P
	    	 */
	    	Leqv(BFALSE(), P) -> {
	    		return Lib.makeNeg(`P);
	    	}

	    	/**
	    	 * Equivalent 5: P ⇔ P == ⊤
	    	 */
	    	Leqv(P, P) -> {
	    		return Lib.True;
	    	}
	    }
	    return predicate;
	}

	@Override
	public Predicate rewrite(UnaryPredicate predicate) {
	    %match (Predicate predicate) {

	    	/**
	    	 * Negation 1: ¬⊤ == ⊥
	    	 */
	    	Not(BTRUE()) -> {
				return Lib.False;
			}

	    	/**
	    	 * Negation 2: ¬⊥ == ⊤
	    	 */
			Not(BFALSE()) -> {
				return Lib.True;
			}

	    	/**
	    	 * Negation 2: ¬¬P == P
	    	 */
			Not(Not(P)) -> {
				return `P;
			}
	    }
	    return predicate;
	}

	@Override
	public Predicate rewrite(QuantifiedPredicate predicate) {
	    %match (Predicate predicate) {

	    	/**
	    	 * Quantification 1: ∀x·(P ∧ ... ∧ Q) == (∀x·P) ∧ ... ∧ ∀(x·Q)
	    	 */
	    	ForAll(idents, Land(children)) -> {
	    		return FormulaSimplification.splitQuantifiedPredicate(predicate.getTag(), predicate.getPredicate().getTag(), `idents, `children);
	    	}

	    	/**
	    	 * Quantification 2: ∃x·(P ⋁ ... ⋁ Q) == (∃x·P) ⋁ ... ⋁ ∃(x·Q)
	    	 */
			Exists(idents, Lor(children)) -> {
	    		return FormulaSimplification.splitQuantifiedPredicate(predicate.getTag(), predicate.getPredicate().getTag(), `idents, `children);
	    	}
	    }
	    return predicate;
	}
	
	@Override
	public Predicate rewrite(RelationalPredicate predicate) {
	    %match (Predicate predicate) {

	    	/**
	    	 * Equality 1: E = E == ⊤
	    	 */
	    	Equal(E, E) -> {
	    		return Lib.True;
	    	}

	    	/**
	    	 * Equality 2: E ≠ E == ⊥
	    	 */
	    	NotEqual(E, E) -> {
	    		return Lib.False;
	    	}

	    	/**
	    	 * Equality 3: E ↦ F = G ↦ H == E = G ∧ F = H
	    	 */
	    	Equal(Mapsto(E, F) , Mapsto(G, H)) -> {
	    		return FormulaSimplification.rewriteMapsto(`E, `F, `G, `H);
	    	}
	    }
	    return predicate;
	}
	
	@Override
	public Expression rewrite(AssociativeExpression expression) {
	    %match (Expression expression) {

	    	/**
	    	 * Set Theory 1: S ∩ ... ∩ ∅ ∩ ... ∩ T == ∅
	    	 * Set Theory 2: S ∪ ... ∪ ∅ ∪ ... ∪ T == S ∪ ... ∪ T
	    	 */
	    	(BInter | BUnion) (children) -> {
	    		return FormulaSimplification.simplifiedAssociativeExpression(expression, `children);
	    	}

	    }
	    return expression;
	}
}
