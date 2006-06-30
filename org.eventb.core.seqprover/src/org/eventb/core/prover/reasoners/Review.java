package org.eventb.core.prover.reasoners;

import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IProofRule;
import org.eventb.core.prover.Reasoner;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputFail;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.sequent.HypothesesManagement;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.HypothesesManagement.ActionType;

public class Review implements Reasoner{
	
	public String getReasonerID() {
		return "reviewed";
	}
	
	public ReasonerOutput apply(IProverSequent seq, ReasonerInput reasonerInput){
	
		// Organize Input
		Input input;
		if (reasonerInput instanceof SerializableReasonerInput){
			input = new Input((SerializableReasonerInput)reasonerInput);
		} 
		else input = (Input) reasonerInput;
		
		if (input.goal == null || input.hyps == null){
			input = new Input(seq.selectedHypotheses(),seq.goal(),input.reviewerConfidence);
		}
		
		if ((! (seq.goal().equals(input.goal))) ||
		   (! (seq.hypotheses().containsAll(input.hyps))))
		{
			ReasonerOutputFail reasonerOutput = new ReasonerOutputFail(this,input);
			reasonerOutput.error = "Reviewed sequent does not match";
			return reasonerOutput;
		}
		
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.neededHypotheses = input.hyps;
		reasonerOutput.goal = seq.goal();
		reasonerOutput.display = "reviewed (confidence "+input.reviewerConfidence+")";
		assert input.reviewerConfidence > 0;
		assert input.reviewerConfidence <= IProofRule.CONFIDENCE_REVIEWED;
		reasonerOutput.reasonerConfidence = input.reviewerConfidence;
		
		reasonerOutput.anticidents = new Anticident[0];
		
		return reasonerOutput;
	}
	
	public static class Input implements ReasonerInput{
		public final Set<Hypothesis> hyps;
		public final Predicate goal;
		// A reviewer confidence in the range 1 (lowest) to 5 (highest)
		public final int reviewerConfidence;
		
		public Input(Set<Hypothesis> hyps,Predicate goal, int reviewerConfidence){
			this.hyps = hyps;
			this.goal = goal;
			assert reviewerConfidence >= 1;
			assert reviewerConfidence <= 5;
			this.reviewerConfidence = reviewerConfidence;
		}
		
		public Input(int reviewerConfidence){
			this.hyps = null;
			this.goal = null;
			assert reviewerConfidence >= 1;
			assert reviewerConfidence <= 5;
			this.reviewerConfidence = reviewerConfidence;
		}
		
		public Input(SerializableReasonerInput serializableReasonerInput) {
			this.hyps = serializableReasonerInput.hypAction.getHyps();
			this.goal = serializableReasonerInput.getPredicate("goal");
			this.reviewerConfidence =  
				Integer.parseInt(serializableReasonerInput.getString("reviewerConfidence"));
			
		}
		
		public SerializableReasonerInput genSerializable() {
			SerializableReasonerInput serializableReasonerInput 
			= new SerializableReasonerInput();
			// the action type is irrelevant : just reusing code to store hypSets
			// inside hypActions.
			// TODO : maybe fix later
			serializableReasonerInput.hypAction =
				new HypothesesManagement.Action(ActionType.SELECT,this.hyps);
			serializableReasonerInput.putPredicate("goal",goal);
			serializableReasonerInput.putString("reviewerConfidence",String.valueOf(reviewerConfidence));
			return serializableReasonerInput;
		}
		
	}

}
