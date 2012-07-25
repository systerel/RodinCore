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

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;

/**
 * Common implementation for tactics that saturate hypotheses with tactic
 * <code>FunImageGoal</code> before attempting another set of tactics. If the
 * attempt fails, the proof tree is restored to its original state.
 * 
 * @author Laurent Voisin
 */
public abstract class FunImageGoalAttempt implements ITactic {

	@Override
	public Object apply(IProofTreeNode initialNode, IProofMonitor pm) {
		final IProverSequent sequent = initialNode.getSequent();
		final Predicate goal = sequent.goal();
		if (!isApplicable(goal)) {
			return "Tactic unapplicable";
		}
		final IProofTreeNode node = saturateWithFunImgGoal(initialNode, pm);
		if (pm != null && pm.isCanceled()) {
			return "Canceled";
		}
		final Object attemptResult = attemptProof(node, pm);
		if (attemptResult == null) {
			return null;
		}
		initialNode.pruneChildren();
		return attemptResult;
	}

	/**
	 * Returns whether this tactic is applicable.
	 * 
	 * @param goal
	 *            the goal of the initial node
	 * @return <code>true</code> if this tactic is applicable
	 */
	protected abstract boolean isApplicable(Predicate goal);

	/**
	 * Attempts to finish the proof after the hypotheses have been saturated.
	 * 
	 * @param node
	 *            proof tree node with saturated hypotheses
	 * @param pm
	 *            proof monitor
	 * @return <code>null</code> if the proof succeeded, an error object
	 *         otherwise
	 * @see ITactic#apply(IProofTreeNode, IProofMonitor)
	 */
	protected abstract Object attemptProof(IProofTreeNode node, IProofMonitor pm);

	private IProofTreeNode saturateWithFunImgGoal(IProofTreeNode initialNode,
			IProofMonitor pm) {
		final FunImgGoalApplier applier = new FunImgGoalApplier(initialNode, pm);
		applier.saturate();
		return applier.getProofTreeNode();
	}

}
