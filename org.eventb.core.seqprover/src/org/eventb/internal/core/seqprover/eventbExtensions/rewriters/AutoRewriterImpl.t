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


	public Predicate makeUnaryPredicate(int tag, Predicate child) {
		return ff.makeUnaryPredicate(tag, child, null);
	}

	public Predicate makeRelationalPredicate(int tag, Expression left,
			Expression right) {
		return ff.makeRelationalPredicate(tag, left, right, null);
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

				return FormulaSimplification.simplifyAssociativePredicate(predicate, `children, isAnd ? Lib.True : Lib.False,
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
	    	 * Negation 3: ¬¬P == P
	    	 */
			Not(Not(P)) -> {
				return `P;
			}

	    	/**
	    	 * Negation 8: ¬ a ≤ b == a > b
	    	 */
			Not(Le(a, b)) -> {
				return makeRelationalPredicate(Predicate.GT, `a, `b);
			}

	    	/**
	    	 * Negation 9: ¬ a ≥ b == a < b
	    	 */
			Not(Ge(a, b)) -> {
				return makeRelationalPredicate(Predicate.LT, `a, `b);
			}

	    	/**
	    	 * Negation 10: ¬ a > b == a ≤ b
	    	 */
			Not(Gt(a, b)) -> {
				return makeRelationalPredicate(Predicate.LE, `a, `b);
			}

	    	/**
	    	 * Negation 11: ¬ a < b == a ≥ b
	    	 */
			Not(Lt(a, b)) -> {
				return makeRelationalPredicate(Predicate.GE, `a, `b);
			}

	    	/**
	    	 * Negation 12: ¬ (E = FALSE) == E = TRUE
	    	 */
			Not(Equal(E, FALSE())) -> {
				return makeRelationalPredicate(Predicate.EQUAL, `E, Lib.TRUE);
			}

	    	/**
	    	 * Negation 13: ¬ (E = TRUE) == E = FALSE
	    	 */
			Not(Equal(E, TRUE())) -> {
				return makeRelationalPredicate(Predicate.EQUAL, `E, Lib.FALSE);
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
	    	
	    	/**
	    	 * Quantification 3: ∀x, ..., y, ..., z·P(y) ∧ ... ∧ y = E ∧ ... ∧ Q(y) ⇒ R(y) 
	    	 *                == ∀x, ..., ...,z·P(E) ∧ ... ∧ ... ∧ Q(E) ⇒ R(E)
	    	 */
	    	ForAll(idents, Limp(Land(children), R)) -> {
	    		return FormulaSimplification.checkForAllOnePointRule(predicate, `idents, `children, `R);
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
	    	 * Negation 4: E ≠ F == ¬ E = F
	    	 */
	    	NotEqual(E, F) -> {
	    		return makeUnaryPredicate(
	    			Predicate.NOT, makeRelationalPredicate(Expression.EQUAL, `E, `F));
	    	}

	    	/**
	    	 * Negation 5: E ∉ F == ¬ E ∈ F
	    	 */
	    	NotIn(E, F) -> {
	    		return makeUnaryPredicate(
	    			Predicate.NOT, makeRelationalPredicate(Expression.IN, `E, `F));
	    	}


	    	/**
	    	 * Negation 6: E ⊄ F == ¬ E ⊂ F
	    	 */
	    	NotSubset(E, F) -> {
	    		return makeUnaryPredicate(
	    			Predicate.NOT, makeRelationalPredicate(Expression.SUBSET, `E, `F));
	    	}

	    	/**
	    	 * Negation 7: E ⊈ F == ¬ E ⊆ F
	    	 */
	    	NotSubsetEq(E, F) -> {
	    		return makeUnaryPredicate(
	    			Predicate.NOT, makeRelationalPredicate(Expression.SUBSETEQ, `E, `F));
	    	}

	    	/**
	    	 * Equality 3: E ↦ F = G ↦ H == E = G ∧ F = H
	    	 */
	    	Equal(Mapsto(E, F) , Mapsto(G, H)) -> {
	    		return FormulaSimplification.rewriteMapsto(`E, `F, `G, `H);
	    	}
	    	
	    	/**
	    	 * Set Theory 5: ∅ ⊆ S == ⊤
	    	 */
	    	SubsetEq(EmptySet(), _) -> {
	    		return Lib.True;
	    	}
	    	
	    	/**
	    	 * Set Theory 6: S ⊆ S == ⊤
	    	 */
	    	SubsetEq(S, S) -> {
	    		return Lib.True;
	    	}
			
			/**
	    	 * Set Theory 7: E ∈ ∅ == ⊥
	    	 */
	    	In(_, EmptySet()) -> {
	    		return Lib.False;
	    	}	    	

			/**
	    	 * Set Theory 8: A ∈ {A} == ⊤
	    	 * Set Theory 9: B ∈ {A, ..., B, ..., C} == ⊤
	    	 * Set Theory 16: E ∈ {F} == E = F (if F is a single expression)
	    	 */
	    	In(E, SetExtension(members)) -> {
	    		return FormulaSimplification.simplifySetMember(predicate, `E, `members);
	    	}

			/**
	    	 * Set Theory 10: E ∈ {x | P(x)} == P(E)
	    	 */
	    	In(E, Cset(idents, guard, expression)) -> {
	    		return FormulaSimplification.simplifySetComprehension(predicate, `E, `idents, `guard, `expression);
	    	}
		
			/**
	    	 * Set Theory 17: {E} = {F} == E = F   if E, F is a single expression
	    	 */
	    	Equal(SetExtension(E), SetExtension(F)) -> {
	    		return FormulaSimplification.simplifySetEquality(predicate, `E, `F);
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
	    		return FormulaSimplification.simplifyAssociativeExpression(expression, `children);
	    	}

	    	/**
	    	 * Arithmetic 1: E + ... + 0 + ... + F == E + ... + ... + F
	    	 */
	    	Plus (children) -> {
	    		return FormulaSimplification.simplifyAssociativeExpression(expression, `children);
	    	}

	    	/**
	    	 * Arithmetic 5: E ∗ ... ∗ 1 ∗ ... ∗ F == E ∗ ... ∗ ... ∗ F
	    	 * Arithmetic 6: E ∗ ... ∗ 0 ∗ ... ∗ F == 0
	    	 */
	    	Mul (children) -> {
	    		return FormulaSimplification.simplifyMulArithmetic(expression, `children);
	    	}
	    }
	    return expression;
	}

	@Override
	public Expression rewrite(BinaryExpression expression) {
	    %match (Expression expression) {

			/**
	    	 * Set Theory 11: S ∖ S == ∅
	    	 */
	    	SetMinus(S, S) -> {
	    		return FormulaSimplification.getEmptySetOfType(`S);
	    	}

			/**
	    	 * Set Theory 18: S ∖ ∅ == S
	    	 */
	    	SetMinus(S, T) -> {
	    		return FormulaSimplification.simplifySetSubtraction(expression, `S, `T);
	    	}
	    	
	    	/**
	    	 * Set Theory 15: (f  {E↦ F})(E) == F
	    	 */
	    	FunImage(Ovr(children), E) -> {
	    		return FormulaSimplification.simplifyFunctionOvr(expression, `children, `E);
	    	}

			/**
	    	 * Arithmetic 2: E − 0 == E
	    	 * Arithmetic 3: 0 − E == −E
	    	 */
	    	Minus(E, F) -> {
	    		return FormulaSimplification.simplifyMinusArithmetic(expression, `E, `F);
	    	}

			/**
	    	 * Arithmetic 10: (−E) ÷ (−F) == E ÷ F
	    	 */
	    	Div(UnMinus(E), UnMinus(F)) -> {
	    		return FormulaSimplification.getFaction(`E, `F);
	    	}

			/**
	    	 * Arithmetic 8: E ÷ 1 = E
	    	 * Arithmetic 10: (−E) ÷ (−F) == E ÷ F
	    	 */
	    	Div(UnMinus(E), IntegerLiteral(F)) -> {
	    		return FormulaSimplification.getFaction(`expression, `E, `F);
	    	}

			/**
	    	 * Arithmetic 9: 0 ÷ E = 0
	    	 * Arithmetic 10: (−E) ÷ (−F) == E ÷ F
	    	 */
	    	Div(IntegerLiteral(E), UnMinus(F)) -> {
	    		return FormulaSimplification.getFaction(`expression, `E, `F);
	    	}

			/**
	    	 * Arithmetic 8: E ÷ 1 = E
	    	 * Arithmetic 9: 0 ÷ E = 0
	    	 * Arithmetic 10: (−E) ÷ (−F) == E ÷ F
	    	 */
	    	Div(IntegerLiteral(E), IntegerLiteral(F)) -> {
	    		return FormulaSimplification.getFaction(`expression, `E, `F);
	    	}

			/**
	    	 * Arithmetic 11: E^1 == E
	    	 * Arithmetic 12: E^0 == 1
	    	 */
	    	Expn (E, F) -> {
	    		return FormulaSimplification.simplifyExpnArithmetic(expression, `E, `F);
	    	}
	    }
	    return expression;
	}

	@Override
	public Expression rewrite(UnaryExpression expression) {
	    %match (Expression expression) {

			/**
	    	 * Set Theory 12: r∼∼ == r
	    	 */
	    	Converse(Converse(r)) -> {
	    		return `r;
	    	}

			/**
	    	 * Set Theory 13: dom(x ↦ a, ..., y ↦ b) = {x, ..., y}
	    	 */
	    	Dom(SetExtension(members)) -> {
	    		return FormulaSimplification.getDomain(expression, `members);
	    	}
		
			/**
	    	 * Set Theory 14: ran(x ↦ a, ..., y ↦ b) = {a, ..., b}
	    	 */
	    	Ran(SetExtension(members)) -> {
	    		return FormulaSimplification.getRange(expression, `members);
	    	}

			/**
	    	 * Arithmetic 4: −(−E) = E
	    	 */
	    	UnMinus(UnMinus(E)) -> {
	    		return `E;
	    	}
			
	    }
	    return expression;
	}

}
