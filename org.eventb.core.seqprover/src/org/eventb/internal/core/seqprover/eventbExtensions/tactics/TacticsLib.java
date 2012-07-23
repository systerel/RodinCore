/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
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

/**
 * This is a collection of static constants and methods that are used in
 * relation with tactics.
 * 
 */
public class TacticsLib {

	/**
	 * Adds additional hypotheses about function application in the sequent.
	 * 
	 * @param node
	 *            the open proof tree node where the tactic could apply
	 * @param pm
	 *            the proof monitor of the tactic application
	 * @return an open proof tree node with additional hypotheses relevant for
	 *         function application in the sequent
	 */
	public static IProofTreeNode addFunctionalHypotheses(IProofTreeNode node,
			IProofMonitor pm) {
		final IProverSequent seq = node.getSequent();
		final List<Predicate> funHyps = searchFunctionalHyps(seq);
		final List<IPosition> funAppPositions = findFunAppPositions(seq.goal());
		final FunImgGoalApplier applier = new FunImgGoalApplier(node, pm, funAppPositions);
		for (Predicate hyp : funHyps) {
			final IProofTreeNode backtrackNode = applier.getProofTreeNode();
			if (!applier.apply(hyp)) {
				backtrackNode.pruneChildren();
			}
		}
		return applier.getProofTreeNode();
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
	 * Find all hypotheses from the visible hypotheses of the given sequent
	 * which are of the form f ∈ S1 op S2 where f denotes a function.
	 * 
	 * @return a list of hypotheses the form f ∈ S1 op S2 where f is a function
	 */
	private static List<Predicate> searchFunctionalHyps(IProverSequent seq) {
		final List<Predicate> functionalHyps = new ArrayList<Predicate>();
		for (Predicate hyp : seq.visibleHypIterable()) {
			if (Lib.isInclusion(hyp)) {
				final Expression set = Lib.getSet(hyp);
				if (Lib.isRel(set) || Lib.isFun(set)) {
					functionalHyps.add(hyp);
				}
			}
		}
		return functionalHyps;
	}

	/**
	 * Creates an instance of InDomManager for the first domain occurrence in
	 * the given goal.
	 * <p>
	 * The given goal MUST have at least one occurrence of a domain. In case
	 * several occurrences are found, the first one is considered.
	 * </p>
	 * 
	 * @param goal
	 *            Goal of the sequent
	 * @return a set of InDomManager
	 */
	public static InDomGoalManager createInDomManager(final Predicate goal) {
		final List<IPosition> domPositions = TacticsLib.findDomExpression(goal);
		assert !domPositions.isEmpty();
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

}
