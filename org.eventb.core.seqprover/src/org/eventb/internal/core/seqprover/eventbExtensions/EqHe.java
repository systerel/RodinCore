/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactoring around a hierarchy of classes
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeForwardInfHypAction;
import static org.eventb.core.seqprover.eventbExtensions.DLib.mDLib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;

/**
 * This class handles reasoners Eq and He.
 */
public abstract class EqHe extends HypothesisReasoner {

	private boolean goalDependant = true;

	/**
	 * Gets the expression that will be replaced.
	 * 
	 * @param hyp
	 *            the equality predicate in hypothesis
	 * @return the expression that will be replaced, either lhs or rhs of the
	 *         equality depending on the considered reasoner
	 */
	protected abstract Expression getFrom(Predicate hyp);

	/**
	 * Gets the expression that will be the replacement.
	 * 
	 * @param hyp
	 *            the equality predicate in hypothesis
	 * @return the expression that will be replaced, either lhs or rhs of the
	 *         equality depending on the considered reasoner
	 */
	protected abstract Expression getTo(Predicate hyp);

	@Override
	protected boolean isGoalDependent(IProverSequent sequent, Predicate pred) {
		return goalDependant;
	}

	@Override
	protected IAntecedent[] getAntecedents(IProverSequent sequent,
			Predicate hypEq) throws IllegalArgumentException {

		final FormulaFactory ff = sequent.getFormulaFactory();
		final DLib dlib = mDLib(ff);

		if (hypEq == null) {
			throw new IllegalArgumentException("Nonexistent hypothesis");
		}
		if (!Lib.isEq(hypEq)) {
			throw new IllegalArgumentException(
					"Hypothesis is not an equality: " + hypEq);
		}

		final Expression from = getFrom(hypEq);
		final Expression to = getTo(hypEq);

		final List<IHypAction> rewrites = new ArrayList<IHypAction>();
		final Set<Predicate> toDeselect = new LinkedHashSet<Predicate>();
		final Set<Predicate> toSelect = new LinkedHashSet<Predicate>();

		for (Predicate hyp : sequent.selectedHypIterable()) {

			if (hyp.equals(hypEq)) {
				continue;
			}

			final Predicate rewritten = dlib.rewrite(hyp, from, to);

			if (sequent.containsHypothesis(rewritten)) {
				// if the sequent contains the rewritten hypothesis, we have to
				// consider the case where the hypothesis is not selected. In
				// this case, it becomes selected
				if (!sequent.isSelected(rewritten)) {
					toSelect.add(rewritten);
					toDeselect.add(hyp);
				}
				continue;
			}

			rewrites.add(makeForwardInfHypAction(Collections.singleton(hyp),
					Collections.singleton(rewritten)));
			toDeselect.add(hyp);
		}

		if (!toDeselect.isEmpty()) {
			rewrites.add(ProverFactory.makeDeselectHypAction(toDeselect));
		}
		if (!toSelect.isEmpty()) {
			rewrites.add(ProverFactory.makeSelectHypAction(toSelect));
		}

		Predicate newGoal = dlib.rewrite(sequent.goal(), from, to);
		// if the goal is not rewritten, goal dependency is updated
		goalDependant = newGoal != sequent.goal();

		if (!goalDependant) {
			newGoal = null;
			if (rewrites.isEmpty()) {
				// when both goal and hypotheses are unchanged, the reasoner
				// application fails
				throw new IllegalArgumentException("Nothing to replace");
			}
		}

		final IAntecedent antecedent = makeAntecedent(newGoal, null, null,
				rewrites);

		return new IAntecedent[] { antecedent };
	}
}
