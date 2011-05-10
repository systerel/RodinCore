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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;

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
	 * @return a set of the hypothesis of the sequent (no negative predicate
	 *         allowed)
	 */
	public static Set<Predicate> createHypSet(IProverSequent seq) {
		Set<Predicate> hypSet = new HashSet<Predicate>();
		for (Predicate hyp : seq.hypIterable()) {
			if (hyp != null) {
				if (hyp.getTag() == Predicate.NOT) {
					final UnaryPredicate not = (UnaryPredicate) hyp;
					hypSet.add(not.getChild());
				} else {
					hypSet.add(hyp);
				}
			}
		}
		return hypSet;
	}

	/**
	 * Returns a set of predicates contained both in <code>goal</code> and
	 * <code>hypSet</code>
	 * 
	 * @param goal
	 *            the goal analyzed
	 * @param hypSet
	 *            the set reference of hypothesis
	 * @return a set of predicates contained both in <code>goal</code> and
	 *         <code>hypSet</code>
	 */
	public static Set<Predicate> analyzeGoal(Predicate goal,
			Set<Predicate> hypSet) {
		Set<Predicate> set = new HashSet<Predicate>();
		analyzeSubPred(goal, goal, set, hypSet);
		return set;
	}

	/**
	 * Returns a set of predicates contained both in <code>hyp</code> and
	 * <code>hypSet</code>
	 * 
	 * @param hyp
	 *            the hypothesis analyzed
	 * @param hypSet
	 *            the set reference of hypothesis
	 * @return a set of predicates contained both in <code>hyp</code> and
	 *         <code>hypSet</code>
	 */
	public static Set<Predicate> analyzeHyp(Predicate hyp, Set<Predicate> hypSet) {
		Set<Predicate> set = new HashSet<Predicate>();
		final Predicate pred;
		if (hyp.getTag() == Predicate.NOT) {
			UnaryPredicate not = (UnaryPredicate) hyp;
			pred = not.getChild();
		} else
			pred = hyp;
		hypSet.remove(pred);
		analyzeSubPred(hyp, hyp, set, hypSet);
		hypSet.add(pred);
		return set;
	}

	/**
	 * If the predicate <code>subPred</code> is contained in the set reference
	 * of hypothesis and is not the predicate <code>origin</code> (= hypothesis
	 * or goal) analyzed, then it is added to the set <code>set</code>. Else,
	 * this method is called recursively on the children of <code>subPred</code>
	 * 
	 * @param origin
	 *            the predicate (hypothesis or goal) analyzed
	 * @param subPred
	 *            the predicate compared to the set reference of hypothesis
	 *            (should be a child of origin)
	 * @param set
	 *            the set of predicates returned
	 * @param hypSet
	 *            the set reference of hypothesis
	 */
	public static void analyzeSubPred(Predicate origin, Predicate subPred,
			Set<Predicate> set, Set<Predicate> hypSet) {
		if (hypSet.contains(subPred)) {
			set.add(subPred);
		} else {
			for (int i = 0; i < subPred.getChildCount(); i++) {
				final Formula<?> child = subPred.getChild(i);
				if (child instanceof Predicate)
					analyzeSubPred(origin, (Predicate) child, set, hypSet);
			}
		}
	}

	/**
	 * Returns the goal re-written using the generalized Modus Ponens and add in
	 * neededHyps all the hypothesis needed to achieve it.
	 * 
	 * @param goal
	 *            the re-written goal
	 * @param seq
	 *            the sequent containing the goal
	 * @param modifGoalSet
	 *            the set of all the sub-predicate of the goal that can be
	 *            substitute by <code>⊤</code> or <code>⊥</code>
	 * @param neededHyps
	 *            the set of all the needed hypotheses to rewrite the goal
	 *            (computed by this method, it should be an empty set)
	 * @return
	 */
	public static Predicate rewriteGoal(Predicate goal, IProverSequent seq,
			Set<Predicate> modifGoalSet, Set<Predicate> neededHyps) {
		final FormulaFactory ff = seq.getFormulaFactory();
		final DLib lib = DLib.mDLib(ff);
		Predicate rewrittenPred = goal;
		for (Predicate value : modifGoalSet) {
			final Predicate negValue = ff.makeUnaryPredicate(Formula.NOT,
					value, null);
			final Predicate p;
			if (seq.containsHypothesis(value)) {
				p = lib.True();
				neededHyps.add(value);
			} else if (seq.containsHypothesis(negValue)) {
				p = lib.False();
				neededHyps.add(negValue);
			} else {
				continue;
			}
			rewrittenPred = Lib.equivalenceRewrite(rewrittenPred, value, p, ff);
		}
		if (rewrittenPred != goal)
			return rewrittenPred;
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
			Map<Predicate, Set<Predicate>> modifHypMap) {
		final List<IHypAction> hypActions = new ArrayList<IHypAction>();
		final FormulaFactory ff = seq.getFormulaFactory();
		final DLib lib = DLib.mDLib(ff);

		for (Entry<Predicate, Set<Predicate>> entry : modifHypMap.entrySet()) {
			Set<Predicate> inferredHyps = new HashSet<Predicate>();
			Set<Predicate> sourceHyps = new LinkedHashSet<Predicate>();
			final Set<Predicate> preds = entry.getValue();
			final Predicate hyp = entry.getKey();

			Predicate rewrittenHyp = hyp;
			for (Predicate pred : preds) {
				final Predicate negPred = ff.makeUnaryPredicate(Formula.NOT,
						pred, null);
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
				rewrittenHyp = Lib.equivalenceRewrite(rewrittenHyp, pred,
						substitute, ff);
			}
			if (rewrittenHyp != hyp)
				inferredHyps = Collections.singleton(rewrittenHyp);
			else
				continue;
			sourceHyps.add(hyp);
			hypActions.add(ProverFactory.makeForwardInfHypAction(sourceHyps,
					inferredHyps));
			hypActions.add(ProverFactory.makeHideHypAction(Collections
					.singleton(hyp)));
		}
		return hypActions;
	}

}
