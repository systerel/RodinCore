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
 * Basic manual rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class RemoveMembershipRewriterImpl extends AutoRewriterImpl {

	public RemoveMembershipRewriterImpl() {
		super();
	}

	private RelationalPredicate makeRelationalPredicate(int tag, Expression left,
			Expression right) {
		return ff.makeRelationalPredicate(tag, left, right, null);
	}
	
	private AssociativePredicate makeAssociativePredicate(int tag, Predicate ... children) {
		return ff.makeAssociativePredicate(tag, children, null);
	}
	
	private BinaryExpression makeBinaryExpression(int tag, Expression left,
			Expression right) {
		return ff.makeBinaryExpression(tag, left, right, null);
	}

	private UnaryExpression makeUnaryExpression(int tag, Expression child) {
		return ff.makeUnaryExpression(tag, child, null);
	}

	%include {Formula.tom}

	@Override
	public Predicate rewrite(RelationalPredicate predicate) {
		Predicate newPredicate = super.rewrite(predicate);
		if (!newPredicate.equals(predicate))
			return newPredicate;

	    %match (Predicate predicate) {

			/**
	    	 * Set Theory: A ∈ {A} == ⊤
	    	 * Set Theory: B ∈ {A, ..., B, ..., C} == ⊤
	    	 * Set Theory: E ∈ {F} == E = F (if F is a single expression)
	    	 * Set Theory: E ∈ {A, ..., B} == E = A ⋁ ... ⋁ E = B
	    	 */
	    	In(E, SetExtension(members)) -> {
	    		return FormulaUnfold.inSetExtention(`E, `members);
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
	    	 * Set Theory: F ∈ r[S] == (∃x·x ∈ S ∧ x ↦ F ∈ r)
	    	 */
	    	In(F, RelImage(r, S)) -> {
	    		return FormulaUnfold.inRelImage(`F, `r, `S);
	    	}
	    	 
	    	/**
	    	 * Set Theory: E ↦ F ∈ p_1; p_2; ...; p_n ==
	    	 *    ∃x_1, x_2, x_(n−1)· E ↦ x_1 ∈ p_1 ∧
	    	 *                        x_1 ↦ x_2 ∈ p_2 ∧
	    	 *                        ... ∧
	    	 *                        x_(n−1) ↦ F ∈ p_n
	    	 */
	    	In(Mapsto(E, F), Fcomp(children)) -> {
				return FormulaUnfold.inForwardComposition(`E, `F, `children);
	    	}

	    	/**
	    	 * Set Theory: E ↦ F ∈ id(S) == E ∈ S ∧ F = E
	    	 */
	    	In(Mapsto(E, F), Id(S)) -> {
	    		Predicate pred1 = makeRelationalPredicate(Predicate.IN, `E, `S);
	    		Predicate pred2 = makeRelationalPredicate(Predicate.EQUAL, `F, `E);
	    		return makeAssociativePredicate(Predicate.LAND, pred1, pred2);
	    	}
	    	 
	    	/**
	    	 * Set Theory: E ↦ F ∈ p  ... q  r == E ↦ F ∈ ((dom(r) ⩤ (p ...  q)) ∪ r)
	    	 */

	    	/**
	    	 * Set Theory: r ∈ S  T == r ∈ S ↔ T ∧ dom(r) = S
	    	 */
	    	In(r, Trel(S, T)) -> {
	    		Expression rel = makeBinaryExpression(Expression.REL, `S, `T);
	    		Predicate pred1 = makeRelationalPredicate(Predicate.IN, `r, rel);
	    		Expression dom = makeUnaryExpression(Expression.KDOM, `r);
	    		Predicate pred2 = makeRelationalPredicate(Predicate.EQUAL, dom, `S);
	    		return makeAssociativePredicate(Predicate.LAND, pred1, pred2);
	    	}

	    	/**
	    	 * Set Theory: r ∈ S  T == r ∈ S ↔ T ∧ ran(r) = T
	    	 */
	    	In(r, Srel(S, T)) -> {
	    		Expression rel = makeBinaryExpression(Expression.REL, `S, `T);
	    		Predicate pred1 = makeRelationalPredicate(Predicate.IN, `r, rel);
	    		Expression dom = makeUnaryExpression(Expression.KRAN, `r);
	    		Predicate pred2 = makeRelationalPredicate(Predicate.EQUAL, dom, `T);
	    		return makeAssociativePredicate(Predicate.LAND, pred1, pred2);
	    	}

	    	/**
	    	 * Set Theory: r ∈ S  T == r ∈ S  T ∧ r ∈ S  T
	    	 */
	    	In(r, Strel(S, T)) -> {
	    		Expression rel1 = makeBinaryExpression(Expression.SREL, `S, `T);
	    		Predicate pred1 = makeRelationalPredicate(Predicate.IN, `r, rel1);
	    		Expression rel2 = makeBinaryExpression(Expression.TREL, `S, `T);
	    		Predicate pred2 = makeRelationalPredicate(Predicate.IN, `r, rel2);
	    		return makeAssociativePredicate(Predicate.LAND, pred1, pred2);
	    	}

	    	/**
	    	 * Set Theory: f ∈ S ⇸ T == f ∈ S ↔ T ∧ ∀x,y,z·x ↦ y ∈ f ∧ x ↦ z ∈ f ⇒ y = z
	    	 */
	    	In(f, Pfun(S, T)) -> {
	    		return FormulaUnfold.inPfun(`f, `S, `T);
	    	}

	    	/**
	    	 * Set Theory: f ∈ S → T == f ∈ S ⇸ T ∧ dom(f) = S
	    	 */
	    	In(f, Tfun(S, T)) -> {
	    		Expression pfun = makeBinaryExpression(Expression.PFUN, `S, `T);
	    		Predicate pred1 = makeRelationalPredicate(Predicate.IN, `f, pfun);
	    		Expression dom = makeUnaryExpression(Expression.KDOM, `f);
	    		Predicate pred2 = makeRelationalPredicate(Predicate.EQUAL, dom, `S);
	    		return makeAssociativePredicate(Predicate.LAND, pred1, pred2);
	    	}

	    	/**
	    	 * Set Theory: f ∈ S ⤔ T == f ∈ S ⇸ T ∧ f∼ ∈ T ⇸ S
	    	 */
	    	In(f, Pinj(S, T)) -> {
	    		Expression pfun1 = makeBinaryExpression(Expression.PFUN, `S, `T);
	    		Predicate pred1 = makeRelationalPredicate(Predicate.IN, `f, pfun1);
	    		Expression inv = makeUnaryExpression(Expression.CONVERSE, `f);
	    		Expression pfun2 = makeBinaryExpression(Expression.PFUN, `T, `S);
	    		Predicate pred2 = makeRelationalPredicate(Predicate.IN, inv, pfun2);
	    		return makeAssociativePredicate(Predicate.LAND, pred1, pred2);
	    	}

	    	/**
	    	 * Set Theory: f ∈ S ↣ T == f ∈ S ⤔ T ∧ dom(f) = S
	    	 */
	    	In(f, Tinj(S, T)) -> {
	    		Expression pfun = makeBinaryExpression(Expression.PINJ, `S, `T);
	    		Predicate pred1 = makeRelationalPredicate(Predicate.IN, `f, pfun);
	    		Expression dom = makeUnaryExpression(Expression.KDOM, `f);
	    		Predicate pred2 = makeRelationalPredicate(Predicate.EQUAL, dom, `S);
	    		return makeAssociativePredicate(Predicate.LAND, pred1, pred2);
	    	}

	    	/**
	    	 * Set Theory: f ∈ S ⤀ T == f ∈ S ⇸ T ∧ ran(f) = T
	    	 */
	    	In(f, Psur(S, T)) -> {
	    		Expression pfun = makeBinaryExpression(Expression.PFUN, `S, `T);
	    		Predicate pred1 = makeRelationalPredicate(Predicate.IN, `f, pfun);
	    		Expression ran = makeUnaryExpression(Expression.KRAN, `f);
	    		Predicate pred2 = makeRelationalPredicate(Predicate.EQUAL, ran, `T);
	    		return makeAssociativePredicate(Predicate.LAND, pred1, pred2);
	    	}

	    	/**
	    	 * Set Theory: f ∈ S ↠ T == f ∈ S ⤀ T ∧ dom(f) = S
	    	 */
	    	In(f, Tsur(S, T)) -> {
	    		Expression pfun = makeBinaryExpression(Expression.PSUR, `S, `T);
	    		Predicate pred1 = makeRelationalPredicate(Predicate.IN, `f, pfun);
	    		Expression dom = makeUnaryExpression(Expression.KDOM, `f);
	    		Predicate pred2 = makeRelationalPredicate(Predicate.EQUAL, dom, `S);
	    		return makeAssociativePredicate(Predicate.LAND, pred1, pred2);
	    	}

	    	/**
	    	 * Set Theory: f ∈ S ⤖ T == f ∈ S ↣ T ∧ f ∈ S ↠ T
	    	 */
	    	In(f, Tbij(S, T)) -> {
	    		Expression pfun1 = makeBinaryExpression(Expression.TINJ, `S, `T);
	    		Predicate pred1 = makeRelationalPredicate(Predicate.IN, `f, pfun1);
	    		Expression pfun2 = makeBinaryExpression(Expression.TSUR, `S, `T);
	    		Predicate pred2 = makeRelationalPredicate(Predicate.IN, `f, pfun2);
	    		return makeAssociativePredicate(Predicate.LAND, pred1, pred2);
	    	}

	    }
	    return predicate;
	}

}
