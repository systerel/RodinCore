/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
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
public class RemoveMembershipRewriterImpl extends AutoRewriterImpl {

	public RemoveMembershipRewriterImpl() {
		super();
	}

	%include {FormulaV2.tom}

    @ProverRule( { "SIMP_MULTI_IN", "SIMP_IN_SING", "DEF_IN_SETENUM",
			"DEF_IN_MAPSTO", "DEF_IN_POW", "DEF_IN_BUNION", "DEF_IN_BINTER",
			"DEF_IN_SETMINUS", "DEF_IN_KUNION", "DEF_IN_KINTER",
			"DEF_IN_QUNION", "DEF_IN_QINTER", "DEF_IN_DOM", "DEF_IN_RAN",
			"DEF_IN_CONVERSE", "DEF_IN_DOMRES", "DEF_IN_DOMSUB",
			"DEF_IN_RANRES", "DEF_IN_RANSUB", "DEF_IN_RELIMAGE",
			"DEF_IN_FCOMP", "DEF_IN_ID", "DEF_IN_RELDOM", "DEF_IN_RELRAN",
			"DEF_IN_RELDOMRAN", "DEF_IN_FCT", "DEF_IN_TFCT", "DEF_IN_INJ",
			"DEF_IN_TINJ", "DEF_IN_SURJ", "DEF_IN_TSURJ", "DEF_IN_BIJ",
			"DEF_IN_DPROD", "DEF_IN_PPROD", "DEF_IN_POW1", "DEF_IN_UPTO" })
	@Override
	public Predicate rewrite(RelationalPredicate predicate) {
		Predicate newPredicate = super.rewrite(predicate);
		if (!newPredicate.equals(predicate))
			return newPredicate;

	    %match (Predicate predicate) {

			/**
             * SIMP_MULTI_IN
	    	 * Set Theory: A ∈ {A} == ⊤
             * SIMP_MULTI_IN
	    	 * Set Theory: B ∈ {A, ..., B, ..., C} == ⊤
	    	 * SIMP_IN_SING
             * Set Theory: E ∈ {F} == E = F (if F is a single expression)
	    	 * DEF_IN_SETENUM
             * Set Theory: E ∈ {A, ..., B} == E = A ⋁ ... ⋁ E = B
	    	 */
	    	In(E, SetExtension(members)) -> {
	    		return FormulaUnfold.inSetExtention(`E, `members);
	    	}

	    	/**
	    	 * DEF_IN_MAPSTO
             * Set Theory: E ↦ F ∈ S × T == E ∈ S ∧ F ∈ T
	    	 */
	    	In(Mapsto(E, F), Cprod(S, T)) -> {
	    		return FormulaUnfold.inMap(`E, `F, `S, `T);
	    	}
	    	
	    	/**
	    	 * DEF_IN_POW
	    	 * Set Theory: E ∈ ℙ(S) == E ⊆ S
	    	 */
	    	In(E, Pow(S)) -> {
	    		return FormulaUnfold.inPow(`E, `S);
	    	}
	    	
	    	/**
	    	 * DEF_IN_BUNION
	    	 * Set Theory: E ∈ S ∪ ... ∪ T == E ∈ S ⋁ ... ⋁ E ∈ T
	    	 */
	    	In(E, BUnion(children)) -> {
	    		return FormulaUnfold.inAssociative(Formula.LOR, `E, `children);
	    	}
	    	
	    	/**
	    	 * DEF_IN_BINTER
	    	 * Set Theory: E ∈ S ∩ ... ∩ T == E ∈ S ∧ ... ∧ E ∈ T
	    	 */
	    	In(E, BInter(children)) -> {
	    		return FormulaUnfold.inAssociative(Formula.LAND, `E, `children);
	    	}
	    	
	    	/**
	    	 * DEF_IN_SETMINUS
	    	 * Set Theory: E ∈ S ∖ T == E ∈ S ∧ ¬(E ∈ T)
	    	 */
	    	In(E, SetMinus(S, T)) -> {
	    		return FormulaUnfold.inSetMinus(`E, `S, `T);
	    	}
	    	
	    	/**
	    	 * DEF_IN_KUNION
	    	 * Set Theory: E ∈ union(S) == ∃s·s ∈ S ∧ E ∈ s
	    	 */
	    	In(E, Union(S)) -> {
	    		return FormulaUnfold.inGeneralised(Formula.EXISTS, `E, `S);
	    	}
	    	
	    	/**
	    	 * DEF_IN_KINTER
	    	 * Set Theory: E ∈ inter(S) == ∀s·s ∈ S ⇒ E ∈ s
	    	 */
	    	In(E, Inter(S)) -> {
	    		return FormulaUnfold.inGeneralised(Formula.FORALL, `E, `S);
	    	}
	    	
	    	/**
	    	 * DEF_IN_QUNION
	    	 * Set Theory: E ∈ (⋃x·P|T) == ∃x·P ∧ E ∈ T
	    	 */
	    	In(E, Qunion(x,P,T)) -> {
	    		return FormulaUnfold.inQuantified(Formula.EXISTS, `E, `x, `P, `T);
	    	}

	    	/**
	    	 * DEF_IN_QINTER
	    	 * Set Theory: E ∈ (⋂x·P|T) == ∀x·P ⇒ E ∈ T
	    	 */
	    	In(E, Qinter(x,P,T)) -> {
	    		return FormulaUnfold.inQuantified(Formula.FORALL, `E, `x, `P, `T);
	    	}

	    	/**
	    	 * DEF_IN_DOM
	    	 * Set Theory: E ∈ dom(r) == ∃y·E ↦ y ∈ r
	    	 */
	    	In(E, Dom(r)) -> {
	    		return FormulaUnfold.inDom(`E, `r);
	    	}

	    	/**
	    	 * DEF_IN_RAN
	    	 * Set Theory: F ∈ ran(r) == ∃x·x ↦ F ∈ r
	    	 */
	    	In(F, Ran(r)) -> {
	    		return FormulaUnfold.inRan(`F, `r);
	    	}
	    	
	    	/**
	    	 * DEF_IN_CONVERSE
	    	 * Set Theory: E ↦ F ∈ r∼ == F ↦ E ∈ r
	    	 */
	    	In(Mapsto(E, F), Converse(r)) -> {
	    		return FormulaUnfold.inConverse(`E, `F, `r);
	    	}
	    	
	    	/**
	    	 * DEF_IN_DOMRES
	    	 * Set Theory: E ↦ F ∈ S ◁ r == E ∈ S ∧ E ↦ F ∈ r
	    	 */
	    	In(Mapsto(E, F), DomRes(S, r)) -> {
	    		return FormulaUnfold.inDomManipulation(true, `E, `F, `S, `r);
	    	}
	    	
	    	/**
	    	 * DEF_IN_DOMSUB
	    	 * Set Theory: E ↦ F ∈ S ⩤ r == E ∉ S ∧ E ↦ F ∈ r
	    	 */
	    	In(Mapsto(E, F), DomSub(S, r)) -> {
	    		return FormulaUnfold.inDomManipulation(false, `E, `F, `S, `r);
	    	}
	    	
	    	/**
	    	 * DEF_IN_RANRES
	    	 * Set Theory: E ↦ F ∈ r ▷ T  == E ↦ F ∈ r ∧ F ∈ T
	    	 */
	    	In(Mapsto(E, F), RanRes(r, T)) -> {
	    		return FormulaUnfold.inRanManipulation(true, `E, `F, `r, `T);
	    	}

	    	/**
	    	 * DEF_IN_RANSUB
	    	 * Set Theory: E ↦ F ∈ r ⩥ T  == E ↦ F ∈ r ∧ F ∉ T
	    	 */
	    	In(Mapsto(E, F), RanSub(r, T)) -> {
	    		return FormulaUnfold.inRanManipulation(false, `E, `F, `r, `T);
	    	}
	    	
	    	/**
	    	 * DEF_IN_RELIMAGE
	    	 * Set Theory: F ∈ r[S] == (∃x·x ∈ S ∧ x ↦ F ∈ r)
	    	 */
	    	In(F, RelImage(r, S)) -> {
	    		return FormulaUnfold.inRelImage(`F, `r, `S);
	    	}
	    	 
	    	/**
	    	 * DEF_IN_FCOMP
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
	    	 * DEF_IN_ID
	    	 * Set Theory: E ↦ F ∈ id == E = F
	    	 */
	    	In(Mapsto(E, F), IdGen()) -> {
	    		return makeRelationalPredicate(Predicate.EQUAL, `E, `F);
	    	}
	    	 
	    	/**
	    	 * DEF_IN_RELDOM
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
	    	 * DEF_IN_RELRAN
	    	 * Set Theory: r ∈ S  T == r ∈ S ↔ T ∧ ran(r) = T
	    	 */
	    	In(r, Srel(S, T)) -> {
	    		Expression rel = makeBinaryExpression(Expression.REL, `S, `T);
	    		Predicate pred1 = makeRelationalPredicate(Predicate.IN, `r, rel);
	    		Expression ran = makeUnaryExpression(Expression.KRAN, `r);
	    		Predicate pred2 = makeRelationalPredicate(Predicate.EQUAL, ran, `T);
	    		return makeAssociativePredicate(Predicate.LAND, pred1, pred2);
	    	}

	    	/**
	    	 * DEF_IN_RELDOMRAN
	    	 * Set Theory: r ∈ S  T == r ∈ S ↔ T ∧ dom(r) = S & ran(r) = T
	    	 */
	    	In(r, Strel(S, T)) -> {
	    		Expression rel = makeBinaryExpression(Expression.REL, `S, `T);
	    		Predicate pred1 = makeRelationalPredicate(Predicate.IN, `r, rel);
	    		Expression dom = makeUnaryExpression(Expression.KDOM, `r);
	    		Predicate pred2 = makeRelationalPredicate(Predicate.EQUAL, dom, `S);
	    		Expression ran = makeUnaryExpression(Expression.KRAN, `r);
	    		Predicate pred3 = makeRelationalPredicate(Predicate.EQUAL, ran, `T);
	    		return makeAssociativePredicate(Predicate.LAND, pred1, pred2, pred3);
	    	}

	    	/**
	    	 * DEF_IN_FCT
	    	 * Set Theory: f ∈ S ⇸ T == f ∈ S ↔ T ∧ ∀x,y,z·x ↦ y ∈ f ∧ x ↦ z ∈ f ⇒ y = z
	    	 */
	    	In(f, Pfun(S, T)) -> {
	    		return FormulaUnfold.inPfun(`f, `S, `T);
	    	}

	    	/**
	    	 * DEF_IN_TFCT
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
	    	 * DEF_IN_INJ
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
	    	 * DEF_IN_TINJ
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
	    	 * DEF_IN_SURJ
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
	    	 * DEF_IN_TSURJ
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
	    	 * DEF_IN_BIJ
	    	 * Set Theory: f ∈ S ⤖ T == f ∈ S ↣ T ∧ ran(f) = T
	    	 */
	    	In(f, Tbij(S, T)) -> {
	    		Expression pfun1 = makeBinaryExpression(Expression.TINJ, `S, `T);
	    		Predicate pred1 = makeRelationalPredicate(Predicate.IN, `f, pfun1);
	    		Expression ran = makeUnaryExpression(Expression.KRAN, `f);
	    		Predicate pred2 = makeRelationalPredicate(Predicate.EQUAL, ran, `T);
	    		return makeAssociativePredicate(Predicate.LAND, pred1, pred2);
	    	}

	    	/**
	    	 * DEF_IN_DPROD
	    	 * Set Theory: E ↦ (F ↦ G) ∈ p ⊗ q == E ↦ F ∈ p ∧ E ↦ G ∈ q
	    	 */
	    	In(Mapsto(E, Mapsto(F, G)), Dprod(p, q)) -> {
				Expression eMapstoF = makeBinaryExpression(Expression.MAPSTO, `E, `F);
				Expression eMapstoG = makeBinaryExpression(Expression.MAPSTO, `E, `G);
				Predicate pred1 = makeRelationalPredicate(Predicate.IN, eMapstoF, `p);
				Predicate pred2 = makeRelationalPredicate(Predicate.IN, eMapstoG, `q);
				return makeAssociativePredicate(Predicate.LAND, pred1, pred2);
	    	}
		
	    	/**
	    	 * DEF_IN_PPROD
	    	 * Set Theory: E ↦ G ↦ (F ↦ H) ∈ p ∥ q == E ↦ F ∈ p ∧ G ↦ H ∈ q
	    	 */
	    	In(Mapsto(Mapsto(E, G), Mapsto(F, H)), Pprod(p, q)) -> {
				Expression eMapstoF = makeBinaryExpression(Expression.MAPSTO, `E, `F);
				Expression gMapstoH = makeBinaryExpression(Expression.MAPSTO, `G, `H);
				Predicate pred1 = makeRelationalPredicate(Predicate.IN, eMapstoF, `p);
				Predicate pred2 = makeRelationalPredicate(Predicate.IN, gMapstoH, `q);
				return makeAssociativePredicate(Predicate.LAND, pred1, pred2);
	    	}
		
			/**
	    	 * DEF_IN_POW1
	    	 * Set Theory: S ∈ ℙ1(T)  == S ∈ ℙ(T) ∧ S ≠ ∅
	    	 */
	    	In(S, Pow1(T)) -> {
				Expression powT = makeUnaryExpression(Expression.POW, `T);
				Predicate pred1 = makeRelationalPredicate(Predicate.IN, `S, `powT);
				Predicate pred2 = makeRelationalPredicate(Predicate.NOTEQUAL, `S, makeEmptySet(`S.getType()));
				return makeAssociativePredicate(Predicate.LAND, pred1, pred2);
	    	}
	    }
	    return predicate;
	}

}
