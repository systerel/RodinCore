package org.eventb.core.prover.reasoners;

import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IConfidence;
import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.Reasoner;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputFail;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

public class Review implements Reasoner{
	
	public String getReasonerID() {
		return "review";
	}
	
	public ReasonerInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		IReasonerInputSerializer[] reasonerInputSerializers = reasonerInputSerializer.getSubInputSerializers();		
		return new CombiInput(
				new MultiplePredInput(reasonerInputSerializers[0]),
				new SinglePredInput(reasonerInputSerializers[1]),
				new SingleStringInput(reasonerInputSerializers[2])
		);
	}
	
	public ReasonerOutput apply(IProverSequent seq, ReasonerInput reasonerInput){
	
		// Organize Input
		CombiInput input = (CombiInput) reasonerInput;
		
		Set<Hypothesis> hyps = 
			Hypothesis.Hypotheses(
					((MultiplePredInput)input.getReasonerInputs()[0]).getPredicates());
		Predicate goal = ((SinglePredInput)input.getReasonerInputs()[1]).getPredicate();
		int reviewerConfidence = Integer.parseInt(((SingleStringInput)input.getReasonerInputs()[2]).getString());
		
		if ((! (seq.goal().equals(goal))) ||
		   (! (seq.hypotheses().containsAll(hyps))))
		{
			ReasonerOutputFail reasonerOutput = new ReasonerOutputFail(this,input);
			reasonerOutput.error = "Reviewed sequent does not match";
			return reasonerOutput;
		}
		
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.neededHypotheses = hyps;
		reasonerOutput.goal = seq.goal();
		reasonerOutput.display = "rv (confidence "+reviewerConfidence+")";
		assert reviewerConfidence > 0;
		assert reviewerConfidence <= IConfidence.REVIEWED_MAX;
		reasonerOutput.reasonerConfidence = reviewerConfidence;
		
		reasonerOutput.anticidents = new Anticident[0];
		
		return reasonerOutput;
	}
	
//	public static class Input implements ReasonerInput{
//		public final Set<Hypothesis> hyps;
//		public final Predicate goal;
//		// A reviewer confidence in the range 1 (lowest) to 5 (highest)
//		public final int reviewerConfidence;
//		
//		public Input(Set<Hypothesis> hyps,Predicate goal, int reviewerConfidence){
//			this.hyps = hyps;
//			this.goal = goal;
//			assert reviewerConfidence >= 1;
//			assert reviewerConfidence <= 5;
//			this.reviewerConfidence = reviewerConfidence;
//		}
//		
//		public Input(int reviewerConfidence){
//			this.hyps = null;
//			this.goal = null;
//			assert reviewerConfidence >= 1;
//			assert reviewerConfidence <= 5;
//			this.reviewerConfidence = reviewerConfidence;
//		}
//		
//		public Input(SerializableReasonerInput serializableReasonerInput) {
//			this.hyps = serializableReasonerInput.hypAction.getHyps();
//			this.goal = serializableReasonerInput.getPredicate("goal");
//			this.reviewerConfidence =  
//				Integer.parseInt(serializableReasonerInput.getString("reviewerConfidence"));
//			
//		}
//		
//		public SerializableReasonerInput genSerializable() {
//			SerializableReasonerInput serializableReasonerInput 
//			= new SerializableReasonerInput();
//			// the action type is irrelevant : just reusing code to store hypSets
//			// inside hypActions.
//			// TODO : maybe fix later
//			serializableReasonerInput.hypAction =
//				new HypothesesManagement.Action(ActionType.SELECT,this.hyps);
//			serializableReasonerInput.putPredicate("goal",goal);
//			serializableReasonerInput.putString("reviewerConfidence",String.valueOf(reviewerConfidence));
//			return serializableReasonerInput;
//		}
//		
//	}

}
