/*******************************************************************************
 * Copyright (c) 2014, 2025 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     INP Toulouse - handle set minus, intersection, union
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.tactics;

import static java.util.Arrays.asList;
import static org.eventb.core.ast.Formula.BINTER;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.SETMINUS;
import static org.eventb.core.ast.Formula.SUBSET;
import static org.eventb.core.ast.Formula.SUBSETEQ;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.conjI;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.hyp;
import static org.eventb.core.seqprover.tactics.BasicTactics.reasonerTac;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics.TrueGoalTac;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInput;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteInter;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteSet;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteSetMinus;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteUnion;
import org.eventb.internal.core.seqprover.eventbExtensions.HypOr;

/**
 * Discharges simple proofs of finiteness.
 *
 * <ul>
 * <li>
 * For sequents such as: <code>A op B, finite(B) ⊦ finite(A)</code>
 * such that <code>A op B</code> entails that <code>A ⊆ B</code>.
 * <p>
 * Tries to find a hypothesis of the form finite(B) such that B is a finite
 * superset of the set A. Gives then this expression as input to the FiniteSet
 * reasoner and applies it. Finally, applies then the TrueGoal reasoner to the
 * first descendant (WD) and applies the Hyp reasoner to the other descendants.
 * Fails otherwise. Note that we do not fail in the case where the WD subgoal is
 * not identically true, but leave it undischarged.
 * </p>
 * </li>
 * <li>
 * For sequents such as: <code>finite(A) ⊦ finite(A ∖ B)</code>, applies the
 * FiniteSetMinus reasoner, then the Hyp reasoner on the subgoal.
 * </li>
 * <li>
 * For sequents such as: <code>finite(S_i) ⊦ finite(S_1 ∩ ... ∩ S_n)</code>,
 * applies the FiniteInter reasoner, then the HypOr reasoner on the subgoal.
 * </li>
 * <li>
 * For sequents such as:
 * <code>finite(S_1) ;; ... ;; finite(S_n) ⊦ finite(S_1 ∪ ... ∪ S_n)</code>,
 * applies the FiniteUnion reasoner, then the ConjI reasoner on the subgoal,
 * then the Hyp reasoner on the <code>n</code> subgoals.
 * </li>
 * </ul>
 * 
 * @author Josselin Dolhen
 */
public class FiniteInclusionTac implements ITactic {

	private static final EmptyInput EMPTY_INPUT = new EmptyInput();

	@Override
	public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
		final IProverSequent sequent = ptNode.getSequent();
		final Predicate goal = sequent.goal();
		if (goal.getTag() != KFINITE) {
			return "Goal is not of the form finite(S)";
		}
		final Expression goalSet = ((SimplePredicate) goal).getExpression();
		switch (goalSet.getTag()) {
		case SETMINUS:
			if (applyFiniteSetMinus(ptNode, (BinaryExpression) goalSet, pm)) {
				return null;
			}
			break; // Try finiteSet if finiteSetMinus failed
		case BINTER:
			if (applyFiniteInter(ptNode, (AssociativeExpression) goalSet, pm)) {
				return null;
			}
			break; // Try finiteSet if finiteInter failed
		case BUNION:
			if (applyFiniteUnion(ptNode, (AssociativeExpression) goalSet, pm)) {
				return null;
			}
			break; // Try finiteSet if finiteUnion failed
		}
		return applyFiniteSet(ptNode, goalSet, pm);
	}

	private String applyFiniteSet(IProofTreeNode ptNode, Expression goalSet, IProofMonitor pm) {
		final Set<Expression> finiteSets = extractFiniteSupersets(
				ptNode.getSequent().visibleHypIterable(), goalSet);
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

	private boolean applyFiniteSetMinus(IProofTreeNode ptNode, BinaryExpression goalSet, IProofMonitor pm) {
		var ff = ptNode.getFormulaFactory();
		if (!ptNode.getSequent().containsHypothesis(ff.makeSimplePredicate(KFINITE, goalSet.getLeft(), null))) {
			return false;
		}
		Object result = reasonerTac(new FiniteSetMinus(), EMPTY_INPUT).apply(ptNode, pm);
		if (result != null) {
			return false;
		}
		var openDescendants = ptNode.getOpenDescendants();
		if (openDescendants.length != 1 || hyp().apply(openDescendants[0], pm) != null) {
			ptNode.pruneChildren();
			return false;
		}
		return true;
	}

	private boolean applyFiniteInter(IProofTreeNode ptNode, AssociativeExpression goalSet, IProofMonitor pm) {
		var children = Set.of(goalSet.getChildren());
		boolean found = false;
		for (Predicate hyp : ptNode.getSequent().visibleHypIterable()) {
			if (hyp.getTag() == KFINITE && children.contains(((SimplePredicate) hyp).getExpression())) {
				found = true;
				break;
			}
		}
		if (!found) {
			return false;
		}
		Object result = reasonerTac(new FiniteInter(), EMPTY_INPUT).apply(ptNode, pm);
		if (result != null) {
			return false;
		}
		var openDescendants = ptNode.getOpenDescendants();
		if (openDescendants.length != 1 || reasonerTac(new HypOr(), EMPTY_INPUT).apply(openDescendants[0], pm) != null) {
			ptNode.pruneChildren();
			return false;
		}
		return true;
	}

	private boolean applyFiniteUnion(IProofTreeNode ptNode, AssociativeExpression goalSet, IProofMonitor pm) {
		Set<Expression> finiteSets = new HashSet<>();
		for (Predicate hyp : ptNode.getSequent().visibleHypIterable()) {
			if (hyp.getTag() == KFINITE) {
				finiteSets.add(((SimplePredicate) hyp).getExpression());
			}
		}
		var children = goalSet.getChildren();
		if (!finiteSets.containsAll(asList(children))) {
			return false;
		}
		Object result = reasonerTac(new FiniteUnion(), EMPTY_INPUT).apply(ptNode, pm);
		if (result != null) {
			return false;
		}
		if (!applyConjThenHyp(children.length, ptNode, pm)) {
			ptNode.pruneChildren();
			return false;
		}
		return true;
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

	private boolean applyConjThenHyp(int count, IProofTreeNode ptNode, IProofMonitor pm) {
		IProofTreeNode[] openDescendants = ptNode.getOpenDescendants();
		if (openDescendants.length != 1) {
			return false;
		}
		if (conjI().apply(openDescendants[0], pm) != null) {
			return false;
		}
		openDescendants = openDescendants[0].getOpenDescendants();
		if (openDescendants.length != count) {
			return false;
		}
		for (var descendent : openDescendants) {
			if (hyp().apply(descendent, pm) != null) {
				return false;
			}
		}
		return true;
	}

}
