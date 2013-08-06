/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.eventbExtensions.Lib.isDisj;
import static org.eventb.core.seqprover.eventbExtensions.Lib.disjuncts;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isNeg;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isFalse;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isTrue;
import static org.eventb.core.seqprover.eventbExtensions.DLib.makeNeg;
import static org.eventb.core.seqprover.eventbExtensions.DLib.True;
import static org.eventb.core.seqprover.eventbExtensions.DLib.False;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.internal.core.seqprover.eventbExtensions.GeneralizedModusPonens.Level;

/**
 * @author Emmanuel Billaud
 */
public class GenMPC {

	private final IProverSequent seq;
	private final FormulaFactory ff;
	private final Predicate goal;
	
	// The goal broken down in possible disjuncts.
	private final Set<Predicate> goalDisjuncts;

	private final Level level;
	private final IProofMonitor pm;
	// the map (hypothesis ↦ set of its child that can be substitute
	// <code>⊤</code> or <code>⊥</code>) used for the re-writing
	private final Map<Predicate, Map<Predicate, List<IPosition>>> modifHypMap;
	// the set of all the sub-predicate of the goal that can be
	// substitute by <code>⊤</code> or <code>⊥</code>
	private Map<Predicate, List<IPosition>> modifGoalMap;
	private Set<Predicate> goalToHypSet;
	private Predicate rewrittenGoal;
	private RewriteHypsOutput output;
	// the set of all the needed hypotheses to rewrite the goal
	// (computed by this method, it should be an empty set)
	private final Set<Predicate> neededHyps;

	public GenMPC(Level level, IProofMonitor pm, IProverSequent seq) {
		super();
		this.level = level;
		this.pm = pm;
		this.seq = seq;
		ff = seq.getFormulaFactory();
		neededHyps = new HashSet<Predicate>();
		modifHypMap = new HashMap<Predicate, Map<Predicate, List<IPosition>>>();
		modifGoalMap = new HashMap<Predicate, List<IPosition>>();
		goal = seq.goal();
		goalDisjuncts = breakPossibleDisjunct(goal);
	}

	public Predicate goal() {
		return goal;
	}

	public Set<Predicate> neededHyps() {
		return neededHyps;
	}

	public Predicate rewrittenGoal() {
		return rewrittenGoal;
	}

	public RewriteHypsOutput output() {
		return output;
	}

	/**
	 * Runs generalized Modus Ponens reasoner that generate the re-written goal
	 * and hypotheses and all the hypotheses needed to achieve it and all
	 * IHypActions computed.
	 *
	 * Computes each sub-predicate which will be re-written by ⊤ (respectively ⊥)
	 * in the goal in a first time and in hypotheses in a second time. Stocks
	 * in a Map the sub-predicate which will be re-written and its position.
	 * Rewrites the goal and the hypotheses
	 *
	 */
	public boolean runGenMP() {
		Set<Predicate> hypSet = createHypSet();
		goalToHypSet = createGoalToHypSet();
		goalToHypSet.addAll(hypSet);

		Map<Predicate, List<IPosition>> m = analyzePred(goal, hypSet);
		if (m != null) {
			modifGoalMap = m;
		}
		for (Predicate hyp : seq.visibleHypIterable()) {
			if (pm != null && pm.isCanceled()) {
				return false;
			}
			m = analyzePred(hyp, goalToHypSet);
			if (!m.isEmpty()) {
				modifHypMap.put(hyp, m);
			}
		}

		rewrittenGoal = rewriteGoal();
		output = rewriteHyps();
		return true;
	}

	/**
	 * Returns a set of the sequent's hypotheses. If one is tagged
	 * <code>NOT</code>, then the set contains its child.
	 * 
	 * @return a set of the hypotheses of the sequent (no negative predicates
	 *         allowed)
	 */
	private Set<Predicate> createHypSet() {
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
	private void addToSet(Set<Predicate> hypSet, Predicate pred) {
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
	 * @return <code>hyp</code> if it is not a negation, <code>¬hyp</code> else.
	 */
	private Predicate computePred(Predicate hyp) {
		if (isNeg(hyp)) {
			return (makeNeg(hyp));
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
	public boolean isTrueOrFalsePred(Predicate pred) {
		return isFalse(pred) || isTrue(pred);
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
	public Map<Predicate, List<IPosition>> analyzePred(Predicate pred,
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
	public void analyzeSubPred(final Predicate origin,
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
	 * @return the goal re-written, or <code>null</code> if it has not been
	 *         re-written
	 */
	public Predicate rewriteGoal() {
		Predicate rewriteGoal = goal;
		for (Entry<Predicate, List<IPosition>> entry : modifGoalMap.entrySet()) {
			final Predicate value = entry.getKey();
			final Predicate[] substitute = computeSubstitutionForGoal(value);
			if (substitute == null) {
				continue;
			}
			neededHyps.add(substitute[0]);
			for (IPosition pos : entry.getValue()) {
				rewriteGoal = Rewrite(rewriteGoal, value, pos, substitute[1]);
			}
		}

		if (rewriteGoal != goal) {
			return rewriteGoal;
		}
		return null;

	}

	/**
	 * Returns a list of IHypActions needed to complete the re-writing done by
	 * the generalized Modus Ponens.
	 * 
	 * @return a list of IHypActions needed to complete the re-writing done by
	 *         the generalized Modus Ponens, as well as a boolean telling
	 *         whether the reasoner is goal dependent or not.
	 */
	public RewriteHypsOutput rewriteHyps() {
		boolean isGoalDependent = false;
		List<IHypAction> hypActions = new ArrayList<IHypAction>();
		for (Entry<Predicate, Map<Predicate, List<IPosition>>> entryMap : modifHypMap
				.entrySet()) {
			Set<Predicate> inferredHyps = new HashSet<Predicate>();
			Set<Predicate> sourceHyps = new LinkedHashSet<Predicate>();
			final Map<Predicate, List<IPosition>> maps = entryMap.getValue();
			final Predicate hyp = entryMap.getKey();
			Predicate rewriteHyp = hyp;
			for (Entry<Predicate, List<IPosition>> entryPos : maps.entrySet()) {
				final Predicate pred = entryPos.getKey();
				final Predicate[] substitution = computeSubstitutionForHyp(pred);
				if (substitution == null) {
					continue;
				}
				final Predicate hypOrGoal = substitution[0];
				if (hypOrGoal == null) {
					isGoalDependent = true;
				} else {
					sourceHyps.add(hypOrGoal);
				}
				final Predicate substitute = substitution[1];
				for (IPosition pos : entryPos.getValue()) {
					rewriteHyp = Rewrite(rewriteHyp, pred, pos, substitute);
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
		return new RewriteHypsOutput(isGoalDependent, hypActions);
	}

	public Predicate[] computeSubstitutionForHyp(Predicate predicate) {
		return computeSubstitution(predicate, level.from(Level.L1));
	}

	public Predicate[] computeSubstitutionForGoal(Predicate predicate) {
		return computeSubstitution(predicate, false);
	}
	
	/**
	 * Compute the appropriate substitution for the predicate
	 * <code>predicate</code>.
	 * 
	 * @param predicate
	 *            the considered predicate
	 * @param considerGoal
	 *            <code>true</code> if the goal shall be considered for
	 *            replacement
	 * @return <code>null</code> if the predicate cannot be substituted, or
	 *         <code>(predicate ↦ ⊤)</code> (respectively
	 *         <code>(¬predicate ↦ ⊥)</code>) if <code>predicate</code>
	 *         (respectively <code>¬predicate</code>) is a sequent's hypothesis,
	 *         or <code>null ↦ ⊥</code> (respectively <code>null ↦ ⊤</code>) if
	 *         <code>predicate</code> (respectively <code>¬predicate</code>) is
	 *         equal to the sequent's goal or is among the predicate when the
	 *         goal denotes a disjunction.
	 */
	public Predicate[] computeSubstitution(Predicate predicate,
			boolean considerGoal) {
		final Predicate negPred = makeNeg(predicate);
		final Predicate[] result = new Predicate[2];
		if (seq.containsHypothesis(predicate)) {
			result[0] = predicate;
			result[1] = True(ff);
			return result;
		} else if (seq.containsHypothesis(negPred)) {
			result[0] = negPred;
			result[1] = False(ff);
			return result;
		}
		if (!considerGoal) {
			return null;
		}
		for (Predicate child : goalDisjuncts) {
			if (child.equals(predicate)) {
				result[0] = null;
				result[1] = False(ff);
				return result;
			} else if (child.equals(negPred)) {
				result[0] = null;
				result[1] = True(ff);
				return result;
			}
		}
		return null;
	}

	public Set<Predicate> createGoalToHypSet() {
		final Set<Predicate> goalToHypSet = new HashSet<Predicate>();
		if (level.from(Level.L1)) {
			if (!isNeg(goal)) {
				for (Predicate child : goalDisjuncts) {
					if (isNeg(child)) {
						addToSet(goalToHypSet, makeNeg(child));
					} else {
						addToSet(goalToHypSet, child);
					}
				}
			} else {
				addToSet(goalToHypSet, makeNeg(goal));
			}
		}
		return goalToHypSet;
	}

	/**
	 * Returns a set of disjunct of <code>P</code> when it is a disjunction,
	 * otherwise a singleton set containing <code>P</code>. The returned set is
	 * mutable.
	 *
	 * @param P
	 *            a predicate
	 * @return a mutable set of disjuncts of the given predicate
	 */
	public Set<Predicate> breakPossibleDisjunct(Predicate P) {
		final List<Predicate> list;
		if (isDisj(P))
			list = Arrays.asList(disjuncts(P));
		else
			list = Arrays.asList(P);
		return new LinkedHashSet<Predicate>(list);
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
	 * @return the re-written predicate with the parameters given her-above if
	 *         the sub-formula of <code>pred</code> at the position
	 *         <code>pos</code> is equal to the predicate <code>replaced</code>,
	 *         <code>pred</code> else.
	 */
	private Predicate Rewrite(Predicate pred, Predicate replaced,
			IPosition pos, Predicate substitute) {
		if (!pred.getSubFormula(pos).equals(replaced)) {
			return pred;
		}
		return pred.rewriteSubFormula(pos, substitute);
	}

	/**
	 * Class used as the outpout of the method rewriteHyps.
	 */
	public class RewriteHypsOutput {
		private final boolean isGoalDependent;
		private final List<IHypAction> hypActions;

		public RewriteHypsOutput(boolean isGoalDependent,
				List<IHypAction> hypActions) {
			this.isGoalDependent = isGoalDependent;
			this.hypActions = hypActions;
		}

		public boolean isGoalDependent() {
			return isGoalDependent;
		}

		public List<IHypAction> getHypActions() {
			return hypActions;
		}
	}

}
