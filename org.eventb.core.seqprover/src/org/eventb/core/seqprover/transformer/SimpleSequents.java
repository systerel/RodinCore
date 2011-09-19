/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
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
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.PredicateSimplifier.MULTI_IMP_NOT;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.PredicateSimplifier.MULTI_IMP_AND;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.PredicateSimplifier.QUANT_DISTR;
import static org.eventb.internal.core.seqprover.transformer.TrackedPredicate.makeGoal;
import static org.eventb.internal.core.seqprover.transformer.TrackedPredicate.makeHyp;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
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
	 * 
	 * @param hypotheses
	 *            sequent hypotheses, can be or contain <code>null</code>
	 * @param goal
	 *            sequent goal, can be <code>null</code>
	 * @param factory
	 *            formula factory for the given predicates
	 * @return a new simple sequent with the given hypotheses and goal
	 */
	public static ISimpleSequent make(Iterable<Predicate> hypotheses,
			Predicate goal, FormulaFactory factory) {
		final List<TrackedPredicate> preds = makeTrackedPredicates(hypotheses,
				goal);
		final TrackedPredicate trivial = getTrivial(preds);
		if (trivial != null) {
			return new SimpleSequent(factory, trivial);
		}
		return new SimpleSequent(factory, preds);
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
	 * @return a new simple sequent with the given hypotheses and goal
	 */
	public static ISimpleSequent make(Predicate[] hypotheses, Predicate goal,
			FormulaFactory factory) {
		final List<Predicate> list;
		if (hypotheses == null) {
			list = emptyList();
		} else {
			list = asList(hypotheses);
		}
		return make(list, goal, factory);
	}

	private static List<TrackedPredicate> makeTrackedPredicates(
			Iterable<Predicate> hypotheses, Predicate goal) {
		final List<TrackedPredicate> preds = new ArrayList<TrackedPredicate>();
		if (hypotheses != null) {
			for (Predicate hyp : hypotheses) {
				if (hyp != null) {
					preds.add(makeHyp(hyp));
				}
			}
		}
		if (goal != null) {
			preds.add(makeGoal(goal));
		}
		return preds;
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
	 * The simplification is performed by applying repeatedly the following
	 * rules to every predicate of the sequent until reaching a fix-point:
	 * <ul>
	 * <li>SIMP_SPECIAL_AND_BTRUE</li>
	 * <li>SIMP_SPECIAL_AND_BFALSE</li>
	 * <li>SIMP_SPECIAL_OR_BTRUE</li>
	 * <li>SIMP_SPECIAL_OR_BFALSE</li>
	 * <li>SIMP_SPECIAL_IMP_BTRUE_R</li>
	 * <li>SIMP_SPECIAL_IMP_BTRUE_L</li>
	 * <li>SIMP_SPECIAL_IMP_BFALSE_R</li>
	 * <li>SIMP_SPECIAL_IMP_BFALSE_L</li>
	 * <li>SIMP_SPECIAL_NOT_BTRUE</li>
	 * <li>SIMP_SPECIAL_NOT_BFALSE</li>
	 * <li>SIMP_NOT_NOT</li>
	 * <li>SIMP_MULTI_EQV</li>
	 * <li>SIMP_SPECIAL_EQV_BTRUE</li>
	 * <li>SIMP_SPECIAL_EQV_BFALSE</li>
	 * <li>SIMP_FORALL</li>
	 * <li>SIMP_EXISTS</li>
	 * <li>SIMP_LIT_MINUS</li>
	 * </ul>
	 * Note: The last rule is present only for technical reason.
	 * </p>
	 * <p>
	 * Options can be passed to also apply some additional simplification rules.
	 * </p>
	 * 
	 * @param sequent
	 *            sequent to simplify
	 * @param options
	 *            simplification options
	 * @return a simplified sequent equivalent to the given one
	 */
	public static final ISimpleSequent simplify(ISimpleSequent sequent,
			SimplificationOption... options) {
		final FormulaFactory factory = sequent.getFormulaFactory();
		int flags = 0;
		for (SimplificationOption option : options) {
			flags |= option.flags;
		}
		return sequent.apply(new SequentSimplifier(factory, flags));
	}

	private SimpleSequents() {
		// singleton class
	}

}
