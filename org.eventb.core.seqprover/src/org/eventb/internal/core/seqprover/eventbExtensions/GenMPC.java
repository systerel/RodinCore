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
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.NOT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.DefaultInspector;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.DLib;

/**
 * @author Emmanuel Billaud
 */
public class GenMPC {

	/**
	 * Returns a set of the sequent's hypotheses. If one is tagged
	 * <code>NOT</code>, then the set contains its child.
	 * 
	 * @param seq
	 *            the sequent from whom the hypotheses are taken
	 * @return a set of the hypotheses of the sequent (no negative predicates
	 *         allowed)
	 */
	public static Set<Predicate> createHypSet(IProverSequent seq) {
		Set<Predicate> hypSet = new HashSet<Predicate>();
		for (Predicate hyp : seq.hypIterable()) {
			addToSet(hypSet, computePred(hyp));
		}
		return hypSet;
	}

	/**
	 * Add to a set a predicate if and only if it is different from
	 * <code>⊤</code> and <code>⊥</code>
	 * 
	 * @param hypSet
	 *            the set where we want to add <code>pred</code>
	 * @param pred
	 *            the predicate possibly added to <code>hypSet</code>
	 */
	private static void addToSet(Set<Predicate> hypSet, Predicate pred) {
		if (!isTrueOrFalsePred(pred)) {
			hypSet.add(pred);
		}
	}

	/**
	 * Compute the predicate to stored in the set <code>hypSet</code>.
	 * 
	 * @param hyp
	 *            the hypothesis parsed (should not be <code>⊤</code> or
	 *            <code>⊥</code>).
	 * @return <code>hyp</code> if it is not a negation, its child else.
	 */
	private static Predicate computePred(Predicate hyp) {
		if (hyp.getTag() == NOT) {
			return ((UnaryPredicate) hyp).getChild();
		} else {
			return hyp;
		}
	}

	/**
	 * Returns <code>true</code> if and only if the predicate <code>pred</code>
	 * is equal to <code>⊤</code> or <code>⊥</code>.
	 * 
	 * @param pred
	 *            the predicate parsed.
	 * @return <code>true</code> if and only if the predicate <code>pred</code>
	 *         is equal to <code>⊤</code> or <code>⊥</code>.
	 */
	private static boolean isTrueOrFalsePred(Predicate pred) {
		return pred.getTag() == BFALSE || pred.getTag() == BTRUE;
	}

	/**
	 * Returns a map of (Predicate ↦ List of IPositions) where Predicate is a
	 * sub-predicate contained both in <code>pred</code> and
	 * <code>hypSet\{hyp}</code> and the List of IPositions is a list of
	 * positions where Predicate occurs in <code>pred</code>.
	 * 
	 * @param pred
	 *            the predicate analyzed (should be either a hypothesis or a
	 *            goal)
	 * @param hypSet
	 *            the set reference of hypotheses
	 * @return the map (predicate contained both in <code>origin</code> and
	 *         <code>hypSet</code> ↦ its position in <code>pred</code>)
	 */
	public static Map<Predicate, List<IPosition>> analyzePred(Predicate pred,
			Set<Predicate> hypSet) {
		Map<Predicate, List<IPosition>> map = new HashMap<Predicate, List<IPosition>>();
		analyzeSubPred(pred, hypSet, map);
		return map;
	}

	/**
	 * Record in <code>map</code> all the sub-predicate of <code>origin</code>
	 * contained in <code>hypSet</code>, as well as their position in
	 * <code>origin</code>. If A sub-predicate is recorded, its children are not
	 * analyzed.
	 * 
	 * @param origin
	 *            the predicate (hypothesis or goal) analyzed
	 * @param hypSet
	 *            the set reference of hypotheses
	 * @param map
	 *            the map (predicate contained both in <code>origin</code> and
	 *            <code>hypSet</code> ↦ its position in <code>origin</code>)
	 */
	public static void analyzeSubPred(final Predicate origin,
			final Set<Predicate> hypSet,
			final Map<Predicate, List<IPosition>> map) {

		origin.inspect(new DefaultInspector<Predicate>() {

			private void addPredToMap(
					final Map<Predicate, List<IPosition>> map,
					final Set<Predicate> hypSet, Predicate predicate,
					IAccumulator<Predicate> accumulator) {

				if (isTrueOrFalsePred(predicate)
						|| predicate == computePred(origin)) {
					return;
				}
				if (hypSet.contains(predicate)) {
					if (!map.containsKey(predicate)) {
						map.put(predicate, new ArrayList<IPosition>());
					}
					map.get(predicate).add(accumulator.getCurrentPosition());
					accumulator.skipChildren();
				}
			}

			@Override
			public void inspect(AssociativePredicate predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, hypSet, predicate, accumulator);
			}

			@Override
			public void inspect(BinaryPredicate predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, hypSet, predicate, accumulator);
			}

			@Override
			public void inspect(ExtendedPredicate predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, hypSet, predicate, accumulator);
			}

			@Override
			public void inspect(LiteralPredicate predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, hypSet, predicate, accumulator);
			}

			@Override
			public void inspect(MultiplePredicate predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, hypSet, predicate, accumulator);
			}

			@Override
			public void inspect(PredicateVariable predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, hypSet, predicate, accumulator);
			}

			@Override
			public void inspect(QuantifiedPredicate predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, hypSet, predicate, accumulator);
			}

			@Override
			public void inspect(RelationalPredicate predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, hypSet, predicate, accumulator);
			}

			@Override
			public void inspect(SimplePredicate predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, hypSet, predicate, accumulator);
			}

			@Override
			public void inspect(UnaryPredicate predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, hypSet, predicate, accumulator);
			}

		});
	}

	/**
	 * Returns the goal re-written using the generalized Modus Ponens and add in
	 * neededHyps all the hypotheses needed to achieve it.
	 * 
	 * @param goal
	 *            the goal to be re-written
	 * @param seq
	 *            the sequent containing the goal
	 * @param modifGoalMap
	 *            the set of all the sub-predicate of the goal that can be
	 *            substitute by <code>⊤</code> or <code>⊥</code>
	 * @param neededHyps
	 *            the set of all the needed hypotheses to rewrite the goal
	 *            (computed by this method, it should be an empty set)
	 * @return the goal re-written
	 */
	public static Predicate rewriteGoal(Predicate goal, IProverSequent seq,
			Map<Predicate, List<IPosition>> modifGoalMap,
			Set<Predicate> neededHyps) {
		final FormulaFactory ff = seq.getFormulaFactory();
		final DLib lib = DLib.mDLib(ff);
		Predicate rewriteGoal = goal;
		for (Entry<Predicate, List<IPosition>> entry : modifGoalMap.entrySet()) {
			final Predicate value = entry.getKey();
			final Predicate negValue = ff.makeUnaryPredicate(NOT, value, null);
			final Predicate substitute;
			if (seq.containsHypothesis(value)) {
				substitute = lib.True();
				neededHyps.add(value);
			} else if (seq.containsHypothesis(negValue)) {
				substitute = lib.False();
				neededHyps.add(negValue);
			} else {
				continue;
			}

			for (IPosition pos : entry.getValue()) {
				rewriteGoal = Rewrite(rewriteGoal, value, pos, substitute, ff);
			}
		}

		if (rewriteGoal != goal)
			return rewriteGoal;
		return null;

	}

	/**
	 * Returns a list of IHypActions needed to complete the re-writing done by
	 * the generalized Modus Ponens.
	 * 
	 * @param seq
	 *            the sequent on which the generalized Modus Ponens is applied
	 * @param modifHypMap
	 *            the map (hypothesis ↦ set of its child that can be substitute
	 *            <code>⊤</code> or <code>⊥</code>) used for the re-writing
	 * @return a list of IHypActions needed to complete the re-writing done by
	 *         the generalized Modus Ponens
	 */
	public static List<IHypAction> rewriteHyps(IProverSequent seq,
			Map<Predicate, Map<Predicate, List<IPosition>>> modifHypMap) {
		final List<IHypAction> hypActions = new ArrayList<IHypAction>();
		final FormulaFactory ff = seq.getFormulaFactory();
		final DLib lib = DLib.mDLib(ff);

		for (Entry<Predicate, Map<Predicate, List<IPosition>>> entryMap : modifHypMap
				.entrySet()) {
			Set<Predicate> inferredHyps = new HashSet<Predicate>();
			Set<Predicate> sourceHyps = new LinkedHashSet<Predicate>();
			final Map<Predicate, List<IPosition>> maps = entryMap.getValue();
			final Predicate hyp = entryMap.getKey();
			Predicate rewriteHyp = hyp;
			for (Entry<Predicate, List<IPosition>> entryPos : maps.entrySet()) {
				final Predicate pred = entryPos.getKey();
				final Predicate negPred = ff
						.makeUnaryPredicate(NOT, pred, null);
				final Predicate substitute;
				if (seq.containsHypothesis(pred)) {
					sourceHyps.add(pred);
					substitute = lib.True();
				} else if (seq.containsHypothesis(negPred)) {
					sourceHyps.add(negPred);
					substitute = lib.False();
				} else {
					continue;
				}
				for (IPosition pos : entryPos.getValue()) {
					rewriteHyp = Rewrite(rewriteHyp, pred, pos, substitute, ff);
				}
			}
			if (rewriteHyp != hyp) {
				inferredHyps = Collections.singleton(rewriteHyp);
			} else {
				continue;
			}
			sourceHyps.add(hyp);
			hypActions.add(ProverFactory.makeForwardInfHypAction(sourceHyps,
					inferredHyps));
			hypActions.add(ProverFactory.makeHideHypAction(Collections
					.singleton(hyp)));
		}
		return hypActions;
	}

	/**
	 * Re-write a predicate.
	 * 
	 * @param pred
	 *            the predicate to be re-written
	 * @param replaced
	 *            the sub-predicate to be replaced
	 * @param pos
	 *            the position of the sub-predicate to be replaced
	 * @param substitute
	 *            the substitute of the predicate to be replace
	 * @param ff
	 *            the formula factory of the sequent containing the predicate
	 * @return the re-written predicate with the parameters given her-above if
	 *         the sub-formula of <code>pred</code> at the position
	 *         <code>pos</code> is equal to the predicate <code>replaced</code>,
	 *         <code>pred</code> else.
	 */
	private static Predicate Rewrite(Predicate pred, Predicate replaced,
			IPosition pos, Predicate substitute, FormulaFactory ff) {
		if (!pred.getSubFormula(pos).equals(replaced)) {
			return pred;
		}
		return pred.rewriteSubFormula(pos, substitute, ff);
	}

}
