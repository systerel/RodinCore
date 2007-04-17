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
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public class AutoRewrites extends EmptyInputReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".autoRewrites";

	public String getReasonerID() {
		return REASONER_ID;
	}

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {

		final IFormulaRewriter rewriter = new AutoRewriterImpl();

		final List<IHypAction> hypActions = new ArrayList<IHypAction>();
		for (Predicate hyp : seq.visibleHypIterable()) {
			
			// Rewrite the hypothesis
			Predicate inferredHyp = recursiveRewrite(hyp, rewriter);
			Collection<Predicate> inferredHyps = Lib.breakPossibleConjunct(inferredHyp);

			// Check if rewriting made a change
			if (inferredHyp == hyp && inferredHyps.size() == 1) continue;
			// Check if rewriting generated something interesting
			if (inferredHyp.getTag() == Predicate.BTRUE) continue;
			
			// Check if rewriting generated something new
			if (seq.containsHypotheses(inferredHyps)){
				// if the original hyp was selected then...
				if (seq.isSelected(hyp)){
					// hide it and...
					hypActions.add(ProverFactory.makeHideHypAction(Collections.singleton(hyp)));
					// select the inferred hyps
					hypActions.add(ProverFactory.makeSelectHypAction(inferredHyps));
				}
				continue;
			}

			// make the forward inference action
			Collection<Predicate> originalHyps = Collections.singleton(hyp);
			hypActions.add(ProverFactory.makeForwardInfHypAction(
					originalHyps, inferredHyps));

			// Hide the original hypothesis. IMPORTANT: Do it after the
			// forward inference hypothesis action
			hypActions.add(ProverFactory.makeHideHypAction(originalHyps));

		}

		Predicate goal = seq.goal();
		Predicate newGoal = recursiveRewrite(goal, rewriter);

		if (newGoal != goal) {
			IAntecedent[] antecedent = new IAntecedent[] { ProverFactory
					.makeAntecedent(newGoal, null, null, hypActions) };
			return ProverFactory.makeProofRule(this, input, goal, null, null,
					"auto rewrite", antecedent);
		}
		if (!hypActions.isEmpty()) {
			return ProverFactory.makeProofRule(this, input, "auto rewrite",
					hypActions);
		}
		return ProverFactory.reasonerFailure(this, input,
				"No auto rewrites applicable");
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
	 * @param rewriter
	 *            a rewriter which is used to rewrite the input predicate
	 * @return the resulting predicate after rewrite.
	 */
	private Predicate recursiveRewrite(Predicate pred, IFormulaRewriter rewriter) {
		Predicate resultPred;
		resultPred = pred.rewrite(rewriter);
		while (resultPred != pred) {
			pred = resultPred;
			resultPred = pred.rewrite(rewriter);
		}
		return resultPred;
	}

	// class AutoRewritePredicate extends DefaultRewriter {
	//
	// public AutoRewritePredicate(boolean autoFlattening, FormulaFactory ff) {
	// super(autoFlattening, ff);
	// }
	//
	// @Override
	// public Predicate rewrite(AssociativePredicate predicate) {
	// int tag = predicate.getTag();
	//
	// Predicate neutral = tag == AssociativePredicate.LAND ? Lib.True
	// : Lib.False;
	// Predicate determinant = tag == AssociativePredicate.LAND ? Lib.False
	// : Lib.True;
	// return removeAssociative(predicate, neutral, determinant);
	// }
	//
	// private Predicate removeAssociative(AssociativePredicate predicate,
	// Predicate neutral, Predicate dominant) {
	// Predicate[] subPreds = predicate.getChildren();
	// List<Predicate> predicates = new ArrayList<Predicate>();
	// boolean rewrite = false;
	// for (Predicate subPred : subPreds) {
	// if (subPred.equals(dominant))
	// return dominant;
	// if (subPred.equals(neutral)) {
	// rewrite = true;
	// } else {
	// predicates.add(subPred);
	// }
	// }
	//
	// if (rewrite) {
	// if (predicates.size() == 0) {
	// return neutral;
	// }
	// AssociativePredicate newPred = this.getFactory()
	// .makeAssociativePredicate(predicate.getTag(),
	// predicates, predicate.getSourceLocation());
	// return newPred;
	// }
	// return super.rewrite(predicate);
	// }
	// }
}
