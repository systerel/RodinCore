/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
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

import static java.util.Collections.singleton;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeDeselectHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeForwardInfHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeSelectHypAction;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;

/**
 * This class handles reasoners Eq and He.
 */
public abstract class EqHe extends HypothesisReasoner implements
		IVersionedReasoner {

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

		if (hypEq == null) {
			throw new IllegalArgumentException("Nonexistent hypothesis");
		}
		if (!Lib.isEq(hypEq)) {
			throw new IllegalArgumentException(
					"Hypothesis is not an equality: " + hypEq);
		}

		final Rewriter rewriter = new Rewriter(hypEq, getFrom(hypEq),
				getTo(hypEq));
		final List<IHypAction> actions = rewriteHypotheses(sequent, rewriter);
		final Predicate newGoal = rewriteGoal(sequent, rewriter);
		goalDependant = newGoal != null;
		if (actions.isEmpty() && !goalDependant) {
			// When both goal and hypotheses are unchanged, the reasoner
			// application fails
			throw new IllegalArgumentException("Nothing to rewrite");
		}
		final IAntecedent antecedent = makeAntecedent(newGoal, null, null,
				actions);
		return new IAntecedent[] { antecedent };
	}

	private List<IHypAction> rewriteHypotheses(IProverSequent sequent,
			Rewriter rewriter) {
		final List<IHypAction> actions = new ArrayList<IHypAction>();
		final Set<Predicate> toDeselect = new LinkedHashSet<Predicate>();
		final Set<Predicate> toSelect = new LinkedHashSet<Predicate>();
		for (final Predicate hyp : sequent.selectedHypIterable()) {
			final Predicate rewritten = rewriter.rewrite(hyp);
			if (rewritten == null) {
				continue;
			}
			if (sequent.containsHypothesis(rewritten)) {
				// If the sequent already contains the rewritten hypothesis, we
				// have to consider the case where the hypothesis is not yet
				// selected. In this case, it becomes selected. Otherwise,
				// nothing happens
				if (!sequent.isSelected(rewritten)) {
					toSelect.add(rewritten);
					toDeselect.add(hyp);
				}
			} else {
				actions.add(makeForwardInfHypAction(singleton(hyp),
						singleton(rewritten)));
				toDeselect.add(hyp);
			}
		}
		if (!toDeselect.isEmpty()) {
			actions.add(makeDeselectHypAction(toDeselect));
		}
		if (!toSelect.isEmpty()) {
			actions.add(makeSelectHypAction(toSelect));
		}
		return actions;
	}

	private Predicate rewriteGoal(IProverSequent sequent, Rewriter rewriter) {
		return rewriter.rewrite(sequent.goal());
	}

	private static class Rewriter {

		private final Predicate source;
		private final Expression from;
		private final Expression to;

		public Rewriter(Predicate hypEq, Expression from, Expression to) {
			this.source = hypEq;
			this.from = from;
			this.to = to;
		}

		// Rewrites given predicate, returns null if nothing happens
		public Predicate rewrite(Predicate pred) {
			if (source.equals(pred)) {
				return null;
			}
			final Predicate rewritten = DLib.rewrite(pred, from, to);
			if (rewritten == pred) {
				return null;
			}
			return rewritten;
		}

	}

}
