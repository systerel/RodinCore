/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.tactics;

import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.SUBSET;
import static org.eventb.core.ast.Formula.SUBSETEQ;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.hyp;
import static org.eventb.core.seqprover.tactics.BasicTactics.reasonerTac;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics.TrueGoalTac;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInput;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteSet;

/**
 * Discharges a sequent such as : <code>A op B, finite(B) ⊦ finite(A)</code>
 * such that <code>A op B</code> entails that <code>A ⊆ B</code>.
 * <p>
 * Tries to find a hypothesis of the form finite(B) such that B is a finite
 * superset of the set A. Gives then this expression as input to the FiniteSet
 * reasoner and applies it. Finally, applies then the TrueGoal reasoner to the
 * first descendant (WD) and applies the Hyp reasoner to the other descendants.
 * Fails otherwise. Note that we do not fail in the case where the WD subgoal is
 * not identically true, but leave it undischarged.
 * </p>
 * 
 * @author Josselin Dolhen
 */
public class FiniteInclusionTac implements ITactic {

	@Override
	public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
		final IProverSequent sequent = ptNode.getSequent();
		final Predicate goal = sequent.goal();
		if (goal.getTag() != KFINITE) {
			return "Goal is not of the form finite(S)";
		}
		final Expression goalSet = ((SimplePredicate) goal).getExpression();
		final Set<Expression> finiteSets = extractFiniteSupersets(
				sequent.visibleHypIterable(), goalSet);
		if (finiteSets.isEmpty()) {
			return "Tactic unapplicable";
		}
		final Object result = applyFiniteSetTac(ptNode, pm, finiteSets);
		if (pm != null && pm.isCanceled()) {
			return "Canceled";
		}
		if (result != null) {
			return "Tactic unapplicable";
		}
		final boolean success = applyTruGoalAndHyp(ptNode, pm);

		if (!success) {
			ptNode.pruneChildren();
			return "Tactic fails";
		}
		return null;
	}

	/**
	 * Extracts a list of expressions corresponding to finite supersets of the
	 * given goal set.
	 * 
	 * @param hypotheses
	 *            sequent hypotheses
	 * @param goalSet
	 *            the expression E extracted from the goal of form finite(E)
	 * @return a list of expressions
	 */
	private static Set<Expression> extractFiniteSupersets(
			Iterable<Predicate> hypotheses, Expression goalSet) {
		final Set<Expression> superSets = new HashSet<Expression>();
		final Set<Expression> finiteSets = new HashSet<Expression>();
		for (final Predicate hyp : hypotheses) {
			final RelationalPredicate rHyp;
			switch (hyp.getTag()) {
			case SUBSETEQ:
			case SUBSET:
				rHyp = (RelationalPredicate) hyp;
				if (rHyp.getLeft().equals(goalSet)) {
					superSets.add(rHyp.getRight());
				}
				break;
			case EQUAL:
				rHyp = (RelationalPredicate) hyp;
				if (rHyp.getLeft().equals(goalSet)) {
					superSets.add(rHyp.getRight());
				} else if (rHyp.getRight().equals(goalSet)) {
					superSets.add(rHyp.getLeft());
				}
				break;
			case KFINITE:
				finiteSets.add(((SimplePredicate) hyp).getExpression());
				break;
			}
		}
		finiteSets.retainAll(superSets);
		return finiteSets;
	}

	/*
	 * Applies the finiteSet reasoner to the given proof node.
	 */
	private Object applyFiniteSetTac(IProofTreeNode ptNode, IProofMonitor pm,
			Set<Expression> finiteSets) {
		final Expression expr = finiteSets.iterator().next();
		final SingleExprInput input = new SingleExprInput(expr);
		final ITactic finiteSetTac = reasonerTac(new FiniteSet(), input);
		return finiteSetTac.apply(ptNode, pm);
	}

	/**
	 * Applies the TrueGoal reasoner to the first descendant of the given node
	 * and applies the Hyp reasoner to the other descendants.
	 * 
	 * @param ptNode
	 *            the current proof tree node
	 * @param pm
	 *            a progress monitor
	 * @return <code>true</code> if all its descendants are closed,
	 *         <code>false</code> otherwise.
	 */
	private boolean applyTruGoalAndHyp(IProofTreeNode ptNode, IProofMonitor pm) {
		final IProofTreeNode[] openDescendants = ptNode.getOpenDescendants();
		if (openDescendants.length != 3) {
			// Unexpected number of descendants
			return false;
		}
		// Attempt to discharge WD subgoal
		final TrueGoalTac trueGoalTac = new TrueGoalTac();
		trueGoalTac.apply(openDescendants[0], pm);
		// Apply HYP to discharge "A ⊆ B"
		if (hyp().apply(openDescendants[1], pm) != null) {
			return false;
		}
		// Apply HYP to discharge "finite(B)"
		if (hyp().apply(openDescendants[2], pm) != null) {
			return false;
		}
		return true;
	}

}
