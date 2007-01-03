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
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public class RewritePred extends EmptyInputReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".rewritePred";

	public String getReasonerID() {
		return REASONER_ID;
	}

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		System.out.println("Calling REWRITE PRED");
		Iterable<Predicate> hypIterable = seq.hypIterable();
		IFormulaRewriter rewriter = new RewritePredicate(true, FormulaFactory
				.getDefault());

		List<IHypAction> hypActions = new ArrayList<IHypAction>();
		for (Predicate pred : hypIterable) {
			Predicate newPred = pred.rewrite(rewriter);
			// System.out.println("Rewrite " + pred + " to " + newPred);
			if (newPred != pred) {
				System.out.println("Rewrite " + pred + " to " + newPred);
				Collection<Predicate> inferredHyps = new ArrayList<Predicate>();
				Collection<Predicate> neededHyps = new ArrayList<Predicate>();
				inferredHyps.add(newPred);
				neededHyps.add(pred);
				hypActions.add(ProverFactory.makeForwardInfHypAction(
						neededHyps, inferredHyps));
			}
		}

		Predicate goal = seq.goal();
		Predicate newGoal = goal.rewrite(rewriter);
		if (newGoal == goal)
			newGoal = null;

		if (newGoal == null && hypActions.isEmpty()) {
			return ProverFactory.reasonerFailure(this, input,
					"There are no trivial rewritings");
		} else {
			if (newGoal != null) System.out.println("Rewrite goal " + goal + " to " + newGoal);
			return ProverFactory.makeProofRule(this, input, newGoal, "rewrite", hypActions);
		}

	}

	class RewritePredicate extends DefaultRewriter {

		public RewritePredicate(boolean autoFlattening, FormulaFactory ff) {
			super(autoFlattening, ff);
		}

		@Override
		public Predicate rewrite(AssociativePredicate predicate) {
			int tag = predicate.getTag();
			if (tag != AssociativePredicate.LAND)
				return super.rewrite(predicate);

			Predicate[] subPreds = predicate.getChildren();
			List<Predicate> predicates = new ArrayList<Predicate>();
			boolean rewrite = false;
			for (Predicate subPred : subPreds) {
				System.out.println("Sub Pred " + subPred);
				if (!subPred.equals(ASTLib.True)) {
					predicates.add(subPred);
				} else {
					rewrite = true;
				}
			}

			System.out.println("Is rewrite " + rewrite);
			if (rewrite) {
				AssociativePredicate newPred = this.getFactory()
						.makeAssociativePredicate(tag, predicates,
								predicate.getSourceLocation());
				return newPred;
			}
			return super.rewrite(predicate);
		}
	}
}
