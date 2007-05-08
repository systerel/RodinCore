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
public class ManualRewriterImpl extends AutoRewriterImpl {

	public ManualRewriterImpl() {
		super();
	}

	%include {Formula.tom}
	
	@Override
	public Predicate rewrite(UnaryPredicate predicate) {
		Predicate newPredicate = super.rewrite(predicate);
		if (!newPredicate.equals(predicate))
			return newPredicate;
			
	    %match (Predicate predicate) {

			/**
			 * Negation: ¬(S = ∅) == ∃x·x ∈ S
			 */
			Not(Equal(S, EmptySet())) -> {
				return FormulaUnfold.makeExistantial(`S);
			}
			
			/**
			 * Negation: ¬(∅ = S) == ∃x·x ∈ S
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
	public Predicate rewrite(RelationalPredicate predicate) {
		Predicate newPredicate = super.rewrite(predicate);
		if (!newPredicate.equals(predicate))
			return newPredicate;

	    %match (Predicate predicate) {

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

}
