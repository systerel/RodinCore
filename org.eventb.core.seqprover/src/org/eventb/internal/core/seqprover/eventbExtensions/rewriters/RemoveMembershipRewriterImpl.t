/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *     Systerel - added DEF_IN_UPTO
 *     Systerel - added DEF_IN_NATURAL, DEF_IN_NATURAL1, DEF_IN_REL
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
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites.Level;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembership.RMLevel;

/**
 * Basic manual rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class RemoveMembershipRewriterImpl extends AutoRewriterImpl {
	
	private static Level[] RM_TO_AUTO_LEVEL = {Level.L0, Level.L1};
	
	private final boolean isRewrite;
	private Predicate rewrittenPredicate;
	private final RMLevel rmLevel;
	
	/**
	 * Default rewriter.
	 */
	public RemoveMembershipRewriterImpl(RMLevel level) {
		this(level, true);
	}
	
	/**
	 * Constructor using the flag <code>isRewrite</code> telling if the rewriter
	 * should give the result of rewriting, or just tell if the rewriting is
	 * possible.
	 */
	public RemoveMembershipRewriterImpl(RMLevel rmLevel, boolean isRewrite) {
		super(RM_TO_AUTO_LEVEL[rmLevel.ordinal()]);
		this.rmLevel = rmLevel;
		this.isRewrite = isRewrite;
	}

	%include {FormulaV2.tom}
	
	public boolean isApplicableOrRewrite(Predicate predicate) {
		FormulaFactory ff = predicate.getFactory();    	
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
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inSetExtention(`E, `members);
	    		}
	    		return true;
	    	}

	    	/**
	    	 * DEF_IN_MAPSTO
             * Set Theory: E ↦ F ∈ S × T == E ∈ S ∧ F ∈ T
	    	 */
	    	In(Mapsto(E, F), Cprod(S, T)) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inMap(`E, `F, `S, `T);
	    		}
	    		return true;
	    	}
	    	
	    	/**
	    	 * DEF_IN_POW
	    	 * Set Theory: E ∈ ℙ(S) == E ⊆ S
	    	 */
	    	In(E, Pow(S)) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inPow(`E, `S);
	    		}
	    		return true;
	    	}
	    	
	    	/**
	    	 * DEF_IN_BUNION
	    	 * Set Theory: E ∈ S ∪ ... ∪ T == E ∈ S ⋁ ... ⋁ E ∈ T
	    	 */
	    	In(E, BUnion(children)) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inAssociative(Formula.LOR, `E, `children);
	    		}
	    		return true;
	    	}
	    	
	    	/**
	    	 * DEF_IN_BINTER
	    	 * Set Theory: E ∈ S ∩ ... ∩ T == E ∈ S ∧ ... ∧ E ∈ T
	    	 */
	    	In(E, BInter(children)) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inAssociative(Formula.LAND, `E, `children);
	    		}
	    		return true;
	    	}
	    	
	    	/**
	    	 * DEF_IN_SETMINUS
	    	 * Set Theory: E ∈ S ∖ T == E ∈ S ∧ ¬(E ∈ T)
	    	 */
	    	In(E, SetMinus(S, T)) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inSetMinus(`E, `S, `T);
	    		}
	    		return true;
	    	}
	    	
	    	/**
	    	 * DEF_IN_KUNION
	    	 * Set Theory: E ∈ union(S) == ∃s·s ∈ S ∧ E ∈ s
	    	 */
	    	In(E, Union(S)) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inGeneralised(Formula.EXISTS, `E, `S);
	    		}
	    		return true;
	    	}
	    	
	    	/**
	    	 * DEF_IN_KINTER
	    	 * Set Theory: E ∈ inter(S) == ∀s·s ∈ S ⇒ E ∈ s
	    	 */
	    	In(E, Inter(S)) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inGeneralised(Formula.FORALL, `E, `S);
	    		}
	    		return true;
	    	}
	    	
	    	/**
	    	 * DEF_IN_QUNION
	    	 * Set Theory: E ∈ (⋃x·P|T) == ∃x·P ∧ E ∈ T
	    	 */
	    	In(E, Qunion(x,P,T)) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inQuantified(Formula.EXISTS, `E, `x, `P, `T);
	    		}
	    		return true;
	    	}

	    	/**
	    	 * DEF_IN_QINTER
	    	 * Set Theory: E ∈ (⋂x·P|T) == ∀x·P ⇒ E ∈ T
	    	 */
	    	In(E, Qinter(x,P,T)) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inQuantified(Formula.FORALL, `E, `x, `P, `T);
	    		}
	    		return true;
	    	}

	    	/**
	    	 * DEF_IN_DOM
	    	 * Set Theory: E ∈ dom(r) == ∃y·E ↦ y ∈ r
	    	 */
	    	In(E, Dom(r)) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inDom(`E, `r);
	    		}
	    		return true;
	    	}

	    	/**
	    	 * DEF_IN_RAN
	    	 * Set Theory: F ∈ ran(r) == ∃x·x ↦ F ∈ r
	    	 */
	    	In(F, Ran(r)) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inRan(`F, `r);
	    		}
	    		return true;
	    	}
	    	
	    	/**
	    	 * DEF_IN_CONVERSE
	    	 * Set Theory: E ↦ F ∈ r∼ == F ↦ E ∈ r
	    	 */
	    	In(Mapsto(E, F), Converse(r)) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inConverse(`E, `F, `r);
	    		}
	    		return true;
	    	}
	    	
	    	/**
	    	 * DEF_IN_DOMRES
	    	 * Set Theory: E ↦ F ∈ S ◁ r == E ∈ S ∧ E ↦ F ∈ r
	    	 */
	    	In(Mapsto(E, F), DomRes(S, r)) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inDomManipulation(true, `E, `F, `S, `r);
	    		}
	    		return true;
	    	}
	    	
	    	/**
	    	 * DEF_IN_DOMSUB
	    	 * Set Theory: E ↦ F ∈ S ⩤ r == E ∉ S ∧ E ↦ F ∈ r
	    	 */
	    	In(Mapsto(E, F), DomSub(S, r)) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inDomManipulation(false, `E, `F, `S, `r);
	    		}
	    		return true;
	    	}
	    	
	    	/**
	    	 * DEF_IN_RANRES
	    	 * Set Theory: E ↦ F ∈ r ▷ T  == E ↦ F ∈ r ∧ F ∈ T
	    	 */
	    	In(Mapsto(E, F), RanRes(r, T)) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inRanManipulation(true, `E, `F, `r, `T);
	    		}
	    		return true;
	    	}

	    	/**
	    	 * DEF_IN_RANSUB
	    	 * Set Theory: E ↦ F ∈ r ⩥ T  == E ↦ F ∈ r ∧ F ∉ T
	    	 */
	    	In(Mapsto(E, F), RanSub(r, T)) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inRanManipulation(false, `E, `F, `r, `T);
	    		}
	    		return true;
	    	}
	    	
	    	/**
	    	 * DEF_IN_REL
	    	 * Set Theory : r ∈  S ↔ T == r ⊆ S × T
	    	 */
	    	In(r, Rel(S, T) ) -> {
	    		if(rmLevel.from(RMLevel.L1)) {
	    			if(isRewrite){
	    				final Expression cprod = 
	    					ff.makeBinaryExpression(
	    						Expression.CPROD,`S,`T, null);
	    				rewrittenPredicate = 
	    					ff.makeRelationalPredicate(
	    						Predicate.SUBSETEQ, `r, cprod, null);
	    			}
	    			return true;
	    		}
	    	}	    	   	
	    	
	    	/**
	    	 * DEF_IN_RELIMAGE
	    	 * Set Theory: F ∈ r[S] == (∃x·x ∈ S ∧ x ↦ F ∈ r)
	    	 */
	    	In(F, RelImage(r, S)) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inRelImage(`F, `r, `S);
	    		}
	    		return true;
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
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inForwardComposition(`E, `F, `children);
	    		}
	    		return true;
	    	}

	    	/**
	    	 * DEF_IN_ID
	    	 * Set Theory: E ↦ F ∈ id == E = F
	    	 */
	    	In(Mapsto(E, F), IdGen()) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = 
	    				ff.makeRelationalPredicate(
	    					Predicate.EQUAL, `E, `F, null);
	    		}
	    		return true;
	    	}
	    	 
	    	/**
	    	 * DEF_IN_RELDOM
	    	 * Set Theory: r ∈ S  T == r ∈ S ↔ T ∧ dom(r) = S
	    	 */
	    	In(r, Trel(S, T)) -> {
	    		if (isRewrite) {
	    			final Expression rel = 
	    				ff.makeBinaryExpression(
	    					Expression.REL, `S, `T, null);
	    		    final Predicate pred1 = 
	    		    	ff.makeRelationalPredicate(Predicate.IN, `r, rel, null);
	    		    final Expression dom = 
	    		    	ff.makeUnaryExpression(Expression.KDOM, `r, null);
	    		    final Predicate pred2 = 
	    		    	ff.makeRelationalPredicate(Predicate.EQUAL, dom, `S, null);
	    		    rewrittenPredicate = 
	    		    	ff.makeAssociativePredicate(
	    		    		Predicate.LAND, new Predicate[]{pred1, pred2}, null);
	    		}
	    		return true;
	    	}

	    	/**
	    	 * DEF_IN_RELRAN
	    	 * Set Theory: r ∈ S  T == r ∈ S ↔ T ∧ ran(r) = T
	    	 */
	    	In(r, Srel(S, T)) -> {
	    		if (isRewrite) {
	    			final Expression rel = 
	    				ff.makeBinaryExpression(Expression.REL, `S, `T, null);
	    			final Predicate pred1 = 
	    				ff.makeRelationalPredicate(Predicate.IN, `r, rel, null);
	    			final Expression ran = 
	    				ff.makeUnaryExpression(Expression.KRAN, `r, null);
	    			final Predicate pred2 = 
	    				ff.makeRelationalPredicate(Predicate.EQUAL, ran, `T, null);
	    			rewrittenPredicate = 
	    				ff.makeAssociativePredicate(
	    					Predicate.LAND, new Predicate[]{pred1, pred2}, null);
	    		}
	    		return true;
	    	}

	    	/**
	    	 * DEF_IN_RELDOMRAN
	    	 * Set Theory: r ∈ S  T == r ∈ S ↔ T ∧ dom(r) = S & ran(r) = T
	    	 */
	    	In(r, Strel(S, T)) -> {
	    		if (isRewrite) {
	    			final Expression rel = 
	    				ff.makeBinaryExpression(Expression.REL, `S, `T, null);
	    			final Predicate pred1 = 
	    				ff.makeRelationalPredicate(Predicate.IN, `r, rel, null);
	    			final Expression dom = 
	    				ff.makeUnaryExpression(Expression.KDOM, `r, null);
	    			final Predicate pred2 = 
	    				ff.makeRelationalPredicate(Predicate.EQUAL, dom, `S, null);
	    			final Expression ran = 
	    				ff.makeUnaryExpression(Expression.KRAN, `r, null);
	    			final Predicate pred3 = 
	    				ff.makeRelationalPredicate(Predicate.EQUAL, ran, `T, null);
	    			rewrittenPredicate = 
	    				ff.makeAssociativePredicate(
	    					Predicate.LAND, new Predicate[]{pred1, pred2, pred3}, null);
	    		}
	    		return true;
	    	}

	    	/**
	    	 * DEF_IN_FCT
	    	 * Set Theory: f ∈ S ⇸ T == f ∈ S ↔ T ∧ ∀x,y,z·x ↦ y ∈ f ∧ x ↦ z ∈ f ⇒ y = z
	    	 */
	    	In(f, Pfun(S, T)) -> {
	    		if (isRewrite) {
	    			rewrittenPredicate = new FormulaUnfold(ff).inPfun(`f, `S, `T);
	    		}
	    		return true;
	    	}

	    	/**
	    	 * DEF_IN_TFCT
	    	 * Set Theory: f ∈ S → T == f ∈ S ⇸ T ∧ dom(f) = S
	    	 */
	    	In(f, Tfun(S, T)) -> {
	    		if (isRewrite) {
	    			final Expression pfun = 
	    				ff.makeBinaryExpression(Expression.PFUN, `S, `T, null);
	    			final Predicate pred1 = 
	    				ff.makeRelationalPredicate(Predicate.IN, `f, pfun, null);
	    			final Expression dom = 
	    				ff.makeUnaryExpression(Expression.KDOM, `f, null);
	    			final Predicate pred2 = 
	    				ff.makeRelationalPredicate(Predicate.EQUAL, dom, `S, null);
	    			rewrittenPredicate = 
	    				ff.makeAssociativePredicate(
	    					Predicate.LAND, new Predicate[]{pred1, pred2}, null);
	    		}
	    		return true;
	    	}

	    	/**
	    	 * DEF_IN_INJ
	    	 * Set Theory: f ∈ S ⤔ T == f ∈ S ⇸ T ∧ f∼ ∈ T ⇸ S
	    	 */
	    	In(f, Pinj(S, T)) -> {
	    		if (isRewrite) {
	    			final Expression pfun1 = 
	    				ff.makeBinaryExpression(Expression.PFUN, `S, `T, null);
	    			final Predicate pred1 = 
	    				ff.makeRelationalPredicate(Predicate.IN, `f, pfun1, null);
	    			final Expression inv = 
	    				ff.makeUnaryExpression(Expression.CONVERSE, `f, null);
	    			final Expression pfun2 = 
	    				ff.makeBinaryExpression(Expression.PFUN, `T, `S, null);
	    			final Predicate pred2 = 
	    				ff.makeRelationalPredicate(Predicate.IN, inv, pfun2, null);
	    			rewrittenPredicate = 
	    				ff.makeAssociativePredicate(
	    					Predicate.LAND, new Predicate[]{pred1, pred2}, null);
	    		}
	    		return true;
	    	}

	    	/**
	    	 * DEF_IN_TINJ
	    	 * Set Theory: f ∈ S ↣ T == f ∈ S ⤔ T ∧ dom(f) = S
	    	 */
	    	In(f, Tinj(S, T)) -> {
	    		if (isRewrite) {
	    			final Expression pfun = 
	    				ff.makeBinaryExpression(Expression.PINJ, `S, `T, null);
	    			final Predicate pred1 = 
	    				ff.makeRelationalPredicate(Predicate.IN, `f, pfun, null);
	    			final Expression dom = 
	    				ff.makeUnaryExpression(Expression.KDOM, `f, null);
	    			final Predicate pred2 = 
	    				ff.makeRelationalPredicate(Predicate.EQUAL, dom, `S, null);
	    			rewrittenPredicate = 
	    				ff.makeAssociativePredicate(
	    					Predicate.LAND, new Predicate[]{pred1, pred2}, null);
	    		}
	    		return true;
	    	}

	    	/**
	    	 * DEF_IN_SURJ
	    	 * Set Theory: f ∈ S ⤀ T == f ∈ S ⇸ T ∧ ran(f) = T
	    	 */
	    	In(f, Psur(S, T)) -> {
	    		if (isRewrite) {
	    			final Expression pfun = 
	    				ff.makeBinaryExpression(Expression.PFUN, `S, `T, null);
	    			final Predicate pred1 = 
	    				ff.makeRelationalPredicate(Predicate.IN, `f, pfun, null);
	    			final Expression ran = 
	    				ff.makeUnaryExpression(Expression.KRAN, `f, null);
	    			final Predicate pred2 = 
	    				ff.makeRelationalPredicate(Predicate.EQUAL, ran, `T, null);
	    			rewrittenPredicate = 
	    				ff.makeAssociativePredicate(
	    					Predicate.LAND, new Predicate[]{pred1, pred2}, null);
	    		}
	    		return true;
	    	}

	    	/**
	    	 * DEF_IN_TSURJ
	    	 * Set Theory: f ∈ S ↠ T == f ∈ S ⤀ T ∧ dom(f) = S
	    	 */
	    	In(f, Tsur(S, T)) -> {
	    		if (isRewrite) {
	    			final Expression pfun = 
	    				ff.makeBinaryExpression(Expression.PSUR, `S, `T, null);
	    			final Predicate pred1 = 
	    				ff.makeRelationalPredicate(Predicate.IN, `f, pfun, null);
	    			final Expression dom = 
	    				ff.makeUnaryExpression(Expression.KDOM, `f, null);
	    			final Predicate pred2 = 
	    				ff.makeRelationalPredicate(Predicate.EQUAL, dom, `S, null);
	    			rewrittenPredicate = 
	    				ff.makeAssociativePredicate(
	    					Predicate.LAND, new Predicate[]{pred1, pred2}, null);
	    		}
	    		return true;
	    	}

	    	/**
	    	 * DEF_IN_BIJ
	    	 * Set Theory: f ∈ S ⤖ T == f ∈ S ↣ T ∧ ran(f) = T
	    	 */
	    	In(f, Tbij(S, T)) -> {
	    		if (isRewrite) {
	    			final Expression pfun1 = 
	    				ff.makeBinaryExpression(Expression.TINJ, `S, `T, null);
	    			final Predicate pred1 = 
	    				ff.makeRelationalPredicate(Predicate.IN, `f, pfun1, null);
	    			final Expression ran = 
	    				ff.makeUnaryExpression(Expression.KRAN, `f, null);
	    			final Predicate pred2 = 
	    				ff.makeRelationalPredicate(Predicate.EQUAL, ran, `T, null);
	    			rewrittenPredicate = 
	    				ff.makeAssociativePredicate(
	    					Predicate.LAND, new Predicate[]{pred1, pred2}, null);
	    		}
	    		return true;
	    	}

	    	/**
	    	 * DEF_IN_DPROD
	    	 * Set Theory: E ↦ (F ↦ G) ∈ p ⊗ q == E ↦ F ∈ p ∧ E ↦ G ∈ q
	    	 */
	    	In(Mapsto(E, Mapsto(F, G)), Dprod(p, q)) -> {
	    		if (isRewrite) {
	    			final Expression eMapstoF = 
	    				ff.makeBinaryExpression(Expression.MAPSTO, `E, `F, null);
	    			final Expression eMapstoG = 
	    				ff.makeBinaryExpression(Expression.MAPSTO, `E, `G, null);
	    			final Predicate pred1 = 
	    				ff.makeRelationalPredicate(Predicate.IN, eMapstoF, `p, null);
	    			final Predicate pred2 = 
	    				ff.makeRelationalPredicate(Predicate.IN, eMapstoG, `q, null);
	    			rewrittenPredicate = 
	    				ff.makeAssociativePredicate(
	    					Predicate.LAND, new Predicate[]{pred1, pred2}, null);
	    		}
	    		return true;
	    	}
		
	    	/**
	    	 * DEF_IN_PPROD
	    	 * Set Theory: E ↦ G ↦ (F ↦ H) ∈ p ∥ q == E ↦ F ∈ p ∧ G ↦ H ∈ q
	    	 */
	    	In(Mapsto(Mapsto(E, G), Mapsto(F, H)), Pprod(p, q)) -> {
	    		if (isRewrite) {
	    			final Expression eMapstoF = 
	    				ff.makeBinaryExpression(Expression.MAPSTO, `E, `F, null);
	    			final Expression gMapstoH = 
	    				ff.makeBinaryExpression(Expression.MAPSTO, `G, `H, null);
	    			final Predicate pred1 = 
	    				ff.makeRelationalPredicate(Predicate.IN, eMapstoF, `p, null);
	    			final Predicate pred2 = 
	    				ff.makeRelationalPredicate(Predicate.IN, gMapstoH, `q, null);
	    			rewrittenPredicate = 
	    				ff.makeAssociativePredicate(
	    					Predicate.LAND, new Predicate[]{pred1, pred2}, null);
	    		}
	    		return true;
	    	}
		
			/**
	    	 * DEF_IN_POW1
	    	 * Set Theory: S ∈ ℙ1(T)  == S ∈ ℙ(T) ∧ S ≠ ∅
	    	 */
	    	In(S, Pow1(T)) -> {
	    		if (isRewrite) {
	    			final Expression powT = 
	    				ff.makeUnaryExpression(Expression.POW, `T, null);
	    			final Predicate pred1 = 
	    				ff.makeRelationalPredicate(Predicate.IN, `S, `powT, null);
	    			final Predicate pred2 = 
	    				ff.makeRelationalPredicate(
	    					Predicate.NOTEQUAL, 
	    					`S, 
	    					ff.makeEmptySet(`S.getType(), null), 
	    				null);
	    			rewrittenPredicate = 
	    				ff.makeAssociativePredicate(
	    					Predicate.LAND, new Predicate[]{pred1, pred2}, null);
	    		}
	    		return true;
	    	}
	    	
	    	/**
             * DEF_IN_UPTO
             * Set Theory: E ∈ a‥b == a ≤ E ∧ E ≤ b
             */
            In(E, UpTo(a, b))->{
            	if (isRewrite) {
            		final Predicate pred1 = 
	    				ff.makeRelationalPredicate(Formula.LE, `a, `E, null);
            		final Predicate pred2 = 
	    				ff.makeRelationalPredicate(Formula.LE, `E, `b, null);
            		rewrittenPredicate = 
            			ff.makeAssociativePredicate(
	    					Predicate.LAND, new Predicate[]{pred1, pred2}, null);				
            	}
            	return true;
			}
            
	    	/**
             * DEF_IN_NATURAL
             * Set Theory: E ∈ NAT == 0 ≤ E
             */
            In(E, Natural())->{
            	if(rmLevel.from(RMLevel.L1)) {
	            	if (isRewrite) {
	            		rewrittenPredicate = 
	            			ff.makeRelationalPredicate(Formula.LE, 
	            				ff.makeIntegerLiteral(BigInteger.ZERO, null), 
	            				`E , null);				
	            	}
	            	return true;
            	}
			}
            
	    	/**
             * DEF_IN_NATURAL1
             * Set Theory: E ∈ NAT == 1 ≤ E
             */
            In(E, Natural1())->{
               	if(rmLevel.from(RMLevel.L1)) {
               		if (isRewrite) {
               			rewrittenPredicate = 
	    					ff.makeRelationalPredicate(
	    						Formula.LE, 
               					ff.makeIntegerLiteral(BigInteger.ONE, null), 
               					`E , 
               					null);				
               		}
               		return true;
               	}
			}
	    } // end of %match
    	rewrittenPredicate = predicate;
    	return false;
	}
	
	@ProverRule( { "SIMP_MULTI_IN", "SIMP_IN_SING", "DEF_IN_SETENUM",
		"DEF_IN_MAPSTO", "DEF_IN_POW", "DEF_IN_BUNION", "DEF_IN_BINTER",
		"DEF_IN_SETMINUS", "DEF_IN_KUNION", "DEF_IN_KINTER",
		"DEF_IN_QUNION", "DEF_IN_QINTER", "DEF_IN_DOM", "DEF_IN_RAN",
		"DEF_IN_CONVERSE", "DEF_IN_DOMRES", "DEF_IN_DOMSUB",
		"DEF_IN_RANRES", "DEF_IN_RANSUB", "DEF_IN_REL","DEF_IN_RELIMAGE",
		"DEF_IN_FCOMP", "DEF_IN_ID", "DEF_IN_RELDOM", "DEF_IN_RELRAN",
		"DEF_IN_RELDOMRAN", "DEF_IN_FCT", "DEF_IN_TFCT", "DEF_IN_INJ",
		"DEF_IN_TINJ", "DEF_IN_SURJ", "DEF_IN_TSURJ", "DEF_IN_BIJ",
		"DEF_IN_DPROD", "DEF_IN_PPROD", "DEF_IN_POW1", "DEF_IN_UPTO",
		"DEF_IN_NATURAL", "DEF_IN_NATURAL1"})
	@Override
	public Predicate rewrite(RelationalPredicate predicate) {
		final Predicate newPredicate = super.rewrite(predicate);
		if (!newPredicate.equals(predicate))
			return newPredicate;

		isApplicableOrRewrite(predicate);
		return rewrittenPredicate;
	}

}
