/*******************************************************************************
 * Copyright (c) 2012, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.tactics;

import static org.eventb.core.seqprover.eventbExtensions.Tactics.funImgGoal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.eventbExtensions.Lib;

/**
 * This class is responsible for saturating the set of hypotheses with all
 * hypotheses of the form "f(x) ∈ S2" that are computed from a given functional
 * hypothesis of the form f ∈ S1 op S2 when the goal contains "f(x)" in a
 * strictly well-defined position.
 */
public class FunImgGoalApplier {

	private final IProofMonitor monitor;
	private final Predicate goal;
	private IProofTreeNode node;

	private final Set<Predicate> hyps = new LinkedHashSet<Predicate>();
	private final Set<Predicate> addedHyps = new LinkedHashSet<Predicate>();

	public FunImgGoalApplier(IProofTreeNode node, IProofMonitor pm) {
		this.monitor = pm;
		this.node = node;
		this.goal = node.getSequent().goal();
	}

	/**
	 * Saturates the set of visible hypotheses by applying repeatedly the
	 * funImgGoal tactic for each function application found in the goal. It
	 * uses the visible functional hypotheses.
	 */
	public void saturate() {
		final IProverSequent sequent = node.getSequent();
		hyps.addAll(searchFunctionalHyps(sequent));
		final List<IPosition> funAppPositions = findFunAppPositions(goal);
		for (IPosition position : funAppPositions) {
			if (monitor != null && monitor.isCanceled())
				return;
			hyps.addAll(addedHyps);
			for (Predicate hyp : hyps) {
				apply(hyp, position);
			}
		}
	}

	/**
	 * Tries to apply the funImgGoal tactic and updates the current node. If the
	 * tactic succeeds, the added hypothesis is stored to allow further attempts
	 * of funImgGoal on it at other shallower positions.
	 * 
	 * @param hyp
	 *            the functional hypothesis of the form f ∈ S1 op S2
	 * @param position
	 *            a valid position of an function application f(E) in the goal
	 */
	private void apply(Predicate hyp, IPosition position) {
		if (!isSameFunApp(hyp, position)) {
			return;
		}
		if (funImgGoal(hyp, position).apply(node, monitor) != null) {
			return;
		}
		final Predicate newHyp = getNewHyp(node);
		if (newHyp != null && isMemberOfFunRelForm(newHyp)) {
			addedHyps.add(newHyp);
		}
		node = node.getFirstOpenDescendant();
	}

	/**
	 * Tells if the function of the functional application at the given position
	 * in the current goal is the same as the function in the left part of the
	 * given predicate of the form f ∈ S1 op S2
	 * 
	 * @param pred
	 *            a predicate of the form f ∈ S1 op S2
	 * @param position
	 *            a position of functional application in the goal
	 * @return <code>true</code> iff the function of the functional application
	 *         in the goal a the position is the function in the left part of
	 *         the given predicate of the form f ∈ S1 op S2
	 */
	private boolean isSameFunApp(Predicate pred, IPosition position) {
		final BinaryExpression funApp = (BinaryExpression) goal
				.getSubFormula(position);
		return Lib.getElement(pred).equals(funApp.getLeft());
	}

	/**
	 * Returns the new hypothesis created by the precedent rule application.
	 */
	private Predicate getNewHyp(IProofTreeNode oldNode) {
		final IProofRule rule = oldNode.getRule();
		final IAntecedent antecedent = rule.getAntecedents()[0];
		final Set<Predicate> anteAddedHyps = antecedent.getAddedHyps();
		return anteAddedHyps.iterator().next();
	}

	/**
	 * Returns <code>true</code> iff the predicate is of the form f ∈ S1 op S2
	 * where op is either relational or functional.
	 * 
	 * @param pred
	 *            the predicate to test
	 * 
	 * @return <code>true</code> iff the predicate is of the form f ∈ S1 op S2
	 */
	private boolean isMemberOfFunRelForm(Predicate pred) {
		if (Lib.isInclusion(pred)) {
			final Expression set = Lib.getSet(pred);
			if (Lib.isRel(set) || Lib.isFun(set)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Finds function application positions (e.g. f(x)) in the given predicate
	 * and order them from the deepest to the shallowest.
	 * 
	 * @param pred
	 *            a predicate
	 * @return list of function application positions
	 */
	private List<IPosition> findFunAppPositions(Predicate pred) {
		final List<IPosition> funAppPositions = new ArrayList<IPosition>();
		funAppPositions.addAll(pred.getPositions(new DefaultFilter() {
			@Override
			public boolean select(BinaryExpression expression) {
				return (Lib.isFunApp(expression));
			}
		}));
		Lib.removeWDUnstrictPositions(funAppPositions, pred);
		Collections.reverse(funAppPositions);
		return funAppPositions;
	}

	/**
	 * Find all visible hypotheses of the given sequent which are of the form f
	 * ∈ S1 op S2 where f denotes a function.
	 * 
	 * @return a list of hypotheses the form f ∈ S1 op S2 where f is a function
	 */
	private Set<Predicate> searchFunctionalHyps(IProverSequent seq) {
		final Set<Predicate> functionalHyps = new LinkedHashSet<Predicate>();
		for (Predicate hyp : seq.visibleHypIterable()) {
			if (isMemberOfFunRelForm(hyp)) {
				functionalHyps.add(hyp);
			}
		}
		return functionalHyps;
	}

	/** Returns the open proof tree node. */
	public IProofTreeNode getProofTreeNode() {
		return node;
	}

}
