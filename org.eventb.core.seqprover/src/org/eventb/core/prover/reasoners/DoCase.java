package org.eventb.core.prover.reasoners;

import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputFail;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.sequent.IProverSequent;

public class DoCase extends SinglePredInputReasoner{
	
	public String getReasonerID() {
		return "doCase";
	}
	
	public ReasonerOutput apply(IProverSequent seq,ReasonerInput reasonerInput){
		
		// Organize Input
		SinglePredInput input;
		if (reasonerInput instanceof SerializableReasonerInput){
			input = new SinglePredInput((SerializableReasonerInput)reasonerInput);
		} 
		else input = (SinglePredInput) reasonerInput;
		
		if (input.hasError())
		{
			return new ReasonerOutputFail(this,input,input.getError());
		}

		Predicate trueCase = input.getPredicate();
		// This check may be redone for replay since the type environment
		// may have shrunk, making the previous predicate with dangling free vars.
		
		// This check now done when constructing the sequent.. 
		// so the reasoner is successful, but the rule fails.
		
		//		if (! Lib.typeCheckClosed(trueCase,seq.typeEnvironment()))
		//			return new ReasonerOutputFail(this,input,
		//					"Type check failed for predicate: "+trueCase);
		
		
		// We can now assume that the true case has been properly parsed and typed.
		
		// Generate the well definedness condition for the true case
		Predicate trueCaseWD = Lib.WD(trueCase);
		
		// Generate the successful reasoner output
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.display = "dc ("+trueCase.toString()+")";
		reasonerOutput.goal = seq.goal();

		// Generate the anticidents
		reasonerOutput.anticidents = new Anticident[3];
		
		// Well definedness condition
		reasonerOutput.anticidents[0] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[0].subGoal = trueCaseWD;
		
		// The goal with the true case
		reasonerOutput.anticidents[1] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[1].addConjunctsToAddedHyps(trueCase);
		reasonerOutput.anticidents[1].subGoal = seq.goal();
		
		// The goal with the false case
		reasonerOutput.anticidents[2] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[2].addToAddedHyps(Lib.makeNeg(trueCase));
		reasonerOutput.anticidents[2].subGoal = seq.goal();	
				
		return reasonerOutput;
	}
	
	
//	public static class Input implements ReasonerInput{
//		
//		String trueCase;
//		Predicate trueCasePred;
//		
//		public Input(String trueCase){
//			this.trueCase = trueCase;
//			this.trueCasePred = null;
//		}
//
//		public Input(SerializableReasonerInput serializableReasonerInput) {
//			this.trueCase = null;
//			this.trueCasePred = serializableReasonerInput.getPredicate("trueCasePred");
//		}
//		
//		public SerializableReasonerInput genSerializable(){
//			SerializableReasonerInput serializableReasonerInput 
//			= new SerializableReasonerInput();
////			serializableReasonerInput.putString("lemma",lemma);
//			assert trueCasePred != null;
//			serializableReasonerInput.putPredicate("trueCasePred",trueCasePred);
//			return serializableReasonerInput;
//		}
//		
//	}

}
