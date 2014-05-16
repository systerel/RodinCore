/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
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
 *     Systerel - SIMP_EQUAL_CONSTR*, SIMP_DESTR_CONSTR
 *     Systerel - move to tom-2.8
 *     Systerel - extracted this class from AutoRewriterImpl
 *     Systerel - SIMP_MULTI_IMP_NOT_*, SIMP_EXISTS_IMP
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AssociativeSimplification.simplifyLand;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AssociativeSimplification.simplifyLor;
import org.eventb.core.seqprover.eventbExtensions.DLib;


import java.math.BigInteger;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.ProverRule;


/**
 * Implements syntactic simplification of event-B predicates based on some
 * simple rewrite rules.
 */
@SuppressWarnings({"unused", "cast"})
public class PredicateSimplifier extends DefaultRewriter {

	public static final int MULTI_IMP = 1 << 0;
	public static final int MULTI_EQV_NOT = 1 << 1;
	public static final int MULTI_IMP_NOT = 1 << 2;
	public static final int MULTI_IMP_AND = 1 << 3;
	public static final int QUANT_DISTR = 1 << 4;
	public static final int EXISTS_IMP = 1 << 5;
	public static final int MULTI_AND_OR = 1 << 6;

	// true enables trace messages
	protected final boolean debug;
	private final String rewriterName;


	// Enabled options (public for testing purposes only)
	public final boolean withMultiImp;
	public final boolean withMultiImpNot;
	public final boolean withMultiEqvNot;
	public final boolean withMultiImpAnd;
	public final boolean withQuantDistr;
	public final boolean withExistsImp;
	public final boolean withMultiAndOr;

	private static final boolean isSet(int options, int flag) {
		return (options & flag) != 0;
	}

	@ProverRule( { "SIMP_FORALL", "SIMP_EXISTS", "SIMP_LIT_MINUS" })
	/*
	 * Rules SIMP_FORALL, SIMP_EXISTS, and SIMP_LIT_MINUS are implemented by the
	 * fact that this rewriter is auto-flattening (first parameter is true in
	 * the call to the abstract constructor). Unfortunately, it is not possible
	 * to trace auto-flattening.
	 */
	public PredicateSimplifier(int options, boolean debug,
			String rewriterName) {
		super(true);
		this.debug = debug;
		this.withMultiImp = isSet(options, MULTI_IMP);
		this.withMultiEqvNot = isSet(options, MULTI_EQV_NOT);
		this.withMultiImpNot = isSet(options, MULTI_IMP_NOT);
		this.withMultiImpAnd = isSet(options, MULTI_IMP_AND);
		this.withQuantDistr = isSet(options, QUANT_DISTR);
		this.withExistsImp = isSet(options, EXISTS_IMP);
		this.withMultiAndOr = isSet(options, MULTI_AND_OR);
		this.rewriterName = rewriterName;
	}

	protected final <T extends Formula<T>> void trace(T from, T to, String rule,
			String... otherRules) {
		if (!debug) {
			return;
		}
		if (from == to) {
			return;
		}
		final StringBuilder sb = new StringBuilder();
		sb.append(rewriterName);
		sb.append(": ");
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

	protected <T> boolean contains(T[] array, T key) {
		for (T element : array) {
			if (element.equals(key)) {
				return true;
			}
		}
		return false;
	}

	protected AssociativePredicate makeAssociativePredicate(int tag,
			Predicate... children) {
		final FormulaFactory ff = children[0].getFactory();
		return ff.makeAssociativePredicate(tag, children, null);
	}

	protected BinaryPredicate makeBinaryPredicate(int tag, Predicate left, Predicate right) {
		final FormulaFactory ff = left.getFactory();
		return ff.makeBinaryPredicate(tag, left, right, null);
	}

	protected QuantifiedPredicate makeQuantifiedPredicate(int tag,
			BoundIdentDecl[] boundIdentifiers, Predicate child) {
		final FormulaFactory ff = child.getFactory();
		return ff.makeQuantifiedPredicate(tag, boundIdentifiers, child, null);
	}

	private Predicate distributeQuantifier(int tag, BoundIdentDecl[] bids,
			Predicate... children) {
		final int length = children.length;
		final Predicate[] newChildren = new Predicate[length];
		for (int i = 0; i < length; ++i) {
			newChildren[i] = makeQuantifiedPredicate(tag, bids, children[i]);
		}

		return makeAssociativePredicate(tag == FORALL ? LAND : LOR, newChildren);
	}

	%include {FormulaV2.tom}

	@ProverRule( { "SIMP_SPECIAL_AND_BTRUE", "SIMP_SPECIAL_AND_BFALSE",
			"SIMP_MULTI_AND", "SIMP_MULTI_AND_NOT",
			"SIMP_SPECIAL_OR_BTRUE", "SIMP_SPECIAL_OR_BFALSE",
			"SIMP_MULTI_OR", "SIMP_MULTI_OR_NOT" })
	@Override
	public Predicate rewrite(AssociativePredicate predicate) {
		final Predicate result;
		%match (Predicate predicate) {
			/**
			 * SIMP_SPECIAL_AND_BTRUE
			 *    P ∧ ... ∧ ⊤ ∧ ... ∧ Q  == P ∧ ... ∧ Q
			 * SIMP_SPECIAL_AND_BFALSE
			 *    P ∧ ... ∧ ⊥ ∧ ... ∧ Q  == ⊥
			 * SIMP_MULTI_AND
			 *    P ∧ ... ∧ Q ∧ ... ∧ Q ∧ ... ∧ R  == P ∧ ... ∧ Q ∧ ... ∧ R
			 * SIMP_MULTI_AND_NOT
			 *    P ∧ ... ∧ Q ∧ ... ∧ ¬Q ∧ ... ∧ R  == ⊥
			 */
			Land(_) -> {
				result = simplifyLand(predicate, withMultiAndOr);
				trace(predicate, result, "SIMP_SPECIAL_AND_BTRUE",
						"SIMP_SPECIAL_AND_BFALSE", "SIMP_MULTI_AND",
						"SIMP_MULTI_AND_NOT");
				return result;
			}

			/**
			 * SIMP_SPECIAL_OR_BTRUE
			 *    P ⋁ ... ⋁ ⊤ ⋁ ... ⋁ Q  == ⊤
			 * SIMP_SPECIAL_OR_BFALSE
			 *    P ⋁ ... ⋁ ⊥ ⋁ ... ⋁ Q  == P ⋁ ... ⋁ Q
			 * SIMP_MULTI_OR
			 *    P ⋁ ... ⋁ Q ⋁ ... ⋁ Q ⋁ ... ⋁ R  == P ⋁ ... ⋁ Q ⋁ ... ⋁ R
			 * SIMP_MULTI_OR_NOT
			 *    P ⋁ ... ⋁ Q ⋁ ... ⋁ ¬Q ⋁ ... ⋁ R  == P ⋁ ... ⋁ Q ⋁ ... ⋁ R
			 */
			Lor(_) -> {
				result = simplifyLor(predicate, withMultiAndOr);
				trace(predicate, result, "SIMP_SPECIAL_OR_BTRUE",
						"SIMP_SPECIAL_OR_BFALSE", "SIMP_MULTI_OR",
						"SIMP_MULTI_OR_NOT");
				return result;
			}
		}
		return predicate;
	}

	@ProverRule( { "SIMP_SPECIAL_IMP_BTRUE_L", "SIMP_SPECIAL_IMP_BFALSE_L",
			"SIMP_SPECIAL_IMP_BTRUE_R", "SIMP_SPECIAL_IMP_BFALSE_R",
			"SIMP_MULTI_IMP", "SIMP_MULTI_EQV", "SIMP_SPECIAL_EQV_BTRUE",
			"SIMP_SPECIAL_EQV_BFALSE", "SIMP_MULTI_IMP_AND",
			"SIMP_MULTI_IMP_AND_NOT_R", "SIMP_MULTI_IMP_AND_NOT_L",
			"SIMP_MULTI_EQV_NOT", "SIMP_MULTI_IMP_NOT_L",
			"SIMP_MULTI_IMP_NOT_R" })
	@Override
	public Predicate rewrite(BinaryPredicate predicate) {
		final Predicate result;
		FormulaFactory ff = predicate.getFactory();
		%match (Predicate predicate) {
			/**
			 * SIMP_SPECIAL_IMP_BTRUE_L
			 *    ⊤ ⇒ P == P
			 */
			Limp(BTRUE(), P) -> {
				result = `P;
				trace(predicate, result, "SIMP_SPECIAL_IMP_BTRUE_L");
				return result;
			}

			/**
			 * SIMP_SPECIAL_IMP_BFALSE_L
			 *    ⊥ ⇒ P == ⊤
			 */
			Limp(BFALSE(), _) -> {
				result = DLib.True(ff);
				trace(predicate, result, "SIMP_SPECIAL_IMP_BFALSE_L");
				return result;
			}

			/**
			 * SIMP_SPECIAL_IMP_BTRUE_R
			 *    P ⇒ ⊤ == ⊤
			 */
			Limp(_, BTRUE()) -> {
				result = predicate.getRight();
				trace(predicate, result, "SIMP_SPECIAL_IMP_BTRUE_R");
				return result;
			}

			/**
			 * SIMP_SPECIAL_IMP_BFALSE_R
			 *    P ⇒ ⊥ == ¬P
			 */
			Limp(P, BFALSE()) -> {
				result = DLib.makeNeg(`P);
				trace(predicate, result, "SIMP_SPECIAL_IMP_BFALSE_R");
				return result;
			}

			/**
			 * SIMP_MULTI_IMP
			 *    P ⇒ P == ⊤
			 */
			Limp(P, P) -> {
				if (withMultiImp) {
					result = DLib.True(ff);
					trace(predicate, result, "SIMP_MULTI_IMP");
					return result;
				}
			}

			/**
			 * SIMP_SPECIAL_EQV_BTRUE
			 *    P ⇔ ⊤ == P
			 */
			Leqv(P, BTRUE()) -> {
				result = `P;
				trace(predicate, result, "SIMP_SPECIAL_EQV_BTRUE");
				return result;
			}

			/**
			 * SIMP_SPECIAL_EQV_BTRUE
			 *    ⊤ ⇔ P = P
			 */
			Leqv(BTRUE(), P) -> {
				result = `P;
				trace(predicate, result, "SIMP_SPECIAL_EQV_BTRUE");
				return result;
			}

			/**
			 * SIMP_MULTI_EQV
			 *    P ⇔ P == ⊤
			 */
			Leqv(P, P) -> {
				result = DLib.True(ff);
				trace(predicate, result, "SIMP_MULTI_EQV");
				return result;
			}

			/**
			 * SIMP_SPECIAL_EQV_BFALSE
			 *    P ⇔ ⊥ = ¬P
			 */
			Leqv(P, BFALSE()) -> {
				result = DLib.makeNeg(`P);
				trace(predicate, result, "SIMP_SPECIAL_EQV_BFALSE");
				return result;
			}

			/**
			 * SIMP_SPECIAL_EQV_BFALSE
			 *    ⊥ ⇔ P == ¬P
			 */
			Leqv(BFALSE(), P) -> {
				result = DLib.makeNeg(`P);
				trace(predicate, result, "SIMP_SPECIAL_EQV_BFALSE");
				return result;
			}

			/**
			 * SIMP_MULTI_EQV_NOT
			 *     P ⇔ ¬P == ⊥
			 *    ¬P ⇔  P == ⊥
			 */
			Leqv(P, Not(P)) -> {
				if (withMultiEqvNot) {
					result = DLib.False(ff);
					trace(predicate, result, "SIMP_MULTI_EQV_NOT");
					return result;
				}
			}
			Leqv(Not(P), P) -> {
				if (withMultiEqvNot) {
					result = DLib.False(ff);
					trace(predicate, result, "SIMP_MULTI_EQV_NOT");
					return result;
				}
			}

			/**
			 * SIMP_MULTI_IMP_AND
			 *    P ∧ ... ∧ Q ∧ ... ∧ R ⇒ Q == ⊤
			 */
			Limp(Land(pList(_*, Q, _*)), Q) -> {
				if (withMultiImpAnd) {
					result = DLib.True(ff);
					trace(predicate, result, "SIMP_MULTI_IMP_AND");
					return result;
				}
			}

			/**
			 * SIMP_MULTI_IMP_AND_NOT_R
			 *    P ∧ ... ∧ Q ∧ ... ∧ R ⇒ ¬Q == ¬(P ∧ ... ∧ Q ∧ ... ∧ R)
			 *
			 * SIMP_MULTI_IMP_AND_NOT_L
			 *    P ∧ ... ∧ ¬Q ∧ ... ∧ R ⇒ Q == ¬(P ∧ ... ∧ ¬Q ∧ ... ∧ R)
			 */
			Limp(and@Land(children), Q) -> {
				/* Tom-2.8 doc says the following should work:
				 *    Limp(and@Land(pList(_*, nQ, _*)), Q)
				 *    && (nQ << Predicate DLib.makeNeg(Q))
				 * but this raises an internal error in Tom!
				 */
				if (withMultiImpAnd && contains(`children, DLib.makeNeg(`Q))) {
					result = DLib.makeNeg(`and);
					trace(predicate, result, "SIMP_MULTI_IMP_AND_NOT_R",
							"SIMP_MULTI_IMP_AND_NOT_L");
					return result;
				}
			}

			/**
			 * SIMP_MULTI_IMP_NOT_L
			 *    ¬P ⇒  P == P
			 */
			Limp(Not(P), P) -> {
				if (withMultiImpNot) {
					result = `P;
					trace(predicate, result, "SIMP_MULTI_IMP_NOT_L");
					return result;
				}
			}

			/**
			 * SIMP_MULTI_IMP_NOT_R
			 *     P ⇒ ¬P == ¬P
			 */
			Limp(P, notP@Not(P)) -> {
				if (withMultiImpNot) {
					result = `notP;
					trace(predicate, result, "SIMP_MULTI_IMP_NOT_R");
					return result;
				}
			}

		}
		return predicate;
	}

	@ProverRule( { "SIMP_SPECIAL_NOT_BTRUE", "SIMP_SPECIAL_NOT_BFALSE",
			"SIMP_NOT_NOT" })
	@Override
	public Predicate rewrite(UnaryPredicate predicate) {
		FormulaFactory ff = predicate.getFactory();
		final Predicate result;
		%match (Predicate predicate) {
			/**
			 * SIMP_SPECIAL_NOT_BTRUE
			 *    ¬⊤ == ⊥
			 */
			Not(BTRUE()) -> {
				result = DLib.False(ff);
				trace(predicate, result, "SIMP_SPECIAL_NOT_BTRUE");
				return result;
			}

			/**
			 * SIMP_SPECIAL_NOT_BFALSE
			 *    ¬⊥ == ⊤
			 */
			Not(BFALSE()) -> {
				result =  DLib.True(ff);
				trace(predicate, result, "SIMP_SPECIAL_NOT_BFALSE");
				return result;
			}

			/**
			 * SIMP_NOT_NOT
			 *    ¬¬P == P
			 */
			Not(Not(P)) -> {
				result =  `P;
				trace(predicate, result, "SIMP_NOT_NOT");
				return result;
			}
		}
		return predicate;
	}

	@ProverRule({"SIMP_FORALL_AND", "SIMP_EXISTS_OR", "SIMP_EXISTS_IMP"})
	@Override
	public Predicate rewrite(QuantifiedPredicate predicate) {
		final Predicate result;
		%match (Predicate predicate) {
			/**
			 * SIMP_FORALL_AND
			 *    ∀x·P ∧ ... ∧ Q == (∀x·P) ∧ ... ∧ (∀x·Q)
			 */
			ForAll(bids, Land(children)) -> {
				if (withQuantDistr) {
					result = distributeQuantifier(FORALL, `bids, `children);
					trace(predicate, result, "SIMP_FORALL_AND");
					return result;
				}
			}

			/**
			 * SIMP_EXISTS_OR
			 *    ∃x·P ⋁ ... ⋁ Q == (∃x·P) ⋁ ... ⋁ (∃x·Q)
			 */
			Exists(bids, Lor(children)) -> {
				if (withQuantDistr) {
					result = distributeQuantifier(EXISTS, `bids, `children);
					trace(predicate, result, "SIMP_EXISTS_OR");
					return result;
				}
			}

			/**
			 * SIMP_EXISTS_IMP
			 *    ∃x·P ⇒ Q == (∃x·P) ⇒ (∃x·Q)
			 */
			Exists(bids, Limp(P, Q)) -> {
				if (withExistsImp) {
					final Predicate left =
							makeQuantifiedPredicate(FORALL, `bids, `P);
					final Predicate right =
							makeQuantifiedPredicate(EXISTS, `bids, `Q);
					result = makeBinaryPredicate(LIMP, left, right);
					trace(predicate, result, "SIMP_EXISTS_IMP");
					return result;
				}
			}
		}
		return predicate;
	}

}
