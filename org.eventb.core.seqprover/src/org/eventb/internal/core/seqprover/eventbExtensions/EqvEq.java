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

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;

/**
 * Abstract class for Equivalence and Equality rewriter
 * 
 * @author Josselin Dolhen
 */
public abstract class EqvEq<T extends Formula<T>> extends HypothesisReasoner {

	protected abstract class Rewriter {
		private final Predicate source;
		protected final T from;
		protected final T to;

		public Rewriter(Predicate hypEq, T from, T to) {
			this.source = hypEq;
			this.from = from;
			this.to = to;
		}

		protected abstract Predicate doRewrite(Predicate pred);

		// Rewrites given predicate, returns null if nothing happens
		public final Predicate rewrite(Predicate pred) {
			if (source.equals(pred)) {
				return null;
			}
			final Predicate rewritten = doRewrite(pred);
			if (rewritten == pred) {
				return null;
			}
			return rewritten;
		}

	}

	private boolean goalDependant = true;

	protected abstract int getTag();

	/**
	 * Gets the expression that will be replaced.
	 * 
	 * @param hyp
	 *            the equivalence/equality predicate in hypothesis
	 * @return the expression that will be replaced, either lhs or rhs of the
	 *         equality depending on the considered reasoner
	 */
	protected abstract T getFrom(Predicate hyp);

	/**
	 * Gets the expression that will be the replacement.
	 * 
	 * @param hyp
	 *            the equivalence/equality predicate in hypothesis
	 * @return the expression that will be replaced, either lhs or rhs of the
	 *         equality depending on the considered reasoner
	 */
	protected abstract T getTo(Predicate hyp);

	protected abstract Rewriter getRewriter(Predicate hyp, T from, T to);

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
		if (hypEq.getTag() != getTag()) {
			throw new IllegalArgumentException("Unsupported hypothesis: "
					+ hypEq);
		}

		final Rewriter rewriter = getRewriter(hypEq, getFrom(hypEq),
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

}
