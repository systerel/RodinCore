package org.eventb.core.seqprover.reasoners;

import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

public class Review implements IReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".review";
	
	public static class ReviewInput implements IReasonerInput {

		Set<Hypothesis> hyps;
		Predicate goal;
		int confidence;

		// TODO add check on confidence parameter
		public ReviewInput(IProverSequent sequent, int confidence) {
			this.hyps = sequent.selectedHypotheses();
			this.goal = sequent.goal();
			this.confidence = confidence;
		}
		
		// TODO add checks or remove this method.
		public ReviewInput(Predicate[] hyps, Predicate[] goals,
				String confidence) {

			this.hyps = Hypothesis.Hypotheses(hyps);
			this.goal = goals[0];
			this.confidence = Integer.parseInt(confidence);
		}

		public void applyHints(ReplayHints hints) {
			Predicate[] newPreds = new Predicate[hyps.size()];
			int i = 0;
			for (Hypothesis hyp: hyps) {
				newPreds[i++] = hints.applyHints(hyp.getPredicate());
			}
			hyps = Hypothesis.Hypotheses(newPreds);
			goal = hints.applyHints(goal);
		}

		public String getError() {
			return null;
		}

		public boolean hasError() {
			return false;
		}

		public void serialize(IReasonerInputWriter writer)
				throws SerializeException {

			Predicate[] preds = new Predicate[hyps.size()];
			int i = 0;
			for (Hypothesis hyp: hyps) {
				preds[i++] = hyp.getPredicate();
			}
			writer.putPredicates("hyps", preds);
			writer.putPredicates("goal", goal);
			writer.putString("conf", Integer.toString(confidence));
		}
		
	}
	
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		
		return new ReviewInput(
				reader.getPredicates("hyps"),
				reader.getPredicates("goal"),
				reader.getString("conf")
		);
	}
	
	public IReasonerOutput apply(IProverSequent seq,
			IReasonerInput reasonerInput, IProofMonitor pm) {
	
		// Organize Input
		ReviewInput input = (ReviewInput) reasonerInput;
		
		Set<Hypothesis> hyps = input.hyps;
		Predicate goal = input.goal;
		int reviewerConfidence = input.confidence;
		
		if ((! (seq.goal().equals(goal))) ||
		   (! (seq.hypotheses().containsAll(hyps)))) {
			return ProverFactory.reasonerFailure(this, input,
					"Reviewed sequent does not match");
		}
		
		assert reviewerConfidence > 0;
		assert reviewerConfidence <= IConfidence.REVIEWED_MAX;
	
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				hyps,
				reviewerConfidence,
				"rv (confidence "+reviewerConfidence+")",
				new IAntecedent[0]);		
		
		return reasonerOutput;
	}
	
}
