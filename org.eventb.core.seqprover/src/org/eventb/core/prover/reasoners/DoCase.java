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
import org.eventb.core.prover.sequent.IProverSequent;

public class DoCase implements Reasoner{
	
	public String getReasonerID() {
		return "doCase";
	}
	
	public ReasonerOutput apply(IProverSequent seq,ReasonerInput reasonerInput){
		
		// Organize Input
		Input input;
		if (reasonerInput instanceof SerializableReasonerInput){
			input = new Input((SerializableReasonerInput)reasonerInput);
		} 
		else input = (Input) reasonerInput;
		
		// Parse and write into input 
		if (input.trueCasePred == null)
		{
			Predicate trueCasePred = Lib.parsePredicate(input.trueCase);
			if (trueCasePred == null) 
				return new ReasonerOutputFail(this,input,
						"Parse error for case: "+ input.trueCase);
			if (! Lib.isWellTyped(trueCasePred,seq.typeEnvironment()))
				return new ReasonerOutputFail(this,input,
						"Type check failed for case: "+input.trueCase);
			input.trueCasePred = trueCasePred;
		}
		
		
		// We can now assume that the true case has been properly parsed and typed.
		
		// Generate the well definedness condition for the true case
		Predicate trueCaseWD = Lib.WD(input.trueCasePred);
		
		// Generate the successful reasoner output
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.display = "dc ("+input.trueCasePred.toString()+")";
		reasonerOutput.goal = seq.goal();

		// Generate the anticidents
		reasonerOutput.anticidents = new Anticident[3];
		
		// Well definedness condition
		reasonerOutput.anticidents[0] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[0].subGoal = trueCaseWD;
		
		// The goal with the true case
		reasonerOutput.anticidents[1] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[1].addedHypotheses.add(input.trueCasePred);
		reasonerOutput.anticidents[1].subGoal = seq.goal();
		
		// The goal with the false case
		reasonerOutput.anticidents[2] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[2].addedHypotheses.add(Lib.makeNeg(input.trueCasePred));
		reasonerOutput.anticidents[2].subGoal = seq.goal();	
				
		return reasonerOutput;
	}
	
	
	public static class Input implements ReasonerInput{
		
		String trueCase;
		Predicate trueCasePred;
		
		public Input(String trueCase){
			this.trueCase = trueCase;
			this.trueCasePred = null;
		}

		public Input(SerializableReasonerInput serializableReasonerInput) {
			this.trueCase = null;
			this.trueCasePred = serializableReasonerInput.getPredicate("trueCasePred");
		}
		
		public SerializableReasonerInput genSerializable(){
			SerializableReasonerInput serializableReasonerInput 
			= new SerializableReasonerInput();
//			serializableReasonerInput.putString("lemma",lemma);
			assert trueCasePred != null;
			serializableReasonerInput.putPredicate("trueCasePred",trueCasePred);
			return serializableReasonerInput;
		}
		
	}

}
