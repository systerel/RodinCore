/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
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

import java.util.List;
import java.util.Set;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.eventbExtensions.Lib;

/**
 * This class is responsible of saturating the given proof tree node, with all
 * hypotheses of the form "f(x) ∈ S2" that are computed from given functional
 * hypotheses of the form f ∈ S1 op S2.
 */
public class FunImgGoalApplier {

	private final List<IPosition> positions;
	private final IProofMonitor monitor;
	private final Predicate goal;
	private Predicate currentHyp;
	private IProofTreeNode node;

	public FunImgGoalApplier(IProofTreeNode node, IProofMonitor pm,
			List<IPosition> positions) {
		this.positions = positions;
		this.monitor = pm;
		this.node = node;
		this.goal = node.getSequent().goal();
	}

	/**
	 * Saturates the hypotheses of the proof tree node by applying successively
	 * the funImgGoal tactic with the given functional hypothesis.
	 * 
	 * @param hyp
	 *            the functional hypothesis of the form f ∈ S1 op S2 to be used
	 * @return <code>true</code> if the tactic applied on the current node, or
	 *         <code>false</code> otherwise.
	 */
	public boolean apply(Predicate hyp) {
		this.currentHyp = hyp;
		for (IPosition position : positions) {
			if (!canForward(position)) {
				continue;
			}
			if (funImgGoal(currentHyp, position).apply(node, monitor) != null) {
				return false;
			}
			currentHyp = getNewHyp(node);
			node = node.getFirstOpenDescendant();
		}
		return true;
	}

	public IProofTreeNode getProofTreeNode() {
		return node;
	}

	private boolean canForward(IPosition position) {
		final BinaryExpression funApp = getFunApp(goal, position);
		return Lib.getElement(currentHyp).equals(funApp.getLeft());
	}

	private BinaryExpression getFunApp(Predicate goal, IPosition pos) {
		return (BinaryExpression) goal.getSubFormula(pos);
	}

	private Predicate getNewHyp(IProofTreeNode oldNode) {
		final IProofRule rule = oldNode.getRule();
		final IAntecedent antecedent = rule.getAntecedents()[0];
		final Set<Predicate> addedHyps = antecedent.getAddedHyps();
		return addedHyps.iterator().next();
	}

}