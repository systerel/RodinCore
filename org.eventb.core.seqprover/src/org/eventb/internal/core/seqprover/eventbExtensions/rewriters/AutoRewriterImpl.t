/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *     Systerel - SIMP_IN_COMPSET_*, SIMP_SPECIAL_OVERL, SIMP_FUNIMAGE_LAMBDA
 *     Systerel - Added tracing mechanism
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

	public static boolean DEBUG;

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

	private static <T extends Formula<T>> void trace(T from, T to, String rule,
			String... otherRules) {
		if (!DEBUG) {
			return;
		}
		if (from == to) {
			return;
		}
		final StringBuilder sb = new StringBuilder();
		sb.append("AutoRewriter: ");
		sb.append(from);
		sb.append("  \u219d  ");
		sb.append(to);

		sb.append("   (");
		sb.append(rule);
		for (final String r : otherRules) {
			sb.append(" | ");
			sb.append(r);
		}
		sb.append(")");

		System.out.println(sb);
	}
	
	%include {FormulaV2.tom}
	
	@ProverRule( { "SIMP_SPECIAL_FINITE", "SIMP_FINITE_SETENUM",
			"SIMP_FINITE_BUNION", "SIMP_FINITE_POW", "DERIV_FINITE_CPROD",
			"SIMP_FINITE_CONVERSE", "SIMP_FINITE_UPTO" })
	@Override
	public Predicate rewrite(SimplePredicate predicate) {
		final Predicate result;
	    %match (Predicate predicate) {

			/**
             * SIMP_SPECIAL_FINITE
	    	 * Finite: finite(∅) = ⊤
	    	 */
			Finite(EmptySet()) -> {
				result = Lib.True;
				trace(predicate, result, "SIMP_SPECIAL_FINITE");
				return result;
			}

			/**
             * SIMP_FINITE_SETENUM
	    	 * Finite: finite({a, ..., b}) = ⊤
	    	 */
			Finite(SetExtension(_)) -> {
				result = Lib.True;
				trace(predicate, result, "SIMP_FINITE_SETENUM");
				return result;
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
				result = makeAssociativePredicate(Predicate.LAND, newChildren);
				trace(predicate, result, "SIMP_FINITE_BUNION");
				return result;
			}

			/**
             * SIMP_FINITE_POW
	    	 * Finite: finite(ℙ(S)) == finite(S)
	    	 */
			Finite(Pow(S)) -> {
				result = makeSimplePredicate(Predicate.KFINITE, `S);
				trace(predicate, result, "SIMP_FINITE_POW");
				return result;
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
				result = makeAssociativePredicate(Predicate.LOR, children);
				trace(predicate, result, "DERIV_FINITE_CPROD");
				return result;
			}

			/**
             * SIMP_FINITE_CONVERSE
	    	 * Finite: finite(r∼) == finite(r)
	    	 */
			Finite(Converse(r)) -> {
				result = makeSimplePredicate(Predicate.KFINITE, `r);
				trace(predicate, result, "SIMP_FINITE_CONVERSE");
				return result;
			}

			/**
             * SIMP_FINITE_UPTO
	    	 * Finite: finite(a‥b) == ⊤
	    	 */
			Finite(UpTo(_,_)) -> {
				result = Lib.True;
				trace(predicate, result, "SIMP_FINITE_UPTO");
				return result;
			}

	    }
	    return predicate;
	}

    @ProverRule( { "SIMP_SPECIAL_AND_BTRUE", "SIMP_SPECIAL_AND_BFALSE",
			"SIMP_SPECIAL_OR_BTRUE", "SIMP_SPECIAL_OR_BFALSE" })
	@Override
	public Predicate rewrite(AssociativePredicate predicate) {
		final Predicate result;
	    %match (Predicate predicate) {
	    	/**
             * SIMP_SPECIAL_AND_BTRUE
	    	 * Conjunction 1: P ∧ ... ∧ ⊤ ∧ ... ∧ Q  == P ∧ ... ∧ Q
             * SIMP_SPECIAL_AND_BFALSE
	    	 * Conjunction 2: P ∧ ... ∧ ⊥ ∧ ... ∧ Q  == ⊥
	    	 */
	    	Land(children) -> {
				result = FormulaSimplification.simplifyAssociativePredicate(predicate, `children, Lib.True,
    				Lib.False);
				trace(predicate, result, "SIMP_SPECIAL_AND_BTRUE", "SIMP_SPECIAL_AND_BFALSE");
				return result;
			}

	    	/**
             * SIMP_SPECIAL_OR_BTRUE
	    	 * Disjunction 1: P ⋁ ... ⋁ ⊤ ⋁ ... ⋁ Q  == ⊤
             * SIMP_SPECIAL_OR_BFALSE
	    	 * Disjunction 2: P ⋁ ... ⋁ ⊥ ⋁ ... ⋁ Q  == P ⋁ ... ⋁ Q
	    	 */
	    	Lor(children) -> {
				result = FormulaSimplification.simplifyAssociativePredicate(predicate, `children, Lib.False,
    				Lib.True);
				trace(predicate, result, "SIMP_SPECIAL_OR_BTRUE", "SIMP_SPECIAL_OR_BFALSE");
				return result;
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
		final Predicate result;
	    %match (Predicate predicate) {
	    	/**
             * SIMP_SPECIAL_IMP_BTRUE_L
	    	 * Implication 1: ⊤ ⇒ P == P
	    	 */
	    	Limp(BTRUE(), P) -> {
	    		result = `P;
	    		trace(predicate, result, "SIMP_SPECIAL_IMP_BTRUE_L");
				return result;
            }

	    	/**
             * SIMP_SPECIAL_IMP_BFALSE_L
	    	 * Implication 2: ⊥ ⇒ P == ⊤
	    	 */
	    	Limp(BFALSE(), _) -> {
	    		result = Lib.True;
	    		trace(predicate, result, "SIMP_SPECIAL_IMP_BFALSE_L");
				return result;
 	    	}

	    	/**
             * SIMP_SPECIAL_IMP_BTRUE_R
	    	 * Implication 3: P ⇒ ⊤ == ⊤
	    	 */
	    	Limp(_, BTRUE()) -> {
	    		result = predicate.getRight();
	    		trace(predicate, result, "SIMP_SPECIAL_IMP_BTRUE_R");
				return result;
 	    	}
	    	
	    	/**
             * SIMP_SPECIAL_IMP_BFALSE_R
	    	 * Implication 4: P ⇒ ⊥ == ¬P
	    	 */
	    	Limp(P, BFALSE()) -> {
	    		result = Lib.makeNeg(`P);
	    		trace(predicate, result, "SIMP_SPECIAL_IMP_BFALSE_R");
				return result;
 	    	}

	    	/**
             * SIMP_MULTI_IMP
	    	 * Implication 5: P ⇒ P == ⊤
	    	 */
	    	Limp(P, P) -> {
	    		result = Lib.True;
	    		trace(predicate, result, "SIMP_MULTI_IMP");
				return result;
 	    	}

	    	/**
             * SIMP_MULTI_EQV
	    	 * Equivalent 5: P ⇔ P == ⊤
	    	 */
	    	Leqv(P, P) -> {
	    		result = Lib.True;
	    		trace(predicate, result, "SIMP_MULTI_EQV");
				return result;
 	    	}

	    	/**
             * SIMP_SPECIAL_EQV_BTRUE
	    	 * Equivalent 1: P ⇔ ⊤ == P
	    	 */
	    	Leqv(P, BTRUE()) -> {
	    		result = `P;
	    		trace(predicate, result, "SIMP_SPECIAL_EQV_BTRUE");
				return result;
 	    	}

	    	/**
             * SIMP_SPECIAL_EQV_BTRUE
	    	 * Equivalent 2: ⊤ ⇔ P = P
	    	 */
	    	Leqv(BTRUE(), P) -> {
	    		result = `P;
	    		trace(predicate, result, "SIMP_SPECIAL_EQV_BTRUE");
				return result;
 	    	}

	    	/**
             * SIMP_SPECIAL_EQV_BFALSE
	    	 * Equivalent 3: P ⇔ ⊥ = ¬P
	    	 */
	    	Leqv(P, BFALSE()) -> {
	    		result = Lib.makeNeg(`P);
	    		trace(predicate, result, "SIMP_SPECIAL_EQV_BFALSE");
				return result;
 	    	}

	    	/**
             * SIMP_SPECIAL_EQV_BFALSE
	    	 * Equivalent 4: ⊥ ⇔ P == ¬P
	    	 */
	    	Leqv(BFALSE(), P) -> {
	    		result = Lib.makeNeg(`P);
	    		trace(predicate, result, "SIMP_SPECIAL_EQV_BFALSE");
				return result;
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
		final Predicate result;
	    %match (Predicate predicate) {

	    	/**
             * SIMP_SPECIAL_NOT_BTRUE
	    	 * Negation 1: ¬⊤ == ⊥
	    	 */
	    	Not(BTRUE()) -> {
	    		result = Lib.False;
	    		trace(predicate, result, "SIMP_SPECIAL_NOT_BTRUE");
				return result;
			}

	    	/**
             * SIMP_SPECIAL_NOT_BFALSE
	    	 * Negation 2: ¬⊥ == ⊤
	    	 */
			Not(BFALSE()) -> {
				result =  Lib.True;
	    		trace(predicate, result, "SIMP_SPECIAL_NOT_BFALSE");
				return result;
			}

	    	/**
             * SIMP_NOT_NOT
	    	 * Negation 3: ¬¬P == P
	    	 */
			Not(Not(P)) -> {
				result =  `P;
	    		trace(predicate, result, "SIMP_NOT_NOT");
				return result;
			}

	    	/**
             * SIMP_NOT_LE
	    	 * Negation 8: ¬ a ≤ b == a > b
	    	 */
			Not(Le(a, b)) -> {
				result =  makeRelationalPredicate(Predicate.GT, `a, `b);
	    		trace(predicate, result, "SIMP_NOT_LE");
				return result;
			}

	    	/**
             * SIMP_NOT_GE
	    	 * Negation 9: ¬ a ≥ b == a < b
	    	 */
			Not(Ge(a, b)) -> {
				result =  makeRelationalPredicate(Predicate.LT, `a, `b);
	    		trace(predicate, result, "SIMP_NOT_GE");
				return result;
			}

	    	/**
             * SIMP_NOT_GT
	    	 * Negation 10: ¬ a > b == a ≤ b
	    	 */
			Not(Gt(a, b)) -> {
				result =  makeRelationalPredicate(Predicate.LE, `a, `b);
	    		trace(predicate, result, "SIMP_NOT_GT");
				return result;
			}

	    	/**
             * SIMP_NOT_LT
	    	 * Negation 11: ¬ a < b == a ≥ b
	    	 */
			Not(Lt(a, b)) -> {
				result =  makeRelationalPredicate(Predicate.GE, `a, `b);
	    		trace(predicate, result, "SIMP_NOT_LT");
				return result;
			}

	    	/**
             * SIMP_SPECIAL_NOT_EQUAL_FALSE_R
	    	 * Negation 12: ¬ (E = FALSE) == E = TRUE
	    	 */
			Not(Equal(E, FALSE())) -> {
				result =  makeRelationalPredicate(Predicate.EQUAL, `E, Lib.TRUE);
	    		trace(predicate, result, "SIMP_SPECIAL_NOT_EQUAL_FALSE_R");
				return result;
			}

	    	/**
             * SIMP_SPECIAL_NOT_EQUAL_TRUE_R
	    	 * Negation 13: ¬ (E = TRUE) == E = FALSE
	    	 */
			Not(Equal(E, TRUE())) -> {
				result =  makeRelationalPredicate(Predicate.EQUAL, `E, Lib.FALSE);
	    		trace(predicate, result, "SIMP_SPECIAL_NOT_EQUAL_TRUE_R");
				return result;
			}

	    	/**
             * SIMP_SPECIAL_NOT_EQUAL_FALSE_L
	    	 * Negation 14: ¬ (FALSE = E) == TRUE = E
	    	 */
			Not(Equal(FALSE(), E)) -> {
				result =  makeRelationalPredicate(Predicate.EQUAL, Lib.TRUE, `E);
	    		trace(predicate, result, "SIMP_SPECIAL_NOT_EQUAL_FALSE_L");
				return result;
			}

	    	/**
             * SIMP_SPECIAL_NOT_EQUAL_TRUE_L
	    	 * Negation 15: ¬ (TRUE = E) == FALSE = E
	    	 */
			Not(Equal(TRUE(), E)) -> {
				result =  makeRelationalPredicate(Predicate.EQUAL, Lib.FALSE, `E);
	    		trace(predicate, result, "SIMP_SPECIAL_NOT_EQUAL_TRUE_L");
				return result;
			}
			
	    }
	    return predicate;
	}

    @ProverRule({"SIMP_FORALL_AND", "SIMP_EXISTS_OR"}) 
	@Override
	public Predicate rewrite(QuantifiedPredicate predicate) {
		final Predicate result;
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

	    		result = makeAssociativePredicate(Predicate.LAND, predicates);
	    		trace(predicate, result, "SIMP_FORALL_AND");
				return result;
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

	    		result = makeAssociativePredicate(Predicate.LOR, predicates);
	    		trace(predicate, result, "SIMP_EXISTS_OR");
				return result;
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
		final Predicate result;
	    %match (Predicate predicate) {

	    	/**
             * SIMP_MULTI_EQUAL
	    	 * Equality: E = E == ⊤
	    	 */
	    	Equal(E, E) -> {
	    		result = Lib.True;
	    		trace(predicate, result, "SIMP_MULTI_EQUAL");
				return result;
	    	}

	    	/**
             * SIMP_MULTI_NOTEQUAL
	    	 * Equality: E ≠ E == ⊥
	    	 */
	    	NotEqual(E, E) -> {
	    		result = Lib.False;
	    		trace(predicate, result, "SIMP_MULTI_NOTEQUAL");
				return result;
	    	}

			/**
             * SIMP_MULTI_LE
	    	 * Arithmetic: E ≤ E == ⊤
	    	 */
	    	Le(E, E) -> {
				result = Lib.True;
	    		trace(predicate, result, "SIMP_MULTI_LE");
				return result;
	    	}
			
	    	/**
             * SIMP_MULTI_GE
	    	 * Arithmetic: E ≥ E == ⊤
	    	 */
	    	Ge(E, E) -> {
				result = Lib.True;
	    		trace(predicate, result, "SIMP_MULTI_GE");
				return result;
	    	}
			
			/**
             * SIMP_MULTI_LT
	    	 * Arithmetic: E < E == ⊥
	    	 */
	    	Lt(E, E) -> {
				result = Lib.False;
	    		trace(predicate, result, "SIMP_MULTI_LT");
				return result;
	    	}
			
			/**
             * SIMP_MULTI_GE
	    	 * Arithmetic: E > E == ⊥
	    	 */
	    	Gt(E, E) -> {
				result = Lib.False;
	    		trace(predicate, result, "SIMP_MULTI_GE");
				return result;
	    	}
			
			/**
             * SIMP_EQUAL_MAPSTO
	    	 * Equality 3: E ↦ F = G ↦ H == E = G ∧ F = H
	    	 */
	    	Equal(Mapsto(E, F) , Mapsto(G, H)) -> {
	    		Predicate pred1 = makeRelationalPredicate(Expression.EQUAL, `E, `G);
				Predicate pred2 = makeRelationalPredicate(Expression.EQUAL, `F, `H);
				result = makeAssociativePredicate(Predicate.LAND, new Predicate[] {
						pred1, pred2 });
	    		trace(predicate, result, "SIMP_EQUAL_MAPSTO");
				return result;
	    	}
	    	
	    	/**
             * SIMP_SPECIAL_EQUAL_TRUE
	    	 * Equality 4: TRUE = FALSE == ⊥
	    	 */
	    	Equal(TRUE(), FALSE()) -> {
	    		result = Lib.False;
	    		trace(predicate, result, "SIMP_SPECIAL_EQUAL_TRUE");
				return result;
	    	}

	    	/**
             * SIMP_SPECIAL_EQUAL_TRUE
	    	 * Equality 5: FALSE = TRUE == ⊥
	    	 */
	    	Equal(FALSE(), TRUE()) -> {
	    		result = Lib.False;
	    		trace(predicate, result, "SIMP_SPECIAL_EQUAL_TRUE");
				return result;
	    	}

	    	/**
             * SIMP_NOTEQUAL
	    	 * Negation 4: E ≠ F == ¬ E = F
	    	 */
	    	NotEqual(E, F) -> {
	    		result = makeUnaryPredicate(
	    			Predicate.NOT, makeRelationalPredicate(Expression.EQUAL, `E, `F));
	    		trace(predicate, result, "SIMP_NOTEQUAL");
				return result;
	    	}

	    	/**
             * SIMP_NOTIN
	    	 * Negation 5: E ∉ F == ¬ E ∈ F
	    	 */
	    	NotIn(E, F) -> {
	    		result = makeUnaryPredicate(
	    			Predicate.NOT, makeRelationalPredicate(Expression.IN, `E, `F));
	    		trace(predicate, result, "SIMP_NOTIN");
				return result;
	    	}


	    	/**
             * SIMP_NOTSUBSET
	    	 * Negation 6: E ⊄ F == ¬ E ⊂ F
	    	 */
	    	NotSubset(E, F) -> {
	    		result = makeUnaryPredicate(
	    			Predicate.NOT, makeRelationalPredicate(Expression.SUBSET, `E, `F));
	    		trace(predicate, result, "SIMP_NOTSUBSET");
				return result;
	    	}

	    	/**
             * SIMP_NOTSUBSETEQ
	    	 * Negation 7: E ⊈ F == ¬ E ⊆ F
	    	 */
	    	NotSubsetEq(E, F) -> {
	    		result = makeUnaryPredicate(
	    			Predicate.NOT, makeRelationalPredicate(Expression.SUBSETEQ, `E, `F));
	    		trace(predicate, result, "SIMP_NOTSUBSETEQ");
				return result;
	    	}

	    	/**
             * SIMP_SPECIAL_SUBSETEQ
	    	 * Set Theory 5: ∅ ⊆ S == ⊤
	    	 */
	    	SubsetEq(EmptySet(), _) -> {
	    		result = Lib.True;
	    		trace(predicate, result, "SIMP_SPECIAL_SUBSETEQ");
				return result;
	    	}
	    	
	    	/**
             * SIMP_MULTI_SUBSETEQ
	    	 * Set Theory: S ⊆ S == ⊤
	    	 */
	    	SubsetEq(S, S) -> {
	    		result = Lib.True;
	    		trace(predicate, result, "SIMP_MULTI_SUBSETEQ");
				return result;
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
	    		result = makeRelationalPredicate(Predicate.SUBSETEQ, `A, union);
	    		trace(predicate, result, "DERIV_SUBSETEQ_SETMINUS_L");
				return result;
	    	}
	    	
	    	/**
             * SIMP_SUBSETEQ_BUNION
	    	 * Set Theory: S ⊆ A ∪ ... ∪ S ∪ ... ∪ B == ⊤
	    	 */
	    	SubsetEq(S, BUnion(children)) -> {
	    		for (Expression child : `children) {
	    			if (child.equals(`S)) {
	    				result = Lib.True;
	    				trace(predicate, result, "SIMP_SUBSETEQ_BUNION");
	    				return result;
	    			}
	    		}
	    	}
			
	    	/**
             * SIMP_SUBSETEQ_BINTER
	    	 * Set Theory: A ∩ ... ∩ S ∩ ... ∩ B ⊆ S == ⊤
	    	 */
	    	SubsetEq(BInter(children), S) -> {
	    		for (Expression child : `children) {
	    			if (child.equals(`S)) {
	    				result = Lib.True;
	    				trace(predicate, result, "SIMP_SUBSETEQ_BINTER");
	    				return result;
	    			}
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
	    		result = makeAssociativePredicate(Predicate.LAND, newChildren);
	    		trace(predicate, result, "DERIV_SUBSETEQ_BUNION");
				return result;
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
	    		result = makeAssociativePredicate(Predicate.LAND, newChildren);
	    		trace(predicate, result, "DERIV_SUBSETEQ_BINTER");
				return result;
	    	}
			
			/**
             * SIMP_SPECIAL_IN
	    	 * Set Theory 7: E ∈ ∅ == ⊥
	    	 */
	    	In(_, EmptySet()) -> {
	    		result = Lib.False;
	    		trace(predicate, result, "SIMP_SPECIAL_IN");
				return result;
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
						result = Lib.True;
						trace(predicate, result, "SIMP_MULTI_IN");
						return result;
					}
				}
				if (`members.length == 1) {
					result = makeRelationalPredicate(Predicate.EQUAL, `E, `members[0]);
					trace(predicate, result, "SIMP_IN_SING");
					return result;
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
				onePoint.matchAndApply();

				if (onePoint.wasSuccessfullyApplied()) {
					// no need to generate a WD PO for the replacement:
					// it is already generated separately by POM 
					// for the whole predicate
					result = onePoint.getProcessedPredicate();
					trace(predicate, result, "SIMP_IN_COMPSET_ONEPOINT");
					return result;
				} else {
					result = existsPred;
					trace(predicate, result, "SIMP_IN_COMPSET");
					return result;
				}
	    	}
		
			/**
             * SIMP_EQUAL_SING
	    	 * Set Theory 19: {E} = {F} == E = F   if E, F is a single expression
	    	 */
	    	Equal(SetExtension(E), SetExtension(F)) -> {
   				if (`E.length == 1 && `F.length == 1) {
					result = makeRelationalPredicate(Predicate.EQUAL, `E[0], `F[0]);
					trace(predicate, result, "SIMP_EQUAL_SING");
					return result;
				}
				return predicate;
	    	}
	    	
	    	/**
             * SIMP_LIT_EQUAL
	    	 * Arithmetic 16: i = j == ⊤  or  i = j == ⊥ (by computation)
	    	 */
	    	Equal(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		result = `i.equals(`j) ? Lib.True : Lib.False;
	    		trace(predicate, result, "SIMP_LIT_EQUAL");
				return result;
	    	}

	    	/**
             * SIMP_LIT_LE
	    	 * Arithmetic 17: i ≤ j == ⊤  or  i ≤ j == ⊥ (by computation)
	    	 */
	    	Le(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		result = `i.compareTo(`j) <= 0 ? Lib.True : Lib.False;
	    		trace(predicate, result, "SIMP_LIT_LE");
				return result;
	    	}

	    	/**
             * SIMP_LIT_LT
	    	 * Arithmetic 18: i < j == ⊤  or  i < j == ⊥ (by computation)
	    	 */
	    	Lt(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		result = `i.compareTo(`j) < 0 ? Lib.True : Lib.False;
	    		trace(predicate, result, "SIMP_LIT_LT");
				return result;
	    	}

	    	/**
             * SIMP_LIT_GE
	    	 * Arithmetic 19: i ≥ j == ⊤  or  i ≥ j == ⊥ (by computation)
	    	 */
	    	Ge(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		result = `i.compareTo(`j) >= 0 ? Lib.True : Lib.False;
	    		trace(predicate, result, "SIMP_LIT_GE");
				return result;
	    	}

	    	/**
             * SIMP_LIT_GT
	    	 * Arithmetic 20: i > j == ⊤  or  i > j == ⊥ (by computation)
	    	 */
	    	Gt(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		result = `i.compareTo(`j) > 0 ? Lib.True : Lib.False;
	    		trace(predicate, result, "SIMP_LIT_GT");
				return result;
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
	    			result = makeRelationalPredicate(Predicate.EQUAL, `S, emptySet);
	    			trace(predicate, result, "SIMP_SPECIAL_EQUAL_CARD");
	    			return result;
	    		}
	    		else if (`E.equals(number1)) {
	    			result = FormulaUnfold.makeExistSingletonSet(`S);
	    			trace(predicate, result, "SIMP_LIT_EQUAL_CARD_1");
	    			return result;
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
	    			result = makeRelationalPredicate(Predicate.EQUAL, `S, emptySet);
	    			trace(predicate, result, "SIMP_SPECIAL_EQUAL_CARD");
	    			return result;
	    		}
	    		else if (`E.equals(number1)) {
	    			result = FormulaUnfold.makeExistSingletonSet(`S);
	    			trace(predicate, result, "SIMP_LIT_EQUAL_CARD_1");
	    			return result;
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
	    			result = makeUnaryPredicate(Predicate.NOT, equal);
	    			trace(predicate, result, "SIMP_LIT_GT_CARD_0");
	    			return result;
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
	    			result = makeUnaryPredicate(Predicate.NOT, equal);
	    			trace(predicate, result, "SIMP_LIT_LT_CARD_0");
	    			return result;
	    		}
	    	}

	    	/**
             * SIMP_LIT_EQUAL_KBOOL_TRUE
	    	 * Boolean: TRUE = bool(P) == P  
	    	 */
	    	Equal(TRUE(), Bool(P)) -> {
	    		result = `P;
	    		trace(predicate, result, "SIMP_LIT_EQUAL_KBOOL_TRUE");
				return result;
	    	}

	    	/**
             * SIMP_LIT_EQUAL_KBOOL_TRUE
	    	 * Boolean: bool(P) = TRUE == P  
	    	 */
	    	Equal(Bool(P), TRUE()) -> {
	    		result = `P;
	    		trace(predicate, result, "SIMP_LIT_EQUAL_KBOOL_TRUE");
				return result;
	    	}

	    	/**
             * SIMP_LIT_EQUAL_KBOOL_FALSE
	    	 * Boolean: FALSE = bool(P) == ¬P  
	    	 */
	    	Equal(FALSE(), Bool(P)) -> {
	    		result = makeUnaryPredicate(Predicate.NOT, `P);
	    		trace(predicate, result, "SIMP_LIT_EQUAL_KBOOL_FALSE");
				return result;
	    	}

	    	/**
             * SIMP_LIT_EQUAL_KBOOL_FALSE
	    	 * Boolean: bool(P) = FALSE == ¬P  
	    	 */
	    	Equal(Bool(P), FALSE()) -> {
	    		result = makeUnaryPredicate(Predicate.NOT, `P);
	    		trace(predicate, result, "SIMP_LIT_EQUAL_KBOOL_FALSE");
				return result;
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
		final Expression result;
	    %match (Expression expression) {

	    	/**
             * SIMP_SPECIAL_BINTER
	    	 * Set Theory: S ∩ ... ∩ ∅ ∩ ... ∩ T == ∅
	    	 * SIMP_TYPE_BINTER
             * Set Theory: S ∩ ... ∩ U ∩ ... ∩ T == S ∩ ... ∩ ... ∩ T
             * SIMP_MULTI_BINTER
             * Set Theory: S ∩ ... ∩ T ∩ T ∩ ... ∩ V == S ∩ ... ∩ T ∩ ... ∩ V
	    	 */
	    	BInter(children) -> {
	    		result = FormulaSimplification.simplifyAssociativeExpression(expression, `children);
	    		trace(expression, result, "SIMP_SPECIAL_BINTER", "SIMP_TYPE_BINTER", "SIMP_MULTI_BINTER");
				return result;
	    	}

	    	/**
	    	 * SIMP_SPECIAL_BUNION
             * Set Theory: S ∪ ... ∪ ∅ ∪ ... ∪ T == S ∪ ... ∪ T
	    	 * SIMP_TYPE_BUNION
             * Set Theory: S ∪ ... ∪ U ∪ ... ∪ T == U
             * SIMP_MULTI_BUNION
             * Set Theory: S ∪ ... ∪ T ∪ T ∪ ... ∪ V == S ∪ ... ∪ T ∪ ... ∪ V
	    	 */
	    	BUnion(children) -> {
	    		result = FormulaSimplification.simplifyAssociativeExpression(expression, `children);
	    		trace(expression, result, "SIMP_SPECIAL_BUNION", "SIMP_TYPE_BUNION", "SIMP_MULTI_BUNION");
				return result;
	    	}

	    	/**
	    	 * SIMP_SPECIAL_PLUS
             * Arithmetic 1: E + ... + 0 + ... + F == E + ... + ... + F
	    	 */
	    	Plus (children) -> {
	    		result = FormulaSimplification.simplifyAssociativeExpression(expression, `children);
	    		trace(expression, result, "SIMP_SPECIAL_PLUS");
				return result;
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
	    		result = MultiplicationSimplifier.simplify(expression, ff);
	    		trace(expression, result, "SIMP_SPECIAL_PROD_1", "SIMP_SPECIAL_PROD_0", "SIMP_SPECIAL_PROD_MINUS_EVEN", "SIMP_SPECIAL_PROD_MINUS_ODD");
				return result;
	    	}
	    	
	    	/**
	    	 * SIMP_SPECIAL_FCOMP
             * Set Theory: p; ... ;∅; ... ;q == ∅
	    	 */
	    	Fcomp (children) -> {
	    		for (Expression child : `children) {
	    			if (Lib.isEmptySet(child)) {
	    				result = ff.makeEmptySet(expression.getType(), null);
	    	    		trace(expression, result, "SIMP_SPECIAL_FCOMP");
	    				return result;
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
	    				result = ff.makeEmptySet(expression.getType(), null);
	    	    		trace(expression, result, "SIMP_SPECIAL_BCOMP");
	    				return result;
	    			}
	    		}
	    	}
	    	
            /**
             * SIMP_SPECIAL_OVERL
			 * Set theory: r  ...  ∅  ...  s  ==  r  ...  s
			 */
	    	Ovr(children) -> {
	    		result = FormulaSimplification.simplifyAssociativeExpression(expression, `children);
	    		trace(expression, result, "SIMP_SPECIAL_OVERL");
				return result;
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
			"SIMP_FUNIMAGE_CPROD", "SIMP_FUNIMAGE_LAMBDA" })
	@Override
	public Expression rewrite(BinaryExpression expression) {
		final Expression result;
	    %match (Expression expression) {

			/**
             * SIMP_MULTI_SETMINUS
	    	 * Set Theory 11: S ∖ S == ∅
	    	 */
	    	SetMinus(S, S) -> {
	    		result = makeEmptySet(`S.getType());
	    		trace(expression, result, "SIMP_MULTI_SETMINUS");
	    		return result;
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
					result = emptySet;
		    		trace(expression, result, "SIMP_SPECIAL_SETMINUS_L");
		    		return result;
				}
   				if (`T.equals(emptySet)) {
					result = `S;
		    		trace(expression, result, "SIMP_SPECIAL_SETMINUS_R");
		    		return result;
				}
				if (`T.equals(type.getBaseType().toExpression(ff))) {
					result = emptySet;
		    		trace(expression, result, "SIMP_TYPE_SETMINUS");
		    		return result;
				}
	    	}
	    	
			/**
	    	 * SIMP_TYPE_SETMINUS_SETMINUS
             * Set Theory: U ∖ (U ∖ S) == S
	    	 */
			SetMinus(U, SetMinus(U, S)) -> {
				PowerSetType type = (PowerSetType) `S.getType();

				if (`U.equals(type.getBaseType().toExpression(ff))) {
					result = `S;
		    		trace(expression, result, "SIMP_TYPE_SETMINUS_SETMINUS");
		    		return result;
				}
			}
			
			/**
			 * SIMP_MULTI_MINUS
             * Arithmetic: E − E == 0
			 */
			Minus(E, E) -> {
				result = number0;
	    		trace(expression, result, "SIMP_MULTI_MINUS");
	    		return result;
			}

			/**
	    	 * SIMP_SPECIAL_MINUS_R
             * Arithmetic: E − 0 == E
	    	 * SIMP_SPECIAL_MINUS_L
             * Arithmetic: 0 − E == −E
	    	 */
	    	Minus(E, F) -> {
	    		if (`F.equals(number0)) {
					result = `E;
		    		trace(expression, result, "SIMP_SPECIAL_MINUS_R");
		    		return result;
				} else if (`E.equals(number0)) {
					result = makeUnaryExpression(Expression.UNMINUS, `F);
		    		trace(expression, result, "SIMP_SPECIAL_MINUS_L");
		    		return result;
				}
				return expression;
	    	}

			/**
	    	 * SIMP_MULTI_DIV
             * Arithmetic: E ÷ E = 1
	    	 */
	    	Div(E, E) -> {
	    		result = Lib.makeIntegerLiteral(1);
	    		trace(expression, result, "SIMP_MULTI_DIV");
	    		return result;
	    	}

			/**
			 * SIMP_SPECIAL_DIV_1
             * Arithmetic: E ÷ 1 = E
			 */
			Div(E, IntegerLiteral(F)) -> {
				if (`F.equals(BigInteger.ONE)) {
					result = `E;
		    		trace(expression, result, "SIMP_SPECIAL_DIV_1");
		    		return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_DIV_0
             * Arithmetic: 0 ÷ E = 0
			 */
			Div(IntegerLiteral(F), _) -> {
				if (`F.equals(BigInteger.ZERO)) {
					result = number0;
		    		trace(expression, result, "SIMP_SPECIAL_DIV_0");
		    		return result;
				}
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
		    			result = newChildren.iterator().next();
		    		} else {
		    		    result = makeAssociativeExpression(Expression.MUL, newChildren);
		    		}
		    		trace(expression, result, "SIMP_MULTI_DIV_PROD");
		    		return result;
	    		}
	    	}

			/**
	    	 * SIMP_DIV_MINUS
             * Arithmetic: (−E) ÷ (−F) == E ÷ F
	    	 */
	    	Div(UnMinus(E), UnMinus(F)) -> {
	    		result = FormulaSimplification.getFaction(`E, `F);
	    		trace(expression, result, "SIMP_DIV_MINUS");
	    		return result;
	    	}

			/**
	    	 * SIMP_DIV_MINUS
             * Arithmetic: (−E) ÷ (−F) == E ÷ F
	    	 */
	    	Div(UnMinus(E), IntegerLiteral(F)) -> {
	    		result = FormulaSimplification.getFaction(`expression, `E, `F);
	    		trace(expression, result, "SIMP_DIV_MINUS");
	    		return result;
	    	}

			/**
	    	 * SIMP_DIV_MINUS
             * Arithmetic 10: (−E) ÷ (−F) == E ÷ F
	    	 */
	    	Div(IntegerLiteral(E), UnMinus(F)) -> {
	    		result = FormulaSimplification.getFaction(`expression, `E, `F);
	    		trace(expression, result, "SIMP_DIV_MINUS");
	    		return result;
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
	    		result = FormulaSimplification.getFaction(`expression, `E, `F);
	    		trace(expression, result, "SIMP_SPECIAL_DIV_1", "SIMP_SPECIAL_DIV_0", "SIMP_DIV_MINUS");
	    		return result;
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
					result = `E;
		    		trace(expression, result, "SIMP_SPECIAL_EXPN_1_R");
		    		return result;
				} else if (`F.equals(number0)) {
					result = number1;
		    		trace(expression, result, "SIMP_SPECIAL_EXPN_0");
		    		return result;
				} else if (`E.equals(number1)) {
					result = number1;
		    		trace(expression, result, "SIMP_SPECIAL_EXPN_1_L");
		    		return result;
				}
				return expression;
	    	}
	    	
	    	/**
	    	 * SIMP_FUNIMAGE_FUNIMAGE_CONVERSE
             * Set Theory: f(f∼(E)) = E
	    	 */
	    	FunImage(f, FunImage(Converse(f), E)) -> {
				result = `E;
	    		trace(expression, result, "SIMP_FUNIMAGE_FUNIMAGE_CONVERSE");
	    		return result;
	    	}

	    	/**
	    	 * SIMP_FUNIMAGE_CONVERSE_FUNIMAGE
	    	 * Set Theory: f∼(f(E)) = E
	    	 */
	    	FunImage(Converse(f), FunImage(f, E)) -> {
				result = `E;
	    		trace(expression, result, "SIMP_FUNIMAGE_CONVERSE_FUNIMAGE");
	    		return result;
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
								result = ((BinaryExpression) child).getRight();
					    		trace(expression, result, "SIMP_MULTI_FUNIMAGE_OVERL_SETENUM");
					    		return result;
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
				result = `E;
	    		trace(expression, result, "SIMP_FUNIMAGE_FUNIMAGE_CONVERSE_SETENUM");
	    		return result;
	    	}

			/**
			 * SIMP_SPECIAL_RELIMAGE_R
             * Set Theory: r[∅] == ∅
			 */
			RelImage(r, EmptySet()) -> {
				result = makeEmptySet(ff.makePowerSetType(Lib.getRangeType(`r)));
	    		trace(expression, result, "SIMP_SPECIAL_RELIMAGE_R");
	    		return result;
			}

			/**
			 * SIMP_SPECIAL_RELIMAGE_L
             * Set Theory: ∅[A] == ∅
			 */
			RelImage(r@EmptySet(), _) -> {
				result = makeEmptySet(ff.makePowerSetType(Lib.getRangeType(`r)));
	    		trace(expression, result, "SIMP_SPECIAL_RELIMAGE_L");
	    		return result;
			}

			/**
			 * SIMP_FUNIMAGE_CPROD
             * Set Theory: (S × {E})(x) == E
			 */
			FunImage(Cprod(_, SetExtension(children)), _) -> {
				if (`children.length == 1) {
					result = `children[0];
			        trace(expression, result, "SIMP_FUNIMAGE_CPROD");
	    	        return result;
				}
			}

            /**
             * SIMP_FUNIMAGE_LAMBDA
             *
             */
            FunImage(Cset(_,_,Mapsto(_,_)),_) -> {
                result = LambdaComputer.rewrite(expression, ff);
                if (result != null) {
                	trace(expression, result, "SIMP_FUNIMAGE_LAMBDA");
                	return result;
                }
            }
		}
	    return expression;
	}

	@ProverRule( { "SIMP_CONVERSE_CONVERSE", "SIMP_CONVERSE_SETENUM",
			"SIMP_DOM_COMPSET", "SIMP_RAN_COMPSET", "SIMP_MINUS_MINUS",
			"SIMP_SPECIAL_CARD", "SIMP_CARD_SING", "SIMP_CARD_POW",
			"SIMP_CARD_BUNION", "SIMP_SPECIAL_DOM", "SIMP_SPECIAL_RAN" })
	@Override
	public Expression rewrite(UnaryExpression expression) {
		final Expression result;
	    %match (Expression expression) {

			/**
             * SIMP_CONVERSE_CONVERSE
	    	 * Set Theory 14: r∼∼ == r
	    	 */
	    	Converse(Converse(r)) -> {
	    		result = `r;
	    		trace(expression, result, "SIMP_CONVERSE_CONVERSE");
	    		return result;
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

				result = makeSetExtension(newMembers);
	    		trace(expression, result, "SIMP_CONVERSE_SETENUM");
	    		return result;
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

				result = makeSetExtension(domain);
	    		trace(expression, result, "SIMP_DOM_COMPSET");
	    		return result;
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

				result = makeSetExtension(range);
	    		trace(expression, result, "SIMP_RAN_COMPSET");
	    		return result;
	    	}

			/**
	    	 * SIMP_MINUS_MINUS
             * Arithmetic 4: −(−E) = E
	    	 */
	    	UnMinus(UnMinus(E)) -> {
	    		result = `E;
	    		trace(expression, result, "SIMP_MINUS_MINUS");
	    		return result;
	    	}
			
			/**
             * SIMP_SPECIAL_CARD
	    	 * Cardinality: card(∅) == 0
	    	 */
			Card(EmptySet()) -> {
				result = number0;
	    		trace(expression, result, "SIMP_SPECIAL_CARD");
	    		return result;
			}

			/**
             * SIMP_CARD_SING
	    	 * Cardinality: card({E}) == 1
	    	 */
			Card(SetExtension(children)) -> {
				if (`children.length == 1) {
					result = number1;
		    		trace(expression, result, "SIMP_CARD_SING");
		    		return result;
				}
			}

			/**
             * SIMP_CARD_POW
	    	 * Cardinality: card(ℙ(S)) == 2^(card(S))
	    	 */
			Card(Pow(S)) -> {
				Expression cardS = makeUnaryExpression(Expression.KCARD, `S);
				result = makeBinaryExpression(Expression.EXPN, number2, cardS);
	    		trace(expression, result, "SIMP_CARD_POW");
	    		return result;
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
	    		Expression temp = subFormulas[0];
	    		boolean positive = false;
	    		for (int i = 1; i < length; ++i) {
	    			if (positive) {
						Expression [] newChildren = new Expression[2];
						newChildren[0] = temp;
						newChildren[1] = subFormulas[i];
						temp = makeAssociativeExpression(Expression.PLUS,
								newChildren);
	    			}
	    			else {
	    				temp = makeBinaryExpression(Expression.MINUS,
	    						temp, subFormulas[i]);
	    			}
	    			positive = !positive;
	    		}
	    		result = temp;
	    		trace(expression, result, "SIMP_CARD_BUNION");
	    		return result;
	    	}
			
			/**
             * SIMP_SPECIAL_DOM
			 * Set Theory: dom(∅) == ∅
			 */
			Dom(r) -> {
				if (`r.equals(makeEmptySet(`r.getType()))) {
					result = makeEmptySet(ff.makePowerSetType(Lib.getDomainType(`r)));
		    		trace(expression, result, "SIMP_SPECIAL_DOM");
		    		return result;
				}
			}
			
			/**
             * SIMP_SPECIAL_RAN
			 * Set Theory: ran(∅) == ∅
			 */
			Ran(r) -> {
				if (`r.equals(makeEmptySet(`r.getType()))) {
					result = makeEmptySet(ff.makePowerSetType(Lib.getRangeType(`r)));
		    		trace(expression, result, "SIMP_SPECIAL_RAN");
		    		return result;
				}
			}
	    
	    }
	    return expression;
	}

	@ProverRule( { "SIMP_SPECIAL_KBOOL_BFALSE", "SIMP_SPECIAL_KBOOL_BTRUE" }) 
    @Override
	public Expression rewrite(BoolExpression expression) {
		final Expression result; 
	    %match (Expression expression) {
	   		/**
             * SIMP_SPECIAL_KBOOL_BFALSE
	    	 * Set Theory:	bool(⊥) = FALSE
	    	 */
	    	Bool(BFALSE()) -> {
				result = ff.makeAtomicExpression(Expression.FALSE, null);
	    		trace(expression, result, "SIMP_SPECIAL_KBOOL_BFALSE");
	    		return result;
			}

	   		/**
             * SIMP_SPECIAL_KBOOL_BTRUE
	    	 * Set Theory:	bool(⊤) = TRUE  
	    	 */
	    	Bool(BTRUE()) -> {
				result = ff.makeAtomicExpression(Expression.TRUE, null);
	    		trace(expression, result, "SIMP_SPECIAL_KBOOL_BTRUE");
	    		return result;
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
    	final Expression result;
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
				if (newMembers.size() != `members.length) {
					result = makeSetExtension(newMembers);
		    		trace(expression, result, "SIMP_MULTI_SETENUM");
		    		return result;
				}
	    	}
		}
	    return expression;
	}
}
