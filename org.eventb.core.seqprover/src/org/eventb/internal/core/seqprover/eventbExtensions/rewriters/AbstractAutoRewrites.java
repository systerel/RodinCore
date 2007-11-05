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

			// Check if rewriting generated something new
			if (seq.containsHypotheses(inferredHyps)) {
				// Hide the original hypothesis.
				if (hideOriginal)
					hypActions.add(ProverFactory.makeHideHypAction(Collections
							.singleton(hyp)));

				// Do NOT re-select the inferred hyps
				continue;
			}

			Collection<Predicate> originalHyps = Collections.singleton(hyp);

			// make the forward inference action
			if (!inferredHyps.isEmpty())
				hypActions.add(ProverFactory.makeForwardInfHypAction(
						originalHyps, inferredHyps));

			// Hide the original hypothesis. IMPORTANT: Do it after the
			// forward inference hypothesis action
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
