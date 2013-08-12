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
	private Predicate rewrittenGoal;
	private RewriteHypsOutput output;
	// the set of all the needed hypotheses to rewrite the goal
	// (computed by this method, it should be an empty set)
	private final Set<Predicate> neededHyps;
	// All possible substitutions
	// (hypothesis or goal ↦ substitute (<code>⊤</code> or <code>⊥</code>))
	private final Map<Predicate, Substitute> substitutes;

	public GenMPC(Level level, IProofMonitor pm, IProverSequent seq) {
		super();
		this.level = level;
		this.pm = pm;
		this.seq = seq;
		ff = seq.getFormulaFactory();
		neededHyps = new HashSet<Predicate>();
		goal = seq.goal();
		goalDisjuncts = breakPossibleDisjunct(goal);
		substitutes = new HashMap<Predicate, Substitute>();
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
		extractFromHypotheses();
		extractFromGoal();

		Map<Predicate, List<IPosition>> m = analyzePred(goal);
		rewrittenGoal = rewriteGoal(m);

		boolean isGoalDependent = false;
		List<IHypAction> hypActions = new ArrayList<IHypAction>();
		for (Predicate hyp : seq.visibleHypIterable()) {
			if (pm != null && pm.isCanceled()) {
				return false;
			}
			m = analyzePred(hyp);
			isGoalDependent |= rewriteHyp(hypActions, hyp, m);

		}
		output = new RewriteHypsOutput(isGoalDependent, hypActions);
		return true;
	}

	/**
	 * Adds the substitutes that can be extracted from hypotheses.
	 */
	private void extractFromHypotheses() {
		for (Predicate hyp : seq.hypIterable()) {
			if (isNeg(hyp)) {
				final Predicate negPred = makeNeg(hyp);
				addSubstitute(new Substitute(hyp, false, negPred, False(ff)));
			} else {
				addSubstitute(new Substitute(hyp, false, hyp, True(ff)));
			}
		}
	}

	// Substitutes coming from hypotheses must be added BEFORE those coming from
	// the goal.
	private void addSubstitute(Substitute subst) {
		final Predicate toReplace = subst.toReplace();
		if (isTrueOrFalsePred(toReplace)) {
			return;
		}
		if (substitutes.containsKey(toReplace)) {
			return;
		}
		substitutes.put(toReplace, subst);
	}
	
	/**
	 * Computes the predicate that can be substituted.
	 * 
	 * @param hyp
	 *            the hypothesis (should not be <code>⊤</code> or <code>⊥</code>
	 *            ).
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
	 * sub-predicate contained both in <code>pred</code> and the known
	 * substitutes and the List of IPositions is a list of positions where
	 * Predicate occurs in <code>pred</code>.
	 * 
	 * @param pred
	 *            the predicate to analyze (should be either a hypothesis or a
	 *            goal)
	 * @return a map of all possible substitution positions
	 */
	public Map<Predicate, List<IPosition>> analyzePred(Predicate pred) {
		Map<Predicate, List<IPosition>> map = new HashMap<Predicate, List<IPosition>>();
		analyzeSubPred(pred, map);
		return map;
	}

	/**
	 * Records in <code>map</code> all the sub-predicate of <code>origin</code>
	 * that can be substituted, as well as their position in <code>origin</code>
	 * . If A sub-predicate is recorded, its children are not analyzed.
	 * 
	 * @param origin
	 *            the predicate (hypothesis or goal) analyzed
	 * @param map
	 *            the map (predicate contained both in <code>origin</code> and
	 *            that can be substituted ↦ its position in <code>origin</code>)
	 */
	public void analyzeSubPred(final Predicate origin,
			final Map<Predicate, List<IPosition>> map) {

		origin.inspect(new DefaultInspector<Predicate>() {

			private void addPredToMap(
					final Map<Predicate, List<IPosition>> map,
					Predicate predicate, IAccumulator<Predicate> accumulator) {

				if (isTrueOrFalsePred(predicate)
						|| predicate == computePred(origin)) {
					return;
				}
				if (substitutes.containsKey(predicate)) {
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
				addPredToMap(map, predicate, accumulator);
			}

			@Override
			public void inspect(BinaryPredicate predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, predicate, accumulator);
			}

			@Override
			public void inspect(ExtendedPredicate predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, predicate, accumulator);
			}

			@Override
			public void inspect(LiteralPredicate predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, predicate, accumulator);
			}

			@Override
			public void inspect(MultiplePredicate predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, predicate, accumulator);
			}

			@Override
			public void inspect(PredicateVariable predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, predicate, accumulator);
			}

			@Override
			public void inspect(QuantifiedPredicate predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, predicate, accumulator);
			}

			@Override
			public void inspect(RelationalPredicate predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, predicate, accumulator);
			}

			@Override
			public void inspect(SimplePredicate predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, predicate, accumulator);
			}

			@Override
			public void inspect(UnaryPredicate predicate,
					IAccumulator<Predicate> accumulator) {
				addPredToMap(map, predicate, accumulator);
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
	public Predicate rewriteGoal(Map<Predicate, List<IPosition>> m) {
		Predicate rewriteGoal = goal;
		for (Entry<Predicate, List<IPosition>> entry : m.entrySet()) {
			final Predicate value = entry.getKey();
			final Substitute substitution = findSubstitutionForGoal(value);
			if (substitution == null) {
				continue;
			}
			neededHyps.add(substitution.hypOrGoal());
			for (IPosition pos : entry.getValue()) {
				rewriteGoal = Rewrite(rewriteGoal, value, pos,
						substitution.substitute());
			}
		}

		if (rewriteGoal != goal) {
			return rewriteGoal;
		}
		return null;

	}

	/**
	 * Rewrite each occurence of a hypothesis into the sequent.
	 * 
	 * @param hypActions
	 *            list of IHypActions needed to complete the re-writing done by
	 *            the generalized Modus Ponens
	 * @param hyp
	 *            the hypothesis
	 * @param maps
	 * @return a boolean telling whether the reasoner is goal dependent or not
	 */
	public boolean rewriteHyp(List<IHypAction> hypActions, Predicate hyp,
			final Map<Predicate, List<IPosition>> maps) {
		boolean isGoalDependent = false;
		Set<Predicate> inferredHyps = new HashSet<Predicate>();
		Set<Predicate> sourceHyps = new LinkedHashSet<Predicate>();
		Predicate rewriteHyp = hyp;
		for (Entry<Predicate, List<IPosition>> entryPos : maps.entrySet()) {
			final Predicate pred = entryPos.getKey();
			final Substitute substitution = findSubstitutionForHyp(pred);
			if (substitution == null) {
				continue;
			}

			if (substitution.hypOrGoal() == null) {
				isGoalDependent = true;
			} else {
				sourceHyps.add(substitution.hypOrGoal());
			}

			for (IPosition pos : entryPos.getValue()) {
				rewriteHyp = Rewrite(rewriteHyp, pred, pos,
						substitution.substitute());
			}
		}
		if (rewriteHyp != hyp) {
			inferredHyps = Collections.singleton(rewriteHyp);
			sourceHyps.add(hyp);
			hypActions.add(ProverFactory.makeForwardInfHypAction(sourceHyps,
					inferredHyps));
			hypActions.add(ProverFactory.makeHideHypAction(Collections
					.singleton(hyp)));
		}

		return isGoalDependent;
	}

	public Substitute findSubstitutionForHyp(Predicate predicate) {
		return findSubstitution(predicate, level.from(Level.L1));
	}

	public Substitute findSubstitutionForGoal(Predicate predicate) {
		return findSubstitution(predicate, false);
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
	public Substitute findSubstitution(Predicate predicate, boolean considerGoal) {
		final Substitute substitute = substitutes.get(predicate);
		if (substitute == null) {
			return null;
		}
		if (substitute.fromGoal() && !considerGoal) {
			return null;
		}
		return substitute;
	}

	public void extractFromGoal() {
		if (!level.from(Level.L1)) {
			// Level 0 ignores all predicates in the goal
			return;
		}
		for (Predicate child : goalDisjuncts) {
			if (isNeg(child)) {
				addSubstitute(new Substitute(goal, true, makeNeg(child), True(ff)));
			} else {
				addSubstitute(new Substitute(goal, true, child, False(ff)));
			}
		}
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
