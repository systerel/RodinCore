/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.transformer;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.PredicateSimplifier.EXISTS_IMP;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.PredicateSimplifier.MULTI_AND_OR;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.PredicateSimplifier.MULTI_EQV_NOT;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.PredicateSimplifier.MULTI_IMP;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.PredicateSimplifier.MULTI_IMP_AND;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.PredicateSimplifier.MULTI_IMP_NOT;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.PredicateSimplifier.QUANT_DISTR;
import static org.eventb.internal.core.seqprover.transformer.TrackedPredicate.makeGoal;
import static org.eventb.internal.core.seqprover.transformer.TrackedPredicate.makeHyp;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.seqprover.transformer.LanguageFilter;
import org.eventb.internal.core.seqprover.transformer.SequentDatatypeTranslator;
import org.eventb.internal.core.seqprover.transformer.SequentSimplifier;
import org.eventb.internal.core.seqprover.transformer.SimpleSequent;
import org.eventb.internal.core.seqprover.transformer.TrackedPredicate;

/**
 * Common protocol for creating and transforming simple sequents.
 * 
 * @see ISimpleSequent
 * @author Laurent Voisin
 * @since 2.3
 * @noextend This class is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class SimpleSequents {

	/**
	 * Returns a new simple sequent created with the given predicates. All
	 * non-null predicates must be type-checked. Null predicates will be
	 * ignored.
	 * <p>
	 * All predicates must have been built with the given formula factory.
	 * </p>
	 * 
	 * @param hypotheses
	 *            sequent hypotheses, can be or contain <code>null</code>
	 * @param goal
	 *            sequent goal, can be <code>null</code>
	 * @param factory
	 *            formula factory for the given predicates
	 * @param origin
	 *            origin of the sequent
	 * @return a new simple sequent with the given hypotheses and goal
	 * @throw IllegalArgumentException if some given predicate was built with
	 *        another factory
	 * @since 2.4
	 */
	public static ISimpleSequent make(Iterable<Predicate> hypotheses,
			Predicate goal, FormulaFactory factory, Object origin) {
		final List<TrackedPredicate> preds = makeTrackedPredicates(hypotheses,
				goal, factory);
		final TrackedPredicate trivial = getTrivial(preds);
		if (trivial != null) {
			return new SimpleSequent(factory, trivial, origin);
		}
		return new SimpleSequent(factory, preds, origin);
	}

	/**
	 * Returns a new simple sequent created with the given predicates. All
	 * non-null predicates must be type-checked. Null predicates will be
	 * ignored.
	 * <p>
	 * All predicates must have been built with the given formula factory.
	 * </p>
	 * 
	 * @param hypotheses
	 *            sequent hypotheses, can be or contain <code>null</code>
	 * @param goal
	 *            sequent goal, can be <code>null</code>
	 * @param factory
	 *            formula factory for the given predicates
	 * @return a new simple sequent with the given hypotheses and goal
	 * @throw IllegalArgumentException if some given predicate was built with
	 *        another factory
	 */
	public static ISimpleSequent make(Iterable<Predicate> hypotheses,
			Predicate goal, FormulaFactory factory) {
		return make(hypotheses, goal, factory, null);
	}

	/**
	 * Returns a new simple sequent created with the given predicates. All
	 * non-null predicates must be type-checked. Null predicates will be
	 * ignored.
	 * 
	 * @param hypotheses
	 *            sequent hypotheses, can be or contain <code>null</code>
	 * @param goal
	 *            sequent goal, can be <code>null</code>
	 * @param factory
	 *            formula factory for the given predicates
	 * @param origin
	 *            the origin of the sequent
	 * @return a new simple sequent with the given hypotheses and goal
	 * @throw IllegalArgumentException if some given predicate was built with
	 *        another factory
	 * @since 2.4
	 */
	public static ISimpleSequent make(Predicate[] hypotheses, Predicate goal,
			FormulaFactory factory, Object origin) {
		final List<Predicate> list;
		if (hypotheses == null) {
			list = emptyList();
		} else {
			list = asList(hypotheses);
		}
		return make(list, goal, factory, origin);
	}

	/**
	 * Returns a new simple sequent created with the given predicates. All
	 * non-null predicates must be type-checked. Null predicates will be
	 * ignored.
	 * <p>
	 * All predicates must have been built with the given formula factory.
	 * </p>
	 * 
	 * @param hypotheses
	 *            sequent hypotheses, can be or contain <code>null</code>
	 * @param goal
	 *            sequent goal, can be <code>null</code>
	 * @param factory
	 *            formula factory for the given predicates
	 * @return a new simple sequent with the given hypotheses and goal
	 * @throw IllegalArgumentException if some given predicate was built with
	 *        another factory
	 */
	public static ISimpleSequent make(Predicate[] hypotheses, Predicate goal,
			FormulaFactory factory) {
		return make(hypotheses, goal, factory, null);
	}

	private static List<TrackedPredicate> makeTrackedPredicates(
			Iterable<Predicate> hypotheses, Predicate goal,
			FormulaFactory factory) {
		final List<TrackedPredicate> preds = new ArrayList<TrackedPredicate>();
		if (hypotheses != null) {
			for (Predicate hyp : hypotheses) {
				if (hyp != null) {
					checkFactory(hyp, factory);
					preds.add(makeHyp(hyp));
				}
			}
		}
		if (goal != null) {
			checkFactory(goal, factory);
			preds.add(makeGoal(goal));
		}
		return preds;
	}

	private static void checkFactory(Predicate hyp, FormulaFactory factory) {
		if (factory != hyp.getFactory()) {
			throw new IllegalArgumentException(
					"Invalid factory for predicate: " + hyp);
		}
	}

	private static TrackedPredicate getTrivial(List<TrackedPredicate> preds) {
		for (TrackedPredicate pred : preds) {
			if (pred.holdsTrivially()) {
				return pred;
			}
		}
		return null;
	}

	/**
	 * Sequent simplification options.
	 * 
	 * @see SimpleSequents#simplify(ISimpleSequent, SimplificationOption...)
	 */
	public static enum SimplificationOption {

		/**
		 * Use additional simplification rules that provide more aggressive
		 * simplification, at the expense of computation time. The additional
		 * rules are:
		 * <ul>
		 * <li>SIMP_MULTI_AND</li>
		 * <li>SIMP_MULTI_AND_NOT</li>
		 * <li>SIMP_MULTI_OR</li>
		 * <li>SIMP_MULTI_OR_NOT</li>
		 * <li>SIMP_MULTI_IMP</li>
		 * <li>SIMP_MULTI_IMP_NOT_L</li>
		 * <li>SIMP_MULTI_IMP_NOT_R</li>
		 * <li>SIMP_MULTI_EQV_NOT</li>
		 * <li>SIMP_MULTI_IMP_AND</li>
		 * <li>SIMP_MULTI_IMP_AND_NOT_R</li>
		 * <li>SIMP_MULTI_IMP_AND_NOT_L</li>
		 * <li>SIMP_FORALL_AND</li>
		 * <li>SIMP_EXISTS_OR</li>
		 * <li>SIMP_EXISTS_IMP</li>
		 * </ul>
		 */
		aggressiveSimplification(MULTI_AND_OR | MULTI_IMP | MULTI_IMP_NOT
				| MULTI_EQV_NOT | MULTI_IMP_AND | QUANT_DISTR | EXISTS_IMP);

		final int flags;

		private SimplificationOption(int flags) {
			this.flags = flags;
		}

	}

	/**
	 * Simplifies logically the given sequent. The returned sequent is
	 * equivalent to the input sequent, albeit possibly simpler.
	 * <p>
	 * The simplification is performed by applying to each predicate of the
	 * sequent the simplifier obtained from
	 * {@link PredicateTransformers#makeSimplifier(SimplificationOption...)}
	 * .
	 * 
	 * @param sequent
	 *            sequent to simplify
	 * @param options
	 *            simplification options
	 * @return a simplified sequent equivalent to the given one
	 */
	public static ISimpleSequent simplify(ISimpleSequent sequent,
			SimplificationOption... options) {
		return sequent.apply(new SequentSimplifier(options));
	}

	/**
	 * Filters the given sequent by removing all predicates that contain a
	 * mathematical extension or an operator with one of the given tags.
	 * <p>
	 * Predicates that contain a mathematical extension are predicates that
	 * contain either an extended expression, or an extended predicate operator,
	 * or an expression whose type contains a parametric type.
	 * </p>
	 * <p>
	 * The result sequent is associated to the default formula factory, rather
	 * than the formula factory of the original sequent to reflect the language
	 * restriction enforced by this filter.
	 * </p>
	 * 
	 * @param sequent
	 *            sequent to simplify
	 * @param tags
	 *            tags of additional core operators to filter out
	 * @return a sequent stronger than the given one, restricted to the core
	 *         mathematical language and that do not contain the given operators
	 * @see FormulaFactory#getDefault()
	 */
	public static ISimpleSequent filterLanguage(ISimpleSequent sequent,
			int... tags) {
		return sequent.apply(new LanguageFilter(tags));
	}

	/**
	 * Translates the datatypes that occur in the given sequent to pure set
	 * theory.
	 * <p>
	 * The result sequent is associated to the formula factory obtained from the
	 * original sequent after all datatype extensions have been removed. Any
	 * other mathematical extension is retained.
	 * </p>
	 * 
	 * @param sequent
	 *            sequent to translate
	 * @return a sequent equivalent to the given one, where all datatypes have
	 *         been translated to pure set theory
	 * @see FormulaFactory#
	 * @since 2.6
	 */
	public static ISimpleSequent translateDatatypes(ISimpleSequent sequent) {
		final ITypeEnvironment typenv = sequent.getTypeEnvironment();
		return sequent.apply(new SequentDatatypeTranslator(typenv));
	}

	private SimpleSequents() {
		// singleton class
	}

}
