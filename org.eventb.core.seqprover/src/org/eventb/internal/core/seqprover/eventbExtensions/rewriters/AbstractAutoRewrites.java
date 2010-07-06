/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public abstract class AbstractAutoRewrites extends EmptyInputReasoner {

	private IFormulaRewriter rewriter;

	private boolean hideOriginal;
	
	protected AbstractAutoRewrites(IFormulaRewriter rewriter,
			boolean hideOriginal) {
		this.rewriter = rewriter;
		this.hideOriginal = hideOriginal;
	}
	
	public void reset_rewriter(IFormulaRewriter a_rewriter)
	{
		rewriter=a_rewriter;
	}

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		
		final List<IHypAction> hypActions = new ArrayList<IHypAction>();
		for (Predicate hyp : seq.visibleHypIterable()) {
			
			// Rewrite the hypothesis
			Predicate inferredHyp = recursiveRewrite(hyp);

			Collection<Predicate> inferredHyps = Lib
					.breakPossibleConjunct(inferredHyp);

			// Check if rewriting made a change
			if (inferredHyp == hyp && inferredHyps.size() == 1)
				continue;

			// Check if rewriting generated something interesting
			inferredHyps.remove(Lib.True);

			Collection<Predicate> originalHyps = Collections.singleton(hyp);

			// Hide the original if the inferredHyps is empty, i.e. the
			// hypothesis get rewritten to Lib.True.
			if (inferredHyps.isEmpty() && hideOriginal) {
				hypActions.add(ProverFactory.makeHideHypAction(originalHyps));
				continue;
			}
				
			// Check if rewriting generated something new
			if (seq.containsHypotheses(inferredHyps)) {
				// IMPORTANT: Do NOT de-select the original if the inferred
				// hypotheses already exist.

				// Do NOT re-select the inferred hyps
				continue;
			}


			// make the forward inference action
			if (!inferredHyps.isEmpty())
				hypActions.add(ProverFactory.makeForwardInfHypAction(
						originalHyps, inferredHyps));

			// Hide the original hypothesis.
			// IMPORTANT: Do it after the forward inference hypothesis action
			if (hideOriginal)
				hypActions.add(ProverFactory.makeHideHypAction(originalHyps));
		}

		Predicate goal = seq.goal();
		Predicate newGoal = recursiveRewrite(goal);

		if (newGoal != goal) {
			IAntecedent[] antecedent = new IAntecedent[] { ProverFactory
					.makeAntecedent(newGoal, null, null, hypActions) };
			return ProverFactory.makeProofRule(this, input, goal, null, null,
					getDisplayName(), antecedent);
		}
		if (!hypActions.isEmpty()) {
			return ProverFactory.makeProofRule(this, input, getDisplayName(),
					hypActions);
		}
		return ProverFactory.reasonerFailure(this, input,
				"No rewrites applicable");
	}

	/**
	 * An utility method which try to rewrite a predicate recursively until
	 * reaching a fix-point.
	 * <p>
	 * If no rewrite where performed on this predicate, then a reference to this
	 * predicate is returned (rather than a copy of this predicate). This allows
	 * to test efficiently (using <code>==</code>) whether rewriting made any
	 * change.
	 * </p>
	 * 
	 * <p>
	 * 
	 * @param pred
	 *            the input predicate
	 * @return the resulting predicate after rewrite.
	 */
	private Predicate recursiveRewrite(Predicate pred) {
		Predicate resultPred;
		resultPred = pred.rewrite(rewriter);
		while (resultPred != pred) {
			pred = resultPred;
			resultPred = pred.rewrite(rewriter);
		}
		return resultPred;
	}
	
	protected abstract String getDisplayName();

}
