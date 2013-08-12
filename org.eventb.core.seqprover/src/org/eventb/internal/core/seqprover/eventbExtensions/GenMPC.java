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

import static org.eventb.core.seqprover.eventbExtensions.DLib.makeNeg;
import static org.eventb.core.seqprover.eventbExtensions.Lib.disjuncts;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isDisj;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isNeg;
import static org.eventb.internal.core.seqprover.eventbExtensions.Substitute.makeSubstitutes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.DefaultInspector;
import org.eventb.core.ast.ExtendedPredicate;
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

	private static class SubstAppli {

		final Substitute substitute;
		final IPosition position;

		public SubstAppli(Substitute substitute, IPosition position) {
			this.substitute = substitute;
			this.position = position;
		}
		
		public Predicate apply(Predicate pred) {
			assert(pred.getSubFormula(position).equals(substitute.toReplace()));
			return pred.rewriteSubFormula(position, substitute.substitute());
		}

		@Override
		public String toString() {
			return substitute + " at pos " + position;
		}

	}

	private final IProverSequent seq;
	private final Predicate goal;

	private final Level level;
	private final IProofMonitor pm;
	private Predicate rewrittenGoal;
	private boolean isGoalDependent;
	private final List<IHypAction> hypActions;
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
		neededHyps = new HashSet<Predicate>();
		goal = seq.goal();
		substitutes = new HashMap<Predicate, Substitute>();
		this.isGoalDependent = false;
		this.hypActions = new ArrayList<IHypAction>();
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

	public boolean isGoalDependent() {
		return isGoalDependent;
	}

	public List<IHypAction> hypActions() {
		return hypActions;
	}

	/**
	 * Runs generalized Modus Ponens reasoner that generate the re-written goal
	 * and hypotheses and all the hypotheses needed to achieve it and all
	 * IHypActions computed.
	 * 
	 * We first compute possible substitutes from hypotheses and goal. Then, we
	 * traverse each predicate to get the list of possible applications of
	 * substitutes and finally apply them.
	 */
	public boolean runGenMP() {
		extractFromHypotheses();
		extractFromGoal();

		List<SubstAppli> applis = analyzePred(goal, true);
		rewrittenGoal = rewriteGoal(applis);

		for (Predicate hyp : seq.visibleHypIterable()) {
			if (pm != null && pm.isCanceled()) {
				return false;
			}
			applis = analyzePred(hyp, false);
			rewriteHyp(hyp, applis);

		}
		return true;
	}

	/**
	 * Adds the substitutes that can be extracted from hypotheses.
	 */
	private void extractFromHypotheses() {
		for (Predicate hyp : seq.hypIterable()) {
			addSubstitute(hyp, false, hyp);
		}
	}

	// Substitutes coming from hypotheses must be added BEFORE those coming from
	// the goal.
	private void addSubstitute(Predicate origin, boolean fromGoal,
			Predicate source) {
		final List<Substitute> substs = makeSubstitutes(origin, fromGoal, source);
		for (final Substitute subst : substs) {
			final Predicate toReplace = subst.toReplace();
			if (substitutes.containsKey(toReplace)) {
				return;
			}
			substitutes.put(toReplace, subst);
		}
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
	 * Returns a list of applications of substitutes to the given predicate. The
	 * returned applications are guaranteed to not overlap.
	 * 
	 * @param origin
	 *            the predicate to analyze (should be either a hypothesis or the
	 *            goal)
	 * @param isGoal
	 *            <code>true</code> if <code>origin</code> is the goal
	 * @return a list of all possible substitution applications
	 */
	public List<SubstAppli> analyzePred(final Predicate origin,
			final boolean isGoal) {
		return origin.inspect(new DefaultInspector<SubstAppli>() {

			private void addPredToMap(Predicate predicate,
					IAccumulator<SubstAppli> accumulator) {
				final Substitute subst = substitutes.get(predicate);
				if (subst == null) {
					return;
				}
				// TODO in next level, fix these tests which are wrong.
				if (isGoal && subst.fromGoal()) {
					// Do not rewrite the goal with itself
					return;
				}
				if (predicate == computePred(origin)) {
					return;
				}
				final IPosition pos = accumulator.getCurrentPosition();
				accumulator.add(new SubstAppli(subst, pos));
				accumulator.skipChildren();
			}

			@Override
			public void inspect(AssociativePredicate predicate,
					IAccumulator<SubstAppli> accumulator) {
				addPredToMap(predicate, accumulator);
			}

			@Override
			public void inspect(BinaryPredicate predicate,
					IAccumulator<SubstAppli> accumulator) {
				addPredToMap(predicate, accumulator);
			}

			@Override
			public void inspect(ExtendedPredicate predicate,
					IAccumulator<SubstAppli> accumulator) {
				addPredToMap(predicate, accumulator);
			}

			@Override
			public void inspect(LiteralPredicate predicate,
					IAccumulator<SubstAppli> accumulator) {
				addPredToMap(predicate, accumulator);
			}

			@Override
			public void inspect(MultiplePredicate predicate,
					IAccumulator<SubstAppli> accumulator) {
				addPredToMap(predicate, accumulator);
			}

			@Override
			public void inspect(PredicateVariable predicate,
					IAccumulator<SubstAppli> accumulator) {
				addPredToMap(predicate, accumulator);
			}

			@Override
			public void inspect(QuantifiedPredicate predicate,
					IAccumulator<SubstAppli> accumulator) {
				addPredToMap(predicate, accumulator);
			}

			@Override
			public void inspect(RelationalPredicate predicate,
					IAccumulator<SubstAppli> accumulator) {
				addPredToMap(predicate, accumulator);
			}

			@Override
			public void inspect(SimplePredicate predicate,
					IAccumulator<SubstAppli> accumulator) {
				addPredToMap(predicate, accumulator);
			}

			@Override
			public void inspect(UnaryPredicate predicate,
					IAccumulator<SubstAppli> accumulator) {
				addPredToMap(predicate, accumulator);
			}

		});
	}

	/**
	 * Returns the goal re-written using the generalized Modus Ponens and add in
	 * neededHyps all the hypotheses needed to achieve it.
	 * 
	 * @param applis
	 *            list of substitute applications to the goal
	 * @return the goal re-written, or <code>null</code> if it has not been
	 *         re-written
	 */
	private Predicate rewriteGoal(List<SubstAppli> applis) {
		Predicate rewriteGoal = goal;
		for (final SubstAppli appli : applis) {
			final Substitute substitution = appli.substitute;
			neededHyps.add(substitution.origin());
			rewriteGoal = appli.apply(rewriteGoal);
		}

		if (rewriteGoal != goal) {
			return rewriteGoal;
		}
		return null;

	}

	/**
	 * Rewrites the given hypothesis with the given application.
	 * 
	 * @param hyp
	 *            the hypothesis
	 * @param applis
	 *            list of substitute applications to the hypothesis
	 */
	private void rewriteHyp(Predicate hyp, List<SubstAppli> applis) {
		Set<Predicate> inferredHyps = new HashSet<Predicate>();
		Set<Predicate> sourceHyps = new LinkedHashSet<Predicate>();
		Predicate rewriteHyp = hyp;
		for (final SubstAppli appli : applis) {
			final Substitute substitution = appli.substitute;
			if (substitution.fromGoal()) {
				isGoalDependent = true;
			} else {
				sourceHyps.add(substitution.origin());
			}
			rewriteHyp = appli.apply(rewriteHyp);
		}
		if (rewriteHyp != hyp) {
			inferredHyps = Collections.singleton(rewriteHyp);
			sourceHyps.add(hyp);
			hypActions.add(ProverFactory.makeForwardInfHypAction(sourceHyps,
					inferredHyps));
			hypActions.add(ProverFactory.makeHideHypAction(Collections
					.singleton(hyp)));
		}
	}

	public void extractFromGoal() {
		if (!level.from(Level.L1)) {
			// Level 0 ignores all predicates in the goal
			return;
		}
		if (isDisj(goal)) {
			for (final Predicate child : disjuncts(goal)) {
				addSubstitute(goal, true, child);
			}
			return;
		}
		addSubstitute(goal, true, goal);
	}

}
