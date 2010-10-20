/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.internal.core.seqprover.eventbExtensions.tactics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;

/**
 * This is a collection of static constants and methods that are used in
 * relation with tactics.
 * 
 */
public class TacticsLib {

	/**
	 * Adds additional hypotheses about function application in the sequent.
	 * 
	 * @param ptNode
	 *            The open proof tree node where the tactic must be applied.
	 * @param pm
	 *            The proof monitor that monitors the progress of the tactic
	 *            application.
	 * @return open proof tree node with additional hypotheses.
	 */
	public static IProofTreeNode addFunctionalHypotheses(IProofTreeNode ptNode,
			IProofMonitor pm) {
		final IProverSequent seq = ptNode.getSequent();
		final Predicate goal = seq.goal();
		List<Predicate> inclusionHypotheses = getInclusionHypotheses(seq);
		final List<IPosition> funAppPositions = findFunAppPositions(goal);
		for (IPosition funAppPosition : funAppPositions) {
			final BinaryExpression funApp = (BinaryExpression) goal
					.getSubFormula(funAppPosition);
			Predicate neededHyp = searchFunctionalHypotheses(
					inclusionHypotheses, funApp);
			if (neededHyp != null) {
				neededHyp = search(inclusionHypotheses, funApp, neededHyp);
				if (Tactics.funImgGoal(neededHyp, funAppPosition).apply(ptNode,
						pm) == null) {
					ptNode = ptNode.getFirstOpenDescendant();
					inclusionHypotheses = getInclusionHypotheses(ptNode
							.getSequent());
				}
			}
		}
		return ptNode;
	}	
	
	/**
	 * Searches hypotheses of the form f(E)∈ S2 in membership hypotheses.
	 * @param inclusionHypotheses
	 * 			list of hypotheses with membership.
	 * @param funApp
	 * 			a function application
	 * @param neededHyp
	 * 			an hypothesis of the form f ∈ S1 op S2
	 * @return
	 * 			true iff no hypotheses of the form f(E)∈ S2 have been found.
	 */
	private static Predicate search(List<Predicate> inclusionHypotheses,
			BinaryExpression funApp, Predicate neededHyp) {
		for (Predicate hyp : inclusionHypotheses) {
			final Expression set = Lib.getSet(hyp);
			final Expression otherSet = ((BinaryExpression) Lib
					.getSet(neededHyp)).getRight();
			if (Lib.getElement(hyp).equals(funApp)
					&& set.equals(otherSet)) {
				return null;
			}
		}
		return neededHyp;
	}

	/**
	 * Finds an hypothesis of the form f ∈ S1 op S2
	 * 
	 * @param inclusionHypotheses
	 * 			List of hypotheses with membership.
	 * @param funApp
	 * 			a function application
	 * @return
	 * 		 true iff an hypothesis of the form f ∈ S1 op S2 has been found.
	 */
	private static Predicate searchFunctionalHypotheses(
			List<Predicate> inclusionHypotheses, BinaryExpression funApp) {
		for (Predicate hyp : inclusionHypotheses) {
			final Expression set = Lib.getSet(hyp);
			final Expression function = Lib.getLeft(funApp);
			if (Lib.isRel(set) || Lib.isFun(set)) {
				if (Lib.getElement(hyp).equals(function)) {
					return hyp;
				}
			}
		}
		return null;
	}

	/**
	 * Creates an instance of InDomManager for each total domain occurrence in
	 * the goal.
	 * 
	 * @param goal
	 *            Goal of the sequent
	 * @return a set of InDomManager
	 */
	public static InDomGoalManager createInDomManager(final Predicate goal) {
		final List<IPosition> domPositions = TacticsLib.findDomExpression(goal);
		assert(domPositions.size()==1);
		final UnaryExpression domExpr = ((UnaryExpression) goal
					.getSubFormula(domPositions.get(0)));
		final InDomGoalManager inDomMng = new InDomGoalManager(domExpr,
				domPositions.get(0));		
		return inDomMng;
	}

	/**
	 * Finds total domain expressions in a predicate
	 * 
	 * @param pred
	 *            a predicate
	 * @return list of total domain expression positions
	 */
	private static List<IPosition> findDomExpression(Predicate pred) {
		final List<IPosition> domPositions = pred.getPositions(new DefaultFilter() {
			@Override
			public boolean select(UnaryExpression expression) {
				return (Lib.isDom(expression) && expression.isWellFormed());
			}
		});
		Lib.removeWDUnstrictPositions(domPositions, pred);
		return domPositions;
	}

	/**
	 * Finds function application expressions in a predicate
	 * 
	 * @param pred
	 *            a predicate
	 * @return list of function application positions
	 */
	private static List<IPosition> findFunAppPositions(Predicate pred) {
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
	 * Creates a list of hypotheses with of the form "element ∈ set"
	 * 
	 * @param seq
	 *            a given sequent
	 * @return list of hypotheses
	 */
	private static List<Predicate> getInclusionHypotheses(IProverSequent seq) {
		final List<Predicate> inclusionHypothesis = new ArrayList<Predicate>();
		for (Predicate hyp : seq.visibleHypIterable()) {
			if (Lib.isInclusion(hyp)) {
				inclusionHypothesis.add(hyp);
			}
		}
		return inclusionHypothesis;
	}
}
