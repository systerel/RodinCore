package org.eventb.core.prover.reasoners;

import org.eventb.core.prover.Reasoner;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.HypothesesManagement.Action;

public class MngHyp implements Reasoner{
	
	public String getReasonerID() {
		return "mngHyp";
	}
	
	public ReasonerOutput apply(IProverSequent seq,ReasonerInput reasonerInput){
		
		//	 Organize Input
		Input input;
		if (reasonerInput instanceof SerializableReasonerInput){
			input = new Input((SerializableReasonerInput)reasonerInput);
		} 
		else input = (Input) reasonerInput;
		
		// remove absent hyps - useful for later replay
		input.action.getHyps().retainAll(seq.hypotheses());
		
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.goal = seq.goal();
		reasonerOutput.display = "manage hyps";
		reasonerOutput.anticidents = new Anticident[1];
		
		reasonerOutput.anticidents[0] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[0].hypAction.add(input.action);
		reasonerOutput.anticidents[0].subGoal = seq.goal();
				
		return reasonerOutput;
	}
	
	public static class Input implements ReasonerInput{
		public final Action action;
		
		public Input(Action action){
			this.action = action;
		}
		
		public Input(SerializableReasonerInput serializableReasonerInput) {
			this.action = serializableReasonerInput.hypAction;
		}
		
		public SerializableReasonerInput genSerializable() {
			SerializableReasonerInput serializableReasonerInput 
			= new SerializableReasonerInput();
			assert action != null;
			serializableReasonerInput.hypAction = action;
			return serializableReasonerInput;
		}
		
	}

}
