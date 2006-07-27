package org.eventb.core.prover.reasoners;

import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.Reasoner;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputFail;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

public class Contr implements Reasoner{
	
	public String getReasonerID() {
		return "contr";
	}
	
	public ReasonerOutput apply(IProverSequent seq,ReasonerInput reasonerInput){
		
		Input input;
		if (reasonerInput instanceof SerializableReasonerInput){
			input = new Input((SerializableReasonerInput)reasonerInput);
		} 
		else input = (Input) reasonerInput;
		
		Hypothesis falseHyp = input.falseHyp;
		Predicate falseHypPred = input.falseHyp.getPredicate();
		
		if ((!falseHypPred.equals(Lib.True)) && (! seq.hypotheses().contains(falseHyp)))
		return new ReasonerOutputFail(this,input,
					"Nonexistent hypothesis:"+falseHyp);

		// Generate the successful reasoner output
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		if (falseHypPred.equals(Lib.True))
		{
			reasonerOutput.display = "ct goal";
		}
		else 
		{
			reasonerOutput.display = "ct hyp ("+input.falseHyp+")";
			reasonerOutput.neededHypotheses.add(falseHyp);
		}
		reasonerOutput.goal = seq.goal();

		// Generate the anticident
		reasonerOutput.anticidents = new Anticident[1];
		reasonerOutput.anticidents[0] = new Anticident();
		reasonerOutput.anticidents[0].addedHypotheses.add(Lib.makeNeg(seq.goal()));
		if (falseHypPred.equals(Lib.True))
			reasonerOutput.anticidents[0].subGoal = Lib.False;
		else
			reasonerOutput.anticidents[0].subGoal = Lib.makeNeg(falseHypPred);
		return reasonerOutput;
	}
	
	
	public static class Input implements ReasonerInput{
		
		Hypothesis falseHyp;
		
		public Input(Hypothesis falseHyp){
			this.falseHyp = falseHyp;
		}
		
		public Input(){
			this.falseHyp = new Hypothesis(Lib.True);
		}
		
		public Input(SerializableReasonerInput serializableReasonerInput) {
			this.falseHyp = new Hypothesis(serializableReasonerInput.getPredicate("falseHyp"));
		}
		
		public SerializableReasonerInput genSerializable(){
			SerializableReasonerInput serializableReasonerInput 
			= new SerializableReasonerInput();
			serializableReasonerInput.putPredicate("falseHyp",falseHyp.getPredicate());
			return serializableReasonerInput;
		}
		
	}

}
