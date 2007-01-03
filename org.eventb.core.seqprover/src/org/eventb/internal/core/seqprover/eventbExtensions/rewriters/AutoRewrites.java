package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.FormulaFactory;
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

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".autoRewrites";

	public String getReasonerID() {
		return REASONER_ID;
	}

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		Iterable<Predicate> hypIterable = seq.hypIterable();
		IFormulaRewriter rewriter = new AutoRewritePredicate(true,
				FormulaFactory.getDefault());

		List<IHypAction> hypActions = new ArrayList<IHypAction>();
		for (Predicate pred : hypIterable) {
			Predicate newPred = pred.rewrite(rewriter);
			if (newPred != pred) {
				Collection<Predicate> inferredHyps = new ArrayList<Predicate>();
				Collection<Predicate> neededHyps = new ArrayList<Predicate>();
				inferredHyps.add(newPred);
				neededHyps.add(pred);
				hypActions.add(ProverFactory.makeForwardInfHypAction(
						neededHyps, inferredHyps));
				hypActions.add(ProverFactory.makeHideHypAction(neededHyps));
			}
		}

		Predicate goal = seq.goal();
		Predicate newGoal = goal.rewrite(rewriter);
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

	class AutoRewritePredicate extends DefaultRewriter {

		public AutoRewritePredicate(boolean autoFlattening, FormulaFactory ff) {
			super(autoFlattening, ff);
		}

		@Override
		public Predicate rewrite(AssociativePredicate predicate) {
			int tag = predicate.getTag();

			return removeAssociative(predicate,
					tag == AssociativePredicate.LAND ? Lib.True : Lib.False);
		}

		private Predicate removeAssociative(AssociativePredicate originalPred,
				Predicate toBeRemoved) {
			Predicate[] subPreds = originalPred.getChildren();
			List<Predicate> predicates = new ArrayList<Predicate>();
			boolean rewrite = false;
			for (Predicate subPred : subPreds) {
				if (!subPred.equals(toBeRemoved)) {
					predicates.add(subPred);
				} else {
					rewrite = true;
				}
			}

			if (rewrite) {
				AssociativePredicate newPred = this.getFactory()
						.makeAssociativePredicate(originalPred.getTag(),
								predicates, originalPred.getSourceLocation());
				return newPred;
			}
			return super.rewrite(originalPred);
		}
	}
}
