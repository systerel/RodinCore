/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
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
public class ManualRewriterImpl extends DefaultRewriter {

	public ManualRewriterImpl() {
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
			 * Negation: ¬(S = ∅) == ∃x·x ∈ S
			 */
			Not(Equal(S, EmptySet())) -> {
				return FormulaUnfold.makeExistantial(`S);
			}
			
			/**
			 * Negation: ¬(S = ∅) == ∃x·x ∈ S
			 */
			Not(Equal(EmptySet(), S)) -> {
				return FormulaUnfold.makeExistantial(`S);
			}
			
			/**
			 * Negation: ¬(P ∧ ... ∧ Q) == ¬P ⋁ ... ⋁ ¬Q
			 */
			Not(Land(children)) -> {
				return FormulaUnfold.deMorgan(Formula.LOR, `children);
			}
			
			/**
			 * Negation: ¬(P ⋁ ... ⋁ Q) == ¬P ∧ ... ∧ ¬Q
			 */
			Not(Lor(children)) -> {
				return FormulaUnfold.deMorgan(Formula.LAND, `children);
			}
			
			/**
			 * Negation: ¬(P ⇒ Q) == P ∧ ¬Q
			 */
			Not(Limp(P, Q)) -> {
				return FormulaUnfold.negImp(`P, `Q);
			}
			
			/**
			 * Negation: ¬(∀x·P) == ∃x·¬P
			 */
			Not(ForAll(idents, P)) -> {
				return FormulaUnfold.negQuant(Formula.EXISTS, `idents, `P);
			}
			
			/**
			 * Negation: ¬(∃x·P) == ∀x·¬P
			 */
			Not(Exists(idents, P)) -> {
				return FormulaUnfold.negQuant(Formula.FORALL, `idents, `P);
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
	    	 * Set Theory: E ∈ {A, ..., B} == E = A ⋁ ... ⋁ E = B
	    	 */
	    	In(E, SetExtension(members)) -> {
	    		return FormulaUnfold.inSetExtention(`E, `members);
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
	    	
	    	/**
	    	 * Set Theory: E ↦ F ∈ S × T == E ∈ S ∧ F ∈ T
	    	 */
	    	In(Mapsto(E, F), Cprod(S, T)) -> {
	    		return FormulaUnfold.inMap(`E, `F, `S, `T);
	    	}
	    	
	    	/**
	    	 * Set Theory: E ∈ ℙ(S) == E ⊆ S
	    	 */
	    	In(E, Pow(S)) -> {
	    		return FormulaUnfold.inPow(`E, `S);
	    	}
	    	
	    	/**
	    	 * Set Theory: E ∈ S ∪ ... ∪ T == E ∈ S ⋁ ... ⋁ E ∈ T
	    	 */
	    	In(E, BUnion(children)) -> {
	    		return FormulaUnfold.inAssociative(Formula.LOR, `E, `children);
	    	}
	    	
	    	/**
	    	 * Set Theory: E ∈ S ∩ ... ∩ T == E ∈ S ∧ ... ∧ E ∈ T
	    	 */
	    	In(E, BInter(children)) -> {
	    		return FormulaUnfold.inAssociative(Formula.LAND, `E, `children);
	    	}
	    	
	    	/**
	    	 * Set Theory: E ∈ S ∖ T == E ∈ S ∧ ¬(E ∈ T)
	    	 */
	    	In(E, SetMinus(S, T)) -> {
	    		return FormulaUnfold.inSetMinus(`E, `S, `T);
	    	}
	    	
	    	/**
	    	 * Set Theory: E ∈ union(S) == ∃s·s ∈ S ∧ E ∈ s
	    	 */
	    	In(E, Union(S)) -> {
	    		return FormulaUnfold.inGeneralised(Formula.EXISTS, `E, `S);
	    	}
	    	
	    	/**
	    	 * Set Theory: E ∈ inter(S) == ∀s·s ∈ S ⇒ E ∈ s
	    	 */
	    	In(E, Inter(S)) -> {
	    		return FormulaUnfold.inGeneralised(Formula.FORALL, `E, `S);
	    	}
	    	
	    	/**
	    	 * Set Theory: E ∈ (⋃x·P|T) == ∃x·P ∧ E ∈ T
	    	 */
	    	In(E, Qunion(x,P,T)) -> {
	    		return FormulaUnfold.inQuantified(Formula.EXISTS, `E, `x, `P, `T);
	    	}

	    	/**
	    	 * Set Theory: E ∈ (⋂x·P|T) == ∀x·P ⇒ E ∈ T
	    	 */
	    	In(E, Qinter(x,P,T)) -> {
	    		return FormulaUnfold.inQuantified(Formula.FORALL, `E, `x, `P, `T);
	    	}

	    	/**
	    	 * Set Theory: E ∈ dom(r) == ∃y·E ↦ y ∈ r
	    	 */
	    	In(E, Dom(r)) -> {
	    		return FormulaUnfold.inDom(`E, `r);
	    	}

	    	/**
	    	 * Set Theory: F ∈ ran(r) == ∃x·x ↦ F ∈ r
	    	 */
	    	In(F, Ran(r)) -> {
	    		return FormulaUnfold.inRan(`F, `r);
	    	}
	    	
	    	/**
	    	 * Set Theory: E ↦ F ∈ r∼ == F ↦ E ∈ r
	    	 */
	    	In(Mapsto(E, F), Converse(r)) -> {
	    		return FormulaUnfold.inConverse(`E, `F, `r);
	    	}
	    	
	    	/**
	    	 * Set Theory: E ↦ F ∈ S ◁ r == E ∈ S ∧ E ↦ F ∈ r
	    	 */
	    	In(Mapsto(E, F), DomRes(S, r)) -> {
	    		return FormulaUnfold.inDomManipulation(true, `E, `F, `S, `r);
	    	}
	    	
	    	/**
	    	 * Set Theory: E ↦ F ∈ S ⩤ r == E ∉ S ∧ E ↦ F ∈ r
	    	 */
	    	In(Mapsto(E, F), DomSub(S, r)) -> {
	    		return FormulaUnfold.inDomManipulation(false, `E, `F, `S, `r);
	    	}
	    	
	    	/**
	    	 * Set Theory: E ↦ F ∈ r ▷ T  == E ↦ F ∈ r ∧ F ∈ T
	    	 */
	    	In(Mapsto(E, F), RanRes(r, T)) -> {
	    		return FormulaUnfold.inRanManipulation(true, `E, `F, `r, `T);
	    	}

	    	/**
	    	 * Set Theory: E ↦ F ∈ r ⩥ T  == E ↦ F ∈ r ∧ F ∉ T
	    	 */
	    	In(Mapsto(E, F), RanSub(r, T)) -> {
	    		return FormulaUnfold.inRanManipulation(false, `E, `F, `r, `T);
	    	}
	    	
	    	/**
	    	 * Set Theory: S ⊆ T == ∀x·x ∈ S ⇒ x ∈ T
	    	 */
	    	SubsetEq(S, T) -> {
	    		return FormulaUnfold.subsetEq(`S, `T);
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
