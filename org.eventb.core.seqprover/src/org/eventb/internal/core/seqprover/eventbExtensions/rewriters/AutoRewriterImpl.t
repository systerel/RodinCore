/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *     Systerel - SIMP_IN_COMPSET_*, SIMP_SPECIAL_OVERL, SIMP_FUNIMAGE_LAMBDA
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

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
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.internal.core.seqprover.eventbExtensions.OnePointSimplifier;

/**
 * Basic automated rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class AutoRewriterImpl extends DefaultRewriter {

	private final IntegerLiteral number0 = ff.makeIntegerLiteral(BigInteger.ZERO, null);
	
	private final IntegerLiteral number1 = ff.makeIntegerLiteral(BigInteger.ONE, null);

	private final IntegerLiteral number2 = ff.makeIntegerLiteral(new BigInteger("2"), null);

	public AutoRewriterImpl() {
		super(true, FormulaFactory.getDefault());
	}

	protected UnaryPredicate makeUnaryPredicate(int tag, Predicate child) {
		return ff.makeUnaryPredicate(tag, child, null);
	}

	protected RelationalPredicate makeRelationalPredicate(int tag, Expression left,
			Expression right) {
		return ff.makeRelationalPredicate(tag, left, right, null);
	}
	
	protected AssociativePredicate makeAssociativePredicate(int tag, Predicate... children) {
		return ff.makeAssociativePredicate(tag, children, null);
	}
	
	protected QuantifiedPredicate makeQuantifiedPredicate(int tag, BoundIdentDecl[] boundIdentifiers, Predicate child) {
		return ff.makeQuantifiedPredicate(tag, boundIdentifiers, child, null);
	}

	protected SetExtension makeSetExtension(Collection<Expression> expressions) {
		return ff.makeSetExtension(expressions, null);
	}
	
	protected UnaryExpression makeUnaryExpression(int tag, Expression child) {
		return ff.makeUnaryExpression(tag, child, null);
	}

	protected BinaryExpression makeBinaryExpression(int tag, Expression left, Expression right) {
		return ff.makeBinaryExpression(tag, left, right, null);
	}

	protected AtomicExpression makeEmptySet(Type type) {
		return ff.makeEmptySet(type, null);
	}
		
	protected AssociativeExpression makeAssociativeExpression(int tag, Expression... children) {
		return ff.makeAssociativeExpression(tag, children, null);
	}

	protected AssociativeExpression makeAssociativeExpression(int tag, Collection<Expression> children) {
		return ff.makeAssociativeExpression(tag, children, null);
	}

	protected SimplePredicate makeSimplePredicate(int tag, Expression expression) {
		return ff.makeSimplePredicate(tag, expression, null);
	}

	%include {FormulaV2.tom}
	
	@ProverRule( { "SIMP_SPECIAL_FINITE", "SIMP_FINITE_SETENUM",
			"SIMP_FINITE_BUNION", "SIMP_FINITE_POW", "DERIV_FINITE_CPROD",
			"SIMP_FINITE_CONVERSE", "SIMP_FINITE_UPTO" })
	@Override
	public Predicate rewrite(SimplePredicate predicate) {
	    %match (Predicate predicate) {

			/**
             * SIMP_SPECIAL_FINITE
	    	 * Finite: finite(∅) = ⊤
	    	 */
			Finite(EmptySet()) -> {
				return Lib.True;
			}

			/**
             * SIMP_FINITE_SETENUM
	    	 * Finite: finite({a, ..., b}) = ⊤
	    	 */
			Finite(SetExtension(_)) -> {
				return Lib.True;
			}

			/**
             * SIMP_FINITE_BUNION
	    	 * Finite: finite(S ∪ ... ∪ ⊤) == finite(S) ∧ ... ∧ finite(T)
	    	 */
			Finite(BUnion(children)) -> {
				Predicate [] newChildren = new Predicate[`children.length];
				for (int i = 0; i < `children.length; ++i) {
					newChildren[i] = makeSimplePredicate(Predicate.KFINITE,
							`children[i]);
				}
				return makeAssociativePredicate(Predicate.LAND, newChildren);
			}

			/**
             * SIMP_FINITE_POW
	    	 * Finite: finite(ℙ(S)) == finite(S)
	    	 */
			Finite(Pow(S)) -> {
				return makeSimplePredicate(Predicate.KFINITE, `S);
			}

			/**
             * DERIV_FINITE_CPROD
	    	 * Finite: finite(S × ⊤) == S = ∅ ∨ T = ∅ ∨ (finite(S) ∧ finite(T))
	    	 */
			Finite(Cprod(S, T)) -> {
				Predicate [] children = new Predicate[3];
				children[0] = makeRelationalPredicate(Predicate.EQUAL, `S,
						makeEmptySet(`S.getType()));
				children[1] = makeRelationalPredicate(Predicate.EQUAL, `T,
						makeEmptySet(`T.getType()));
				Predicate [] subChildren = new Predicate[2];
				subChildren[0] = makeSimplePredicate(Predicate.KFINITE, `S);
				subChildren[1] = makeSimplePredicate(Predicate.KFINITE, `T);
				children[2] = makeAssociativePredicate(Predicate.LAND,
						subChildren);
				return makeAssociativePredicate(Predicate.LOR, children);
			}

			/**
             * SIMP_FINITE_CONVERSE
	    	 * Finite: finite(r∼) == finite(r)
	    	 */
			Finite(Converse(r)) -> {
				return makeSimplePredicate(Predicate.KFINITE, `r);
			}

			/**
             * SIMP_FINITE_UPTO
	    	 * Finite: finite(a‥b) == ⊤
	    	 */
			Finite(UpTo(_,_)) -> {
				return Lib.True;
			}

	    }
	    return predicate;
	}

    @ProverRule( { "SIMP_SPECIAL_AND_BTRUE", "SIMP_SPECIAL_AND_BFALSE",
			"SIMP_SPECIAL_OR_BTRUE", "SIMP_SPECIAL_OR_BFALSE" })
	@Override
	public Predicate rewrite(AssociativePredicate predicate) {
	    %match (Predicate predicate) {
	    	/**
             * SIMP_SPECIAL_AND_BTRUE
	    	 * Conjunction 1: P ∧ ... ∧ ⊤ ∧ ... ∧ Q  == P ∧ ... ∧ Q
             * SIMP_SPECIAL_AND_BFALSE
	    	 * Conjunction 2: P ∧ ... ∧ ⊥ ∧ ... ∧ Q  == ⊥
             * SIMP_SPECIAL_OR_BTRUE
	    	 * Disjunction 1: P ⋁ ... ⋁ ⊤ ⋁ ... ⋁ Q  == ⊤
             * SIMP_SPECIAL_OR_BFALSE
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

    @ProverRule( { "SIMP_SPECIAL_IMP_BTRUE_L", "SIMP_SPECIAL_IMP_BFALSE_L",
			"SIMP_SPECIAL_IMP_BTRUE_R", "SIMP_SPECIAL_IMP_BFALSE_R",
			"SIMP_MULTI_IMP", "SIMP_MULTI_EQV", "SIMP_SPECIAL_EQV_BTRUE",
			"SIMP_SPECIAL_EQV_BFALSE" })
	@Override
	public Predicate rewrite(BinaryPredicate predicate) {
	    %match (Predicate predicate) {
	    	/**
             * SIMP_SPECIAL_IMP_BTRUE_L
	    	 * Implication 1: ⊤ ⇒ P == P
	    	 */
	    	Limp(BTRUE(), P) -> {
	    		return `P;
	    	}

	    	/**
             * SIMP_SPECIAL_IMP_BFALSE_L
	    	 * Implication 2: ⊥ ⇒ P == ⊤
	    	 */
	    	Limp(BFALSE(), _) -> {
	    		return Lib.True;
	    	}

	    	/**
             * SIMP_SPECIAL_IMP_BTRUE_R
	    	 * Implication 3: P ⇒ ⊤ == ⊤
	    	 */
	    	Limp(_, BTRUE()) -> {
	    		return predicate.getRight();
	    	}
	    	
	    	/**
             * SIMP_SPECIAL_IMP_BFALSE_R
	    	 * Implication 4: P ⇒ ⊥ == ¬P
	    	 */
	    	Limp(P, BFALSE()) -> {
	    		return Lib.makeNeg(`P);
	    	}

	    	/**
             * SIMP_MULTI_IMP
	    	 * Implication 5: P ⇒ P == ⊤
	    	 */
	    	Limp(P, P) -> {
	    		return Lib.True;
	    	}

	    	/**
             * SIMP_MULTI_EQV
	    	 * Equivalent 5: P ⇔ P == ⊤
	    	 */
	    	Leqv(P, P) -> {
	    		return Lib.True;
	    	}

	    	/**
             * SIMP_SPECIAL_EQV_BTRUE
	    	 * Equivalent 1: P ⇔ ⊤ == P
	    	 */
	    	Leqv(P, BTRUE()) -> {
	    		return `P;
	    	}

	    	/**
             * SIMP_SPECIAL_EQV_BTRUE
	    	 * Equivalent 2: ⊤ ⇔ P = P
	    	 */
	    	Leqv(BTRUE(), P) -> {
	    		return `P;
	    	}

	    	/**
             * SIMP_SPECIAL_EQV_BFALSE
	    	 * Equivalent 3: P ⇔ ⊥ = ¬P
	    	 */
	    	Leqv(P, BFALSE()) -> {
	    		return Lib.makeNeg(`P);
	    	}

	    	/**
             * SIMP_SPECIAL_EQV_BFALSE
	    	 * Equivalent 4: ⊥ ⇔ P == ¬P
	    	 */
	    	Leqv(BFALSE(), P) -> {
	    		return Lib.makeNeg(`P);
	    	}

	    }
	    return predicate;
	}

    @ProverRule( { "SIMP_SPECIAL_NOT_BTRUE", "SIMP_SPECIAL_NOT_BFALSE",
			"SIMP_NOT_NOT", "SIMP_NOT_LE", "SIMP_NOT_GE", "SIMP_NOT_GT",
			"SIMP_NOT_LT", "SIMP_SPECIAL_NOT_EQUAL_FALSE_R",
			"SIMP_SPECIAL_NOT_EQUAL_TRUE_R", "SIMP_SPECIAL_NOT_EQUAL_FALSE_L",
			"SIMP_SPECIAL_NOT_EQUAL_TRUE_L" })
	@Override
	public Predicate rewrite(UnaryPredicate predicate) {
	    %match (Predicate predicate) {

	    	/**
             * SIMP_SPECIAL_NOT_BTRUE
	    	 * Negation 1: ¬⊤ == ⊥
	    	 */
	    	Not(BTRUE()) -> {
				return Lib.False;
			}

	    	/**
             * SIMP_SPECIAL_NOT_BFALSE
	    	 * Negation 2: ¬⊥ == ⊤
	    	 */
			Not(BFALSE()) -> {
				return Lib.True;
			}

	    	/**
             * SIMP_NOT_NOT
	    	 * Negation 3: ¬¬P == P
	    	 */
			Not(Not(P)) -> {
				return `P;
			}

	    	/**
             * SIMP_NOT_LE
	    	 * Negation 8: ¬ a ≤ b == a > b
	    	 */
			Not(Le(a, b)) -> {
				return makeRelationalPredicate(Predicate.GT, `a, `b);
			}

	    	/**
             * SIMP_NOT_GE
	    	 * Negation 9: ¬ a ≥ b == a < b
	    	 */
			Not(Ge(a, b)) -> {
				return makeRelationalPredicate(Predicate.LT, `a, `b);
			}

	    	/**
             * SIMP_NOT_GT
	    	 * Negation 10: ¬ a > b == a ≤ b
	    	 */
			Not(Gt(a, b)) -> {
				return makeRelationalPredicate(Predicate.LE, `a, `b);
			}

	    	/**
             * SIMP_NOT_LT
	    	 * Negation 11: ¬ a < b == a ≥ b
	    	 */
			Not(Lt(a, b)) -> {
				return makeRelationalPredicate(Predicate.GE, `a, `b);
			}

	    	/**
             * SIMP_SPECIAL_NOT_EQUAL_FALSE_R
	    	 * Negation 12: ¬ (E = FALSE) == E = TRUE
	    	 */
			Not(Equal(E, FALSE())) -> {
				return makeRelationalPredicate(Predicate.EQUAL, `E, Lib.TRUE);
			}

	    	/**
             * SIMP_SPECIAL_NOT_EQUAL_TRUE_R
	    	 * Negation 13: ¬ (E = TRUE) == E = FALSE
	    	 */
			Not(Equal(E, TRUE())) -> {
				return makeRelationalPredicate(Predicate.EQUAL, `E, Lib.FALSE);
			}

	    	/**
             * SIMP_SPECIAL_NOT_EQUAL_FALSE_L
	    	 * Negation 14: ¬ (FALSE = E) == TRUE = E
	    	 */
			Not(Equal(FALSE(), E)) -> {
				return makeRelationalPredicate(Predicate.EQUAL, Lib.TRUE, `E);
			}

	    	/**
             * SIMP_SPECIAL_NOT_EQUAL_TRUE_L
	    	 * Negation 15: ¬ (TRUE = E) == FALSE = E
	    	 */
			Not(Equal(TRUE(), E)) -> {
				return makeRelationalPredicate(Predicate.EQUAL, Lib.FALSE, `E);
			}
			
	    }
	    return predicate;
	}

    @ProverRule({"SIMP_FORALL_AND", "SIMP_EXISTS_OR"}) 
	@Override
	public Predicate rewrite(QuantifiedPredicate predicate) {
	    %match (Predicate predicate) {

	    	/**
             * SIMP_FORALL_AND
	    	 * Quantification 1: ∀x·(P ∧ ... ∧ Q) == (∀x·P) ∧ ... ∧ ∀(x·Q)
	    	 */
	    	ForAll(boundIdentifiers, Land(children)) -> {
	    		Predicate [] predicates = new Predicate[`children.length];
	    		for (int i = 0; i < `children.length; ++i) {
					Predicate qPred = makeQuantifiedPredicate(Predicate.FORALL, `boundIdentifiers, `children[i]);
					predicates[i] = qPred;
				}

				return makeAssociativePredicate(Predicate.LAND, predicates);
	    	}

	    	/**
             * SIMP_EXISTS_OR
	    	 * Quantification 2: ∃x·(P ⋁ ... ⋁ Q) == (∃x·P) ⋁ ... ⋁ ∃(x·Q)
	    	 */
			Exists(boundIdentifiers, Lor(children)) -> {
	    		Predicate [] predicates = new Predicate[`children.length];
	    		for (int i = 0; i < `children.length; ++i) {
					Predicate qPred = makeQuantifiedPredicate(Predicate.EXISTS, `boundIdentifiers, `children[i]);
					predicates[i] = qPred;
				}

				return makeAssociativePredicate(Predicate.LOR, predicates);
	    	}
	    	
	    }
	    return predicate;
	}
	
    @ProverRule( { "SIMP_MULTI_EQUAL", "SIMP_MULTI_NOTEQUAL", "SIMP_MULTI_LE",
			"SIMP_MULTI_GE", "SIMP_MULTI_LT", "SIMP_MULTI_GT",
			"SIMP_EQUAL_MAPSTO", "SIMP_SPECIAL_EQUAL_TRUE", "SIMP_NOTEQUAL",
            "SIMP_NOTIN", "SIMP_NOTSUBSET", "SIMP_NOTSUBSETEQ",
            "SIMP_SPECIAL_SUBSETEQ", "SIMP_MULTI_SUBSETEQ",
            "DERIV_SUBSETEQ_SETMINUS_L", "SIMP_SUBSETEQ_BUNION",
            "SIMP_SUBSETEQ_BINTER", "DERIV_SUBSETEQ_BUNION",
            "DERIV_SUBSETEQ_BINTER", "SIMP_SPECIAL_IN", "SIMP_MULTI_IN",
            "SIMP_IN_SING", "SIMP_IN_COMPSET", "SIMP_IN_COMPSET_ONEPOINT",
            "SIMP_EQUAL_SING", "SIMP_LIT_EQUAL", "SIMP_LIT_LE", "SIMP_LIT_LT",
			"SIMP_LIT_GE", "SIMP_LIT_GT", "SIMP_SPECIAL_EQUAL_CARD",
			"SIMP_LIT_EQUAL_CARD_1", "SIMP_LIT_GT_CARD_0",
			"SIMP_LIT_LT_CARD_0", "SIMP_LIT_EQUAL_KBOOL_TRUE",
			"SIMP_LIT_EQUAL_KBOOL_FALSE" })
    @Override
	public Predicate rewrite(RelationalPredicate predicate) {
	    %match (Predicate predicate) {

	    	/**
             * SIMP_MULTI_EQUAL
	    	 * Equality: E = E == ⊤
	    	 */
	    	Equal(E, E) -> {
	    		return Lib.True;
	    	}

	    	/**
             * SIMP_MULTI_NOTEQUAL
	    	 * Equality: E ≠ E == ⊥
	    	 */
	    	NotEqual(E, E) -> {
	    		return Lib.False;
	    	}

			/**
             * SIMP_MULTI_LE
	    	 * Arithmetic: E ≤ E == ⊤
	    	 */
	    	Le(E, E) -> {
				return Lib.True;
			}
			
	    	/**
             * SIMP_MULTI_GE
	    	 * Arithmetic: E ≥ E == ⊤
	    	 */
	    	Ge(E, E) -> {
				return Lib.True;
			}
			
			/**
             * SIMP_MULTI_LT
	    	 * Arithmetic: E < E == ⊥
	    	 */
	    	Lt(E, E) -> {
				return Lib.False;
			}
			
			/**
             * SIMP_MULTI_GE
	    	 * Arithmetic: E > E == ⊥
	    	 */
	    	Gt(E, E) -> {
				return Lib.False;
			}
			
			/**
             * SIMP_EQUAL_MAPSTO
	    	 * Equality 3: E ↦ F = G ↦ H == E = G ∧ F = H
	    	 */
	    	Equal(Mapsto(E, F) , Mapsto(G, H)) -> {
	    		Predicate pred1 = makeRelationalPredicate(Expression.EQUAL, `E, `G);
				Predicate pred2 = makeRelationalPredicate(Expression.EQUAL, `F, `H);
				return makeAssociativePredicate(Predicate.LAND, new Predicate[] {
						pred1, pred2 });
	    	}
	    	
	    	/**
             * SIMP_SPECIAL_EQUAL_TRUE
	    	 * Equality 4: TRUE = FALSE == ⊥
	    	 */
	    	Equal(TRUE(), FALSE()) -> {
	    		return Lib.False;
	    	}

	    	/**
             * SIMP_SPECIAL_EQUAL_TRUE
	    	 * Equality 5: FALSE = TRUE == ⊥
	    	 */
	    	Equal(FALSE(), TRUE()) -> {
	    		return Lib.False;
	    	}

	    	/**
             * SIMP_NOTEQUAL
	    	 * Negation 4: E ≠ F == ¬ E = F
	    	 */
	    	NotEqual(E, F) -> {
	    		return makeUnaryPredicate(
	    			Predicate.NOT, makeRelationalPredicate(Expression.EQUAL, `E, `F));
	    	}

	    	/**
             * SIMP_NOTIN
	    	 * Negation 5: E ∉ F == ¬ E ∈ F
	    	 */
	    	NotIn(E, F) -> {
	    		return makeUnaryPredicate(
	    			Predicate.NOT, makeRelationalPredicate(Expression.IN, `E, `F));
	    	}


	    	/**
             * SIMP_NOTSUBSET
	    	 * Negation 6: E ⊄ F == ¬ E ⊂ F
	    	 */
	    	NotSubset(E, F) -> {
	    		return makeUnaryPredicate(
	    			Predicate.NOT, makeRelationalPredicate(Expression.SUBSET, `E, `F));
	    	}

	    	/**
             * SIMP_NOTSUBSETEQ
	    	 * Negation 7: E ⊈ F == ¬ E ⊆ F
	    	 */
	    	NotSubsetEq(E, F) -> {
	    		return makeUnaryPredicate(
	    			Predicate.NOT, makeRelationalPredicate(Expression.SUBSETEQ, `E, `F));
	    	}

	    	/**
             * SIMP_SPECIAL_SUBSETEQ
	    	 * Set Theory 5: ∅ ⊆ S == ⊤
	    	 */
	    	SubsetEq(EmptySet(), _) -> {
	    		return Lib.True;
	    	}
	    	
	    	/**
             * SIMP_MULTI_SUBSETEQ
	    	 * Set Theory: S ⊆ S == ⊤
	    	 */
	    	SubsetEq(S, S) -> {
	    		return Lib.True;
	    	}
			
	    	/**
             * DERIV_SUBSETEQ_SETMINUS_L
	    	 * Set Theory: A ∖ B ⊆ S == A ⊆ S ∪ B
	    	 */
	    	SubsetEq(SetMinus(A, B), S) -> {
	    		Expression [] children = new Expression[2];
	    		children[0] = `S;
	    		children[1] = `B;
	    		Expression union = makeAssociativeExpression(Expression.BUNION,
	    				children);
	    		return makeRelationalPredicate(Predicate.SUBSETEQ, `A, union);
	    	}
	    	
	    	/**
             * SIMP_SUBSETEQ_BUNION
	    	 * Set Theory: S ⊆ A ∪ ... ∪ S ∪ ... ∪ B == ⊤
	    	 */
	    	SubsetEq(S, BUnion(children)) -> {
	    		for (Expression child : `children) {
	    			if (child.equals(`S))
	    				return Lib.True;
	    		}
	    	}
			
	    	/**
             * SIMP_SUBSETEQ_BINTER
	    	 * Set Theory: A ∩ ... ∩ S ∩ ... ∩ B ⊆ S == ⊤
	    	 */
	    	SubsetEq(BInter(children), S) -> {
	    		for (Expression child : `children) {
	    			if (child.equals(`S))
	    				return Lib.True;
	    		}
	    	}
			
			/**
             * DERIV_SUBSETEQ_BUNION
	    	 * Set Theory: A ∪ ... ∪ B ⊆ S == A ⊆ S ∧ ... ∧ B ⊆ S
	    	 */
	    	SubsetEq(BUnion(children), S) -> {
	    		Predicate [] newChildren = new Predicate[`children.length];
	    		for (int i = 0; i < `children.length; ++i) {
	    			newChildren[i] = makeRelationalPredicate(Predicate.SUBSETEQ,
	    					`children[i], `S);
	    		}
	    		return makeAssociativePredicate(Predicate.LAND, newChildren);
	    	}
			
			/**
             * DERIV_SUBSETEQ_BINTER
	    	 * Set Theory: S ⊆ A ∩ ... ∩ B  == S ⊆ A ∧ ... ∧ S ⊆ B
	    	 */
	    	SubsetEq(S, BInter(children)) -> {
	    		Predicate [] newChildren = new Predicate[`children.length];
	    		for (int i = 0; i < `children.length; ++i) {
	    			newChildren[i] = makeRelationalPredicate(Predicate.SUBSETEQ,
	    					`S, `children[i]);
	    		}
	    		return makeAssociativePredicate(Predicate.LAND, newChildren);
	    	}
			
			/**
             * SIMP_SPECIAL_IN
	    	 * Set Theory 7: E ∈ ∅ == ⊥
	    	 */
	    	In(_, EmptySet()) -> {
	    		return Lib.False;
	    	}	    	

			/**
             * SIMP_MULTI_IN
	    	 * Set Theory 8: A ∈ {A} == ⊤
	    	 * SIMP_MULTI_IN
             * Set Theory 9: B ∈ {A, ..., B, ..., C} == ⊤
	    	 * SIMP_IN_SING
             * Set Theory 18: E ∈ {F} == E = F (if F is a single expression)
	    	 */
	    	In(E, SetExtension(members)) -> {
	    		for (Expression member : `members) {
					if (`member.equals(`E)) {
						return Lib.True;
					}
				}
				if (`members.length == 1) {
					return makeRelationalPredicate(Predicate.EQUAL, `E, `members[0]);
				}
	    		return predicate;
	    	}

			/**
             * SIMP_IN_COMPSET
	    	 * Set Theory: F ∈ {x,y,... · P(x,y,...) | E(x,y,...)} == ∃x,y,...· P(x,y,...) ∧ E(x,y,...) = F
             * SIMP_IN_COMPSET_ONEPOINT
	    	 * Set Theory 10: E ∈ {x · P(x) | x} == P(E)
	    	 */
	    	In(E, Cset(idents, guard, expression)) -> {
				Predicate equalsPred = makeRelationalPredicate(Predicate.EQUAL,
				                        `expression,
				                        `E.shiftBoundIdentifiers(`idents.length, ff));
				Predicate conjunctionPred = makeAssociativePredicate(Predicate.LAND,
				                                `guard, equalsPred);

				Predicate existsPred = makeQuantifiedPredicate(Predicate.EXISTS,
				                           `idents, conjunctionPred);
				
				final OnePointSimplifier onePoint = 
				    new OnePointSimplifier(existsPred, equalsPred, ff);
				onePoint.match();

				if (onePoint.isApplicable()) {
					// no need to generate a WD PO for the replacement:
					// it is already generated separately by POM 
					// for the whole predicate
					return onePoint.getSimplifiedPredicate();
				} else {
					return existsPred;
				}
	    	}
		
			/**
             * SIMP_EQUAL_SING
	    	 * Set Theory 19: {E} = {F} == E = F   if E, F is a single expression
	    	 */
	    	Equal(SetExtension(E), SetExtension(F)) -> {
   				if (`E.length == 1 && `F.length == 1) {
					return makeRelationalPredicate(Predicate.EQUAL, `E[0], `F[0]);
				}
				return predicate;
	    	}
	    	
	    	/**
             * SIMP_LIT_EQUAL
	    	 * Arithmetic 16: i = j == ⊤  or  i = j == ⊥ (by computation)
	    	 */
	    	Equal(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		return `i.equals(`j) ? Lib.True : Lib.False;
	    	}

	    	/**
             * SIMP_LIT_LE
	    	 * Arithmetic 17: i ≤ j == ⊤  or  i ≤ j == ⊥ (by computation)
	    	 */
	    	Le(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		return `i.compareTo(`j) <= 0 ? Lib.True : Lib.False;
	    	}

	    	/**
             * SIMP_LIT_LT
	    	 * Arithmetic 18: i < j == ⊤  or  i < j == ⊥ (by computation)
	    	 */
	    	Lt(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		return `i.compareTo(`j) < 0 ? Lib.True : Lib.False;
	    	}

	    	/**
             * SIMP_LIT_GE
	    	 * Arithmetic 19: i ≥ j == ⊤  or  i ≥ j == ⊥ (by computation)
	    	 */
	    	Ge(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		return `i.compareTo(`j) >= 0 ? Lib.True : Lib.False;
	    	}

	    	/**
             * SIMP_LIT_GT
	    	 * Arithmetic 20: i > j == ⊤  or  i > j == ⊥ (by computation)
	    	 */
	    	Gt(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		return `i.compareTo(`j) > 0 ? Lib.True : Lib.False;
	    	}
	    	
	    	/**
	    	 * Cardinality:
             * SIMP_SPECIAL_EQUAL_CARD
             * card(S) = 0  ==  S = ∅
             * SIMP_LIT_EQUAL_CARD_1
	    	 * card(S) = 1  ==  ∃x·S = {x}
	    	 */
	    	Equal(Card(S), E) -> {
	    		if (`E.equals(number0)) {
	    			Expression emptySet = makeEmptySet(`S.getType());
	    			return makeRelationalPredicate(Predicate.EQUAL, `S, emptySet);
	    		}
	    		else if (`E.equals(number1)) {
	    			return FormulaUnfold.makeExistSingletonSet(`S);
	    		}
	    	}

	    	/**
	    	 * Cardinality:
             * SIMP_SPECIAL_EQUAL_CARD
             * 0 = card(S)  ==  S = ∅
             * SIMP_LIT_EQUAL_CARD_1
	    	 * 1 = card(S)  ==  ∃x·S = {x}
	    	 */
	    	Equal(E, Card(S)) -> {
	    		if (`E.equals(number0)) {
	    			Expression emptySet = makeEmptySet(`S.getType());
	    			return makeRelationalPredicate(Predicate.EQUAL, `S, emptySet);
	    		}
	    		else if (`E.equals(number1)) {
	    			return FormulaUnfold.makeExistSingletonSet(`S);
	    		}
	    	}

	    	/**
             * SIMP_LIT_GT_CARD_0
	    	 * Cardinality: card(S) > 0  ==  ¬(S = ∅)
	    	 */
	    	Gt(Card(S), E)-> {
	    		if (`E.equals(number0)) {
	    			Expression emptySet = makeEmptySet(`S.getType());
	    			Predicate equal = makeRelationalPredicate(Predicate.EQUAL, `S, emptySet);
	    			return makeUnaryPredicate(Predicate.NOT, equal);
	    		}
	    	}

	    	/**
             * SIMP_LIT_LT_CARD_0
	    	 * Cardinality: 0 < card(S)  ==  ¬(S = ∅)
	    	 */
	    	Lt(E, Card(S)) -> {
	    		if (`E.equals(number0)) {
	    			Expression emptySet = makeEmptySet(`S.getType());
	    			Predicate equal = makeRelationalPredicate(Predicate.EQUAL, `S, emptySet);
	    			return makeUnaryPredicate(Predicate.NOT, equal);
	    		}
	    	}

	    	/**
             * SIMP_LIT_EQUAL_KBOOL_TRUE
	    	 * Boolean: TRUE = bool(P) == P  
	    	 */
	    	Equal(TRUE(), Bool(P)) -> {
	    		return `P;
	    	}

	    	/**
             * SIMP_LIT_EQUAL_KBOOL_TRUE
	    	 * Boolean: bool(P) = TRUE == P  
	    	 */
	    	Equal(Bool(P), TRUE()) -> {
	    		return `P;
	    	}

	    	/**
             * SIMP_LIT_EQUAL_KBOOL_FALSE
	    	 * Boolean: FALSE = bool(P) == ¬P  
	    	 */
	    	Equal(FALSE(), Bool(P)) -> {
	    		return makeUnaryPredicate(Predicate.NOT, `P);
	    	}

	    	/**
             * SIMP_LIT_EQUAL_KBOOL_FALSE
	    	 * Boolean: bool(P) = FALSE == ¬P  
	    	 */
	    	Equal(Bool(P), FALSE()) -> {
	    		return makeUnaryPredicate(Predicate.NOT, `P);
	    	}
	    }
	    return predicate;
	}
	
	@ProverRule( { "SIMP_SPECIAL_BINTER", "SIMP_SPECIAL_BUNION",
			"SIMP_TYPE_BINTER", "SIMP_TYPE_BUNION","SIMP_MULTI_BINTER",
            "SIMP_MULTI_BUNION", "SIMP_SPECIAL_PLUS", "SIMP_SPECIAL_PROD_1",
            "SIMP_SPECIAL_PROD_0", "SIMP_SPECIAL_PROD_MINUS_EVEN",
            "SIMP_SPECIAL_PROD_MINUS_ODD", "SIMP_SPECIAL_FCOMP",
            "SIMP_SPECIAL_BCOMP", "SIMP_SPECIAL_OVERL" })
	@Override
	public Expression rewrite(AssociativeExpression expression) {
	    %match (Expression expression) {

	    	/**
             * SIMP_SPECIAL_BINTER
	    	 * Set Theory: S ∩ ... ∩ ∅ ∩ ... ∩ T == ∅
	    	 * SIMP_SPECIAL_BUNION
             * Set Theory: S ∪ ... ∪ ∅ ∪ ... ∪ T == S ∪ ... ∪ T
	    	 * SIMP_TYPE_BINTER
             * Set Theory: S ∩ ... ∩ U ∩ ... ∩ T == S ∩ ... ∩ ... ∩ T
	    	 * SIMP_TYPE_BUNION
             * Set Theory: S ∪ ... ∪ U ∪ ... ∪ T == U
             * SIMP_MULTI_BINTER
             * Set Theory: S ∩ ... ∩ T ∩ T ∩ ... ∩ V == S ∩ ... ∩ T ∩ ... ∩ V
             * SIMP_MULTI_BUNION
             * Set Theory: S ∪ ... ∪ T ∪ T ∪ ... ∪ V == S ∪ ... ∪ T ∪ ... ∪ V
	    	 */
	    	(BInter | BUnion) (children) -> {
	    		return FormulaSimplification.simplifyAssociativeExpression(expression, `children);
	    	}

	    	/**
	    	 * SIMP_SPECIAL_PLUS
             * Arithmetic 1: E + ... + 0 + ... + F == E + ... + ... + F
	    	 */
	    	Plus (children) -> {
	    		return FormulaSimplification.simplifyAssociativeExpression(expression, `children);
	    	}

	    	/**
	    	 * SIMP_SPECIAL_PROD_1
             * Arithmetic 5: E ∗ ... ∗ 1 ∗ ... ∗ F == E ∗ ... ∗ ... ∗ F
	    	 * SIMP_SPECIAL_PROD_0
             * Arithmetic 6: E ∗ ... ∗ 0 ∗ ... ∗ F == 0
	    	 * SIMP_SPECIAL_PROD_MINUS_EVEN
             * Arithmetic 7: (-E) ∗ (-F) == (E * F)
	    	 * SIMP_SPECIAL_PROD_MINUS_ODD
             * Arithmetic 7.1: (-E) ∗ F == -(E * F)
	    	 */
	    	Mul (_) -> {
	    		return MultiplicationSimplifier.simplify(expression, ff);
	    	}
	    	
	    	/**
	    	 * SIMP_SPECIAL_FCOMP
             * Set Theory: p; ... ;∅; ... ;q == ∅
	    	 */
	    	Fcomp (children) -> {
	    		for (Expression child : `children) {
	    			if (Lib.isEmptySet(child)) {
	    				return ff.makeEmptySet(expression.getType(), null);
	    			}
	    		}
	    	}
	
	    	/**
	    	 * SIMP_SPECIAL_BCOMP
             * Set Theory: p∘ ... ∘∅∘ ... ∘q == ∅
	    	 */
	    	Bcomp (children) -> {
	    		for (Expression child : `children) {
	    			if (Lib.isEmptySet(child)) {
	    				return ff.makeEmptySet(expression.getType(), null);
	    			}
	    		}
	    	}
	    	
            /**
             * SIMP_SPECIAL_OVERL
			 * Set theory: r  ...  ∅  ...  s  ==  r  ...  s
			 */
	    	Ovr(children) -> {
	    		return FormulaSimplification.simplifyAssociativeExpression(expression, `children);
    		}
	    	
	    }
	    return expression;
	}

	@ProverRule( { "SIMP_MULTI_SETMINUS", "SIMP_SPECIAL_SETMINUS_L",
			"SIMP_SPECIAL_SETMINUS_R", "SIMP_TYPE_SETMINUS",
			"SIMP_TYPE_SETMINUS_SETMINUS", "SIMP_MULTI_MINUS",
			"SIMP_SPECIAL_MINUS_R", "SIMP_SPECIAL_MINUS_L", "SIMP_MULTI_DIV",
			"SIMP_SPECIAL_DIV_1", "SIMP_SPECIAL_DIV_0", "SIMP_MULTI_DIV_PROD",
			"SIMP_DIV_MINUS", "SIMP_SPECIAL_EXPN_1_R", "SIMP_SPECIAL_EXPN_0",
			"SIMP_SPECIAL_EXPN_1_L", "SIMP_FUNIMAGE_FUNIMAGE_CONVERSE",
			"SIMP_MULTI_FUNIMAGE_OVERL_SETENUM",
			"SIMP_FUNIMAGE_FUNIMAGE_CONVERSE_SETENUM",
			"SIMP_SPECIAL_RELIMAGE_R", "SIMP_SPECIAL_RELIMAGE_L",
			"SIMP_FUNIMAGE_CPROD" })
	@Override
	public Expression rewrite(BinaryExpression expression) {
	    %match (Expression expression) {

			/**
             * SIMP_MULTI_SETMINUS
	    	 * Set Theory 11: S ∖ S == ∅
	    	 */
	    	SetMinus(S, S) -> {
	    		return makeEmptySet(`S.getType());
	    	}

			/**
	    	 * SIMP_SPECIAL_SETMINUS_L
             * Set Theory: ∅ ∖ S == ∅
	    	 * SIMP_SPECIAL_SETMINUS_R
             * Set Theory: S ∖ ∅ == S
	    	 * SIMP_TYPE_SETMINUS
             * Set Theory: S ∖ U == ∅
	    	 */
	    	SetMinus(S, T) -> {
				PowerSetType type = (PowerSetType) `S.getType();

	    		Expression emptySet = makeEmptySet(type);
				if (`S.equals(emptySet)) {
					return emptySet;
				}
   				if (`T.equals(emptySet)) {
					return `S;
				}
				if (`T.equals(type.getBaseType().toExpression(ff))) {
					return emptySet;
				}
	    	}
	    	
			/**
	    	 * SIMP_TYPE_SETMINUS_SETMINUS
             * Set Theory: U ∖ (U ∖ S) == S
	    	 */
			SetMinus(U, SetMinus(U, S)) -> {
				PowerSetType type = (PowerSetType) `S.getType();

				if (`U.equals(type.getBaseType().toExpression(ff))) {
					return `S;
				}
			}
			
			/**
			 * SIMP_MULTI_MINUS
             * Arithmetic: E − E == 0
			 */
			Minus(E, E) -> {
				return number0;
			}

			/**
	    	 * SIMP_SPECIAL_MINUS_R
             * Arithmetic: E − 0 == E
	    	 * SIMP_SPECIAL_MINUS_L
             * Arithmetic: 0 − E == −E
	    	 */
	    	Minus(E, F) -> {
	    		if (`F.equals(number0)) {
					return `E;
				} else if (`E.equals(number0)) {
					return makeUnaryExpression(Expression.UNMINUS, `F);
				}
				return expression;
	    	}

			/**
	    	 * SIMP_MULTI_DIV
             * Arithmetic: E ÷ E = 1
	    	 */
	    	Div(E, E) -> {
	    		return Lib.makeIntegerLiteral(1);
	    	}

			/**
			 * SIMP_SPECIAL_DIV_1
             * Arithmetic: E ÷ 1 = E
			 */
			Div(E, IntegerLiteral(F)) -> {
				if (`F.equals(BigInteger.ONE))
					return `E;
			}
			
			/**
			 * SIMP_SPECIAL_DIV_0
             * Arithmetic: 0 ÷ E = 0
			 */
			Div(IntegerLiteral(F), _) -> {
				if (`F.equals(BigInteger.ZERO))
					return number0;
			}

			/**
	    	 * SIMP_MULTI_DIV_PROD 
             * Arithmetic: (X ∗ ... ∗ E ∗ ... ∗ Y) ÷ E == X ∗ ... ∗ Y
	    	 */
	    	Div(Mul(children), E) -> {
	    		Collection<Expression> newChildren = new ArrayList<Expression>();

				boolean found = false;
				for (Expression child : `children) {
					if (found)
						newChildren.add(child);
					else if (child.equals(`E))
						found = true;
					else
						newChildren.add(child);
				}
	    		if (newChildren.size() < `children.length) {
		    		if (newChildren.size() == 1) {
		    			return newChildren.iterator().next();
		    		}
					return makeAssociativeExpression(Expression.MUL, newChildren);
	    		}
	    	}

			/**
	    	 * SIMP_DIV_MINUS
             * Arithmetic: (−E) ÷ (−F) == E ÷ F
	    	 */
	    	Div(UnMinus(E), UnMinus(F)) -> {
	    		return FormulaSimplification.getFaction(`E, `F);
	    	}

			/**
	    	 * SIMP_DIV_MINUS
             * Arithmetic: (−E) ÷ (−F) == E ÷ F
	    	 */
	    	Div(UnMinus(E), IntegerLiteral(F)) -> {
	    		return FormulaSimplification.getFaction(`expression, `E, `F);
	    	}

			/**
	    	 * SIMP_DIV_MINUS
             * Arithmetic 10: (−E) ÷ (−F) == E ÷ F
	    	 */
	    	Div(IntegerLiteral(E), UnMinus(F)) -> {
	    		return FormulaSimplification.getFaction(`expression, `E, `F);
	    	}

			/**
	    	 * SIMP_SPECIAL_DIV_1
             * Arithmetic: E ÷ 1 = E
	    	 * SIMP_SPECIAL_DIV_0
             * Arithmetic: 0 ÷ E = 0
	    	 * SIMP_DIV_MINUS
             * Arithmetic: (−E) ÷ (−F) == E ÷ F
	    	 */
	    	Div(IntegerLiteral(E), IntegerLiteral(F)) -> {
	    		return FormulaSimplification.getFaction(`expression, `E, `F);
	    	}

			/**
	    	 * SIMP_SPECIAL_EXPN_1_R
             * Arithmetic: E^1 == E
	    	 * SIMP_SPECIAL_EXPN_0
             * Arithmetic: E^0 == 1
	    	 * SIMP_SPECIAL_EXPN_1_L
             * Arithmetic: 1^E == 1
	    	 */
	    	Expn (E, F) -> {
   				if (`F.equals(number1)) {
					return `E;
				} else if (`F.equals(number0)) {
					return number1;
				} else if (`E.equals(number1)) {
					return number1;
				}
				return expression;
	    	}
	    	
	    	/**
	    	 * SIMP_FUNIMAGE_FUNIMAGE_CONVERSE
             * Set Theory: f(f∼(E)) = E
	    	 */
	    	FunImage(f, FunImage(Converse(f), E)) -> {
				return `E;
	    	}

	    	/**
	    	 * SIMP_FUNIMAGE_CONVERSE_FUNIMAGE
	    	 * Set Theory: f∼(f(E)) = E
	    	 */
	    	FunImage(Converse(f), FunImage(f, E)) -> {
				return `E;
	    	}

	    	/**
	    	 * SIMP_MULTI_FUNIMAGE_OVERL_SETENUM
             * Set Theory 16: (f  {E↦ F})(E) == F
	    	 */
	    	FunImage(Ovr(children), E) -> {
	    		Expression lastExpression = `children[`children.length - 1];
				if (lastExpression instanceof SetExtension) {
					SetExtension sExt = (SetExtension) lastExpression;
					Expression[] members = sExt.getMembers();
					if (members.length == 1) {
						Expression child = members[0];
						if (child instanceof BinaryExpression
								&& child.getTag() == Expression.MAPSTO) {
							if (((BinaryExpression) child).getLeft().equals(`E)) {
								return ((BinaryExpression) child).getRight();
							}
						}
					}
				}

				return expression;
	    	}

	    	/**
	    	 * SIMP_FUNIMAGE_FUNIMAGE_CONVERSE_SETENUM
             * Set Theory: {x ↦ a, ..., y ↦ b}({a ↦ x, ..., b ↦ y}(E)) == E
	    	 */
	    	FunImage(SetExtension(children1), FunImage(SetExtension(children2), E)) -> {
				if (`children1.length != `children2.length)
					return expression;
				for (int i = 0; i < `children1.length; ++i) {
					Expression map1 = `children1[i];
					Expression map2 = `children2[i];
					if (!(Lib.isMapping(map1) && Lib.isMapping(map2)))
						return expression;	
					
					BinaryExpression bExp1 = (BinaryExpression) map1;
					BinaryExpression bExp2 = (BinaryExpression) map2;
					
					if (!(bExp1.getRight().equals(bExp2.getLeft()) &&
							 bExp2.getRight().equals(bExp1.getLeft())))
						return expression;
				}
				return `E;
	    	}

			/**
			 * SIMP_SPECIAL_RELIMAGE_R
             * Set Theory: r[∅] == ∅
			 */
			RelImage(r, EmptySet()) -> {
				return makeEmptySet(ff.makePowerSetType(Lib.getRangeType(`r)));
			}

			/**
			 * SIMP_SPECIAL_RELIMAGE_L
             * Set Theory: ∅[A] == ∅
			 */
			RelImage(r@EmptySet(), _) -> {
				return makeEmptySet(ff.makePowerSetType(Lib.getRangeType(`r)));
			}

			/**
			 * SIMP_FUNIMAGE_CPROD
             * Set Theory: (S × {E})(x) == E
			 */
			FunImage(Cprod(_, SetExtension(children)), _) -> {
				if (`children.length == 1)
					return `children[0];
			}

            /**
             * SIMP_FUNIMAGE_LAMBDA
             *
             */
            FunImage(cSet@Cset(_,_,Mapsto(_,_)),x) -> {
                final QuantifiedExpression cSet = (QuantifiedExpression) `cSet;
                if (cSet.getForm() == QuantifiedExpression.Form.Lambda) {
                    final LambdaImageComputer computer = new LambdaImageComputer(cSet);
                    computer.init();
                    final Expression lambdaImage = computer.computeImage(`x, ff);
                    if (lambdaImage != null) {
                        return lambdaImage;
                    }
                }
            }
		}
	    return expression;
	}

	@ProverRule( { "SIMP_CONVERSE_CONVERSE", "SIMP_CONVERSE_SETENUM",
			"SIMP_DOM_COMPSET", "SIMP_RAN_COMPSET", "SIMP_MINUS_MINUS",
			"SIMP_SPECIAL_CARD", "SIMP_CARD_SING", "SIMP_CARD_POW",
			"SIMP_CARD_CPROD", "SIMP_CARD_SETMINUS", "SIMP_CARD_BUNION",
			"SIMP_SPECIAL_DOM", "SIMP_SPECIAL_RAN" })
	@Override
	public Expression rewrite(UnaryExpression expression) {
	    %match (Expression expression) {

			/**
             * SIMP_CONVERSE_CONVERSE
	    	 * Set Theory 14: r∼∼ == r
	    	 */
	    	Converse(Converse(r)) -> {
	    		return `r;
	    	}

			/**
             * SIMP_CONVERSE_SETENUM
	    	 * Set Theory: {x ↦ a, ..., y ↦ b}∼ == {a ↦ x, ..., b ↦ y}
	    	 */
	    	Converse(SetExtension(members)) -> {
	   				Collection<Expression> newMembers = new LinkedHashSet<Expression>();

				for (Expression member : `members) {
					if (member instanceof BinaryExpression
							&& member.getTag() == Expression.MAPSTO) {
						BinaryExpression bExp = (BinaryExpression) member;
						newMembers.add(
								makeBinaryExpression(
										Expression.MAPSTO, bExp.getRight(), bExp.getLeft()));
					} else {
						return expression;
					}
				}

				return makeSetExtension(newMembers);
	    	}

			/**
             * SIMP_DOM_COMPSET
	    	 * Set Theory 15: dom(x ↦ a, ..., y ↦ b) = {x, ..., y} 
	    	 *                (Also remove duplicate in the resulting set) 
	    	 */
	    	Dom(SetExtension(members)) -> {
   				Collection<Expression> domain = new LinkedHashSet<Expression>();

				for (Expression member : `members) {
					if (member instanceof BinaryExpression
							&& member.getTag() == Expression.MAPSTO) {
						BinaryExpression bExp = (BinaryExpression) member;
						domain.add(bExp.getLeft());
					} else {
						return expression;
					}
				}

				return makeSetExtension(domain);
	    	}
		
			/**
             * SIMP_RAN_COMPSET
	    	 * Set Theory 16: ran(x ↦ a, ..., y ↦ b) = {a, ..., b}
	    	 */
	    	Ran(SetExtension(members)) -> {
	    		Collection<Expression> range = new LinkedHashSet<Expression>();

				for (Expression member : `members) {
					if (member instanceof BinaryExpression
							&& member.getTag() == Expression.MAPSTO) {
						BinaryExpression bExp = (BinaryExpression) member;
						range.add(bExp.getRight());
					} else {
						return expression;
					}
				}

				return makeSetExtension(range);
	    	}

			/**
	    	 * SIMP_MINUS_MINUS
             * Arithmetic 4: −(−E) = E
	    	 */
	    	UnMinus(UnMinus(E)) -> {
	    		return `E;
	    	}
			
			/**
             * SIMP_SPECIAL_CARD
	    	 * Cardinality: card(∅) == 0
	    	 */
			Card(EmptySet()) -> {
				return number0;
			}

			/**
             * SIMP_CARD_SING
	    	 * Cardinality: card({E}) == 1
	    	 */
			Card(SetExtension(children)) -> {
				if (`children.length == 1)
					return number1;
			}

			/**
             * SIMP_CARD_POW
	    	 * Cardinality: card(ℙ(S)) == 2^(card(S))
	    	 */
			Card(Pow(S)) -> {
				Expression cardS = makeUnaryExpression(Expression.KCARD, `S);
				return makeBinaryExpression(Expression.EXPN, number2, cardS);
			}
			
			/**
	    	 * SIMP_CARD_CPROD
             * Cardinality: card(S × T) == card(S) ∗ card(T)
	    	 */
			Card(Cprod(S, T)) -> {
				Expression [] cards = new Expression[2];
				cards[0] = makeUnaryExpression(Expression.KCARD, `S);
				cards[1] = makeUnaryExpression(Expression.KCARD, `T);
				return makeAssociativeExpression(Expression.MUL, cards);
			}
			
			/**
             * SIMP_CARD_SETMINUS
	    	 * Cardinality: card(S ∖ T) = card(S) − card(S ∩ T)
	    	 */
			Card(SetMinus(S, T)) -> {
				Expression cardS = makeUnaryExpression(Expression.KCARD, `S);
				Expression [] children = new Expression[2];
				children[0] = `S;
				children[1] = `T;
				Expression sInterT = makeAssociativeExpression(Expression.BINTER,
						children);
				Expression cardSInterT = makeUnaryExpression(Expression.KCARD,
						sInterT);
				return makeBinaryExpression(Expression.MINUS, cardS, cardSInterT);
			}
			
			/**
             * SIMP_CARD_BUNION
	    	 * Cardinality:    card(S(1) ∪ ... ∪ S(n))
	    	 *               = card(S(1)) + ...  + card(S(n))
             *                 − (card(S(1) ∩ S(2)) + ... + card(S(n−1) ∩ S(n)))
             *                 + (card(S(1) ∩ S(2) ∩ S(3)) + ... + card(S(n−2) ∩ S(n−1) ∩ S(n)))
             *                 − ...                         
             *                 + ((−1)^(n-1) ∗ card(S(1) ∩ ... ∩ S(n)))
	    	 */
	    	Card(BUnion(children)) -> {
	    		int length = `children.length;
	    		Expression [] subFormulas = new Expression[length];
	    		for (int i = 1; i <= length; ++i) {
					List<List<Expression>> expressions = getExpressions(`children, 0, i);
					
					List<Expression> newChildren = new ArrayList<Expression>(expressions.size());
					for (List<Expression> list : expressions) {
						Expression inter;
						if (list.size() == 1)
							inter = list.iterator().next();
						else
							inter = makeAssociativeExpression(
									Expression.BINTER, list);
						Expression card = makeUnaryExpression(Expression.KCARD,
								inter);
						newChildren.add(card);
					}
					if (newChildren.size() != 1) 
						subFormulas[i-1] = makeAssociativeExpression(
								Expression.PLUS, newChildren);
					else
						subFormulas[i-1] = newChildren.iterator().next();
	    		} 
	    		Expression result = subFormulas[0];
	    		boolean positive = false;
	    		for (int i = 1; i < length; ++i) {
	    			if (positive) {
						Expression [] newChildren = new Expression[2];
						newChildren[0] = result;
						newChildren[1] = subFormulas[i];
						result = makeAssociativeExpression(Expression.PLUS,
								newChildren);
	    			}
	    			else {
	    				result = makeBinaryExpression(Expression.MINUS,
	    						result, subFormulas[i]);
	    			}
	    			positive = !positive;
	    		}
	    		return result;
	    	}
			
			/**
             * SIMP_SPECIAL_DOM
			 * Set Theory: dom(∅) == ∅
			 */
			Dom(r) -> {
				if (`r.equals(makeEmptySet(`r.getType())))
					return makeEmptySet(ff.makePowerSetType(Lib.getDomainType(`r)));
			}
			
			/**
             * SIMP_SPECIAL_RAN
			 * Set Theory: ran(∅) == ∅
			 */
			Ran(r) -> {
				if (`r.equals(makeEmptySet(`r.getType())))
					return makeEmptySet(ff.makePowerSetType(Lib.getRangeType(`r)));
			}
	    
	    }
	    return expression;
	}

	@ProverRule( { "SIMP_SPECIAL_KBOOL_BFALSE", "SIMP_SPECIAL_KBOOL_BTRUE" }) 
    @Override
	public Expression rewrite(BoolExpression expression) {
	    %match (Expression expression) {
	   		/**
             * SIMP_SPECIAL_KBOOL_BFALSE
	    	 * Set Theory:	bool(⊥) = FALSE
	    	 */
	    	Bool(BFALSE()) -> {
				return ff.makeAtomicExpression(Expression.FALSE, null);
			}

	   		/**
             * SIMP_SPECIAL_KBOOL_BTRUE
	    	 * Set Theory:	bool(⊤) = TRUE  
	    	 */
	    	Bool(BTRUE()) -> {
				return ff.makeAtomicExpression(Expression.TRUE, null);
			}
    	}
	    return expression;
    }

	private List<List<Expression>> getExpressions(Expression [] array, int from, int size) {
		List<List<Expression>> result = new ArrayList<List<Expression>>();
		if (size == 0) {
			result.add(new ArrayList<Expression>());
		}
		else {
			for (int i = from; i <= array.length - size; ++i) {
				List<List<Expression>> lists = getExpressions(array, i + 1, size - 1);
				for (List<Expression> list : lists) {
					List<Expression> newList = new ArrayList<Expression>();
					newList.add(array[i]);
					newList.addAll(list);
					result.add(newList);
				}
			}
		}
		return result;
	}
    
    @ProverRule("SIMP_MULTI_SETENUM") 
	@Override
	public Expression rewrite(SetExtension expression) {
	    %match (Expression expression) {
			/**
             * SIMP_MULTI_SETENUM
	    	 * Set Theory: {A, ..., B, ..., B, ..., C} == {A, ..., B, ..., C}
	    	 */
	    	SetExtension(members) -> {
	    		Collection<Expression> newMembers = new LinkedHashSet<Expression>();

				for (Expression member : `members) {
					newMembers.add(member);
				}
				if (newMembers.size() != `members.length)	
					return makeSetExtension(newMembers);
	    	}
		}
	    return expression;
	}
}
