package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
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
import org.eventb.internal.core.seqprover.ForwardInfHypAction;

public abstract class AbstractManualRewrites implements IReasoner {

	private final static String POSITION_KEY = "pos";

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

	public IReasonerOutput apply(IProverSequent seq,
			IReasonerInput reasonerInput, IProofMonitor pm) {
		final Input input = (Input) reasonerInput;
		final Predicate hyp = input.pred;
		final IPosition position = input.position;

		final Predicate goal = seq.goal();
		if (hyp == null) {
			// Goal rewriting
			final Predicate[] newGoals = rewrite(goal, position);
			if (newGoals == null) {
				return ProverFactory.reasonerFailure(this, input, "Rewriter "
						+ getReasonerID() + " inapplicable for goal " + goal);
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
			if (!seq.containsHypothesis(hyp)) {
				return ProverFactory.reasonerFailure(this, input,
						"Nonexistent hypothesis: " + hyp);
			}

			final Predicate[] rewriteOutput = rewrite(hyp, position);
			if (rewriteOutput == null) {
				return ProverFactory.reasonerFailure(this, input, "Rewriter "
						+ getReasonerID() + " inapplicable for hypothesis "
						+ hyp);
			}
			final List<Predicate> newHyps = Arrays.asList(rewriteOutput);
			final IHypAction forwardInf = ProverFactory
					.makeForwardInfHypAction(Collections.singleton(hyp),
							newHyps);
			List<IHypAction> hypActions = Arrays.asList(forwardInf,
					getHypAction(hyp, position), ProverFactory
							.makeSelectHypAction(newHyps));
			return ProverFactory.makeProofRule(this, input, getDisplayName(hyp,
					position), hypActions);

		}
	}

	/**
	 * Apply the rewriting to the given predicate.
	 * 
	 * @param pred
	 *            predicate to rewrite
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
	protected abstract IHypAction getHypAction(Predicate pred,
			IPosition position);

	public final IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {

		IAntecedent[] antecedents = reader.getAntecedents();
		
		String image = reader.getString(POSITION_KEY);
		IPosition position = FormulaFactory.getDefault().makePosition(image);

		final int length = antecedents.length;
		if (length != 1) {
			// Goal rewriting
			return new Input(null, position);
		}
		// Hypothesis rewriting
		else if (length == 0) {
			throw new SerializeException(new IllegalStateException(
					"Expected exactly one needed hypothesis!"));
		}
		IAntecedent antecedent = antecedents[0];
		List<IHypAction> hypActions = antecedent.getHypAction();
		if (hypActions.size() == 0) {
			throw new SerializeException(new IllegalStateException(
					"Expected at least one hyp action!"));
		}
		IHypAction hypAction = hypActions.get(0);
		if (hypAction instanceof ForwardInfHypAction) {
			ForwardInfHypAction fHypAction = (ForwardInfHypAction) hypAction;
			Collection<Predicate> hyps = fHypAction.getHyps();
			if (hyps.size() != 1) {
				throw new SerializeException(new IllegalStateException(
						"Expected single required hyp in first forward hyp action!"));
			}
			return new Input(hyps.iterator().next(), position);
		}
		else {
			throw new SerializeException(new IllegalStateException(
					"Expected first hyp action to be a forward hyp action!"));
		}
	}

	public final void serializeInput(IReasonerInput input,
			IReasonerInputWriter writer) throws SerializeException {

		// Serialise the position only, the predicate is contained inside the
		// rule
		writer.putString(POSITION_KEY, ((Input) input).position.toString());
	}

}
