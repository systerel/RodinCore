package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

public abstract class AbstractManualRewrites implements IReasoner {

	public static class Input implements IReasonerInput {

		Predicate pred;

		IPosition position;

		/**
		 * The parameter is the hypothesis to rewrite. If <code>null</code>,
		 * the rewriting will be applied to the goal.
		 * 
		 * @param pred
		 *            hypothesis to rewrite or <code>null</code>
		 */
		public Input(Predicate pred, IPosition position) {
			this.pred = pred;
			this.position = position;
		}

		public void applyHints(ReplayHints renaming) {
			if (pred != null)
				pred = renaming.applyHints(pred);
		}

		public String getError() {
			return null;
		}

		public boolean hasError() {
			return false;
		}

	}

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput reasonerInput,
			IProofMonitor pm) {
		final Input input = (Input) reasonerInput;
		final Predicate hyp = input.pred;
		final IPosition position = input.position;

		final Predicate goal = seq.goal();
		if (hyp == null) {
			// Goal rewriting
			final Predicate[] newGoals = rewrite(goal, position);
			if (newGoals == null) {
				return ProverFactory.reasonerFailure(this, input, "Rewriter "
						+ getReasonerID() + " inapplicable for goal "
						+ goal);
			}

			final int length = newGoals.length;
			IAntecedent[] antecedents = new IAntecedent[length];
			for (int i = 0; i < length; ++i) {
				antecedents[i] = ProverFactory.makeAntecedent(newGoals[i]);
			}
			return ProverFactory.makeProofRule(this, input, goal,
					getDisplayName(hyp, position), antecedents);
		} else {
			// Hypothesis rewriting
			if (! seq.containsHypothesis(hyp)) {
				return ProverFactory.reasonerFailure(this, input,
						"Nonexistent hypothesis: " + hyp);
			}
			
			final Predicate[] rewriteOutput = rewrite(hyp, position);
			if (rewriteOutput == null) {
				return ProverFactory.reasonerFailure(this, input, "Rewriter "
						+ getReasonerID() + " inapplicable for hypothesis "
						+ hyp);
			}

//			final HashSet<Predicate> predSet = new HashSet<Predicate>(Arrays
//					.asList(newHyps));
//			final IAntecedent[] antecedents = new IAntecedent[] { ProverFactory
//					.makeAntecedent(goal, predSet, getHypAction(pred)) };
//			return ProverFactory.makeProofRule(this, input, goal, hyp,
//					getDisplayName(pred), antecedents);
			
			final List<Predicate> newHyps = Arrays.asList(rewriteOutput);
			final IHypAction forwardInf = ProverFactory.makeForwardInfHypAction(Collections.singleton(hyp), newHyps);
			List<IHypAction> hypActions = 
				Arrays.asList(
					forwardInf,
					getHypAction(hyp, position),
					ProverFactory.makeSelectHypAction(newHyps));
			return ProverFactory.makeProofRule(this, input, getDisplayName(hyp, position), hypActions);
			
//			
//			final IAntecedent[] antecedents = 
//				new IAntecedent[] { ProverFactory.makeAntecedent(
//						goal,
//						Arrays.asList(
//								forwardInf,
//								getHypAction(hyp),
//								ProverFactory.makeSelectHypAction(newHyps))) };
//			return ProverFactory.makeProofRule(this, input, goal,
//			getDisplayName(hyp), antecedents);
			
		}
	}

	/**
	 * Apply the rewriting to the given predicate.
	 * 
	 * @param pred
	 *     predicate to rewrite
	 * @return an array of predicates which are the result of rewriting
	 */
	protected abstract Predicate[] rewrite(Predicate pred, IPosition position);
	
	/**
	 * Returns the name to display in the generated rule.
	 * 
	 * @param pred
	 *            the hypothesis predicate that gets rewritten or
	 *            <code>null</code> if it is the goal that gets rewritten
	 * @return the name to display in the rule
	 */
	protected abstract String getDisplayName(Predicate pred, IPosition position);
	
	/**
	 * Returns the action to perform on hypotheses.
	 * 
	 * @param pred
	 *            the hypothesis predicate that gets rewritten
	 * @return the action to perform on hypotheses.
	 */
	protected abstract IHypAction getHypAction(Predicate pred, IPosition position);
	

	/**
	 * Returns whether this reasoner is applicable to the given predicate.
	 * 
	 * @param pred
	 *            the predicate to test for rewritability
	 * @return <code>true</code> if the given predicate might get rewritten by
	 *         this reasoner
	 */
	public abstract boolean isApplicable(Predicate pred, IPosition position);

	public final IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		
		Set<Predicate> neededHyps = reader.getNeededHyps();
		final int length = neededHyps.size();
		if (length == 0) {
			// Goal rewriting
			return new Input(null, null);
		}
		// Hypothesis rewriting
		if (length != 1) {
			throw new SerializeException(new IllegalStateException(
					"Expected exactly one needed hypothesis!"));
		}
		Predicate pred = null;
		for (Predicate hyp: neededHyps) {
			pred = hyp;
		}
		return new Input(pred, null);
	}
	
	public final void serializeInput(IReasonerInput input,
			IReasonerInputWriter writer) throws SerializeException {

		// Nothing to do, all is in the generated rule
	}
	
}
