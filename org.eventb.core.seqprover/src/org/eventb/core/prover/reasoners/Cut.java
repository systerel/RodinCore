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

public class Cut implements Reasoner{
	
	public String getReasonerID() {
		return "cut";
	}
	
	public ReasonerOutput apply(IProverSequent seq,ReasonerInput reasonerInput){
		
		// Organize Input
		Input input;
		if (reasonerInput instanceof SerializableReasonerInput){
			input = new Input((SerializableReasonerInput)reasonerInput);
		} 
		else input = (Input) reasonerInput;
		
		if (input.lemmaPred == null)
		{
			Predicate lemma = Lib.parsePredicate(input.lemma);
			if (lemma == null) 
				return new ReasonerOutputFail(this,input,
						"Parse error for lemma: "+ input.lemma);
			input.lemmaPred = lemma;
		}
		
		// This check may be redone for replay since the type environment
		// may have shrunk, making the previous predicate with dangling free vars.
		if (! Lib.typeCheckClosed(input.lemmaPred,seq.typeEnvironment()))
			return new ReasonerOutputFail(this,input,
					"Type check failed for lemma: "+input.lemma);
		
		// We can now assume that lemma has been properly parsed and typed.
		
		// Generate the well definedness condition for the lemma
		Predicate lemmaWD = Lib.WD(input.lemmaPred);
		
		// Generate the successful reasoner output
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.display = "ah ("+input.lemmaPred.toString()+")";
		reasonerOutput.goal = seq.goal();

		// Generate the anticidents
		reasonerOutput.anticidents = new Anticident[3];
		
		// Well definedness condition
		reasonerOutput.anticidents[0] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[0].subGoal = lemmaWD;
		
		// The lemma to be proven
		reasonerOutput.anticidents[1] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[1].subGoal = input.lemmaPred;
		
		// Proving the original goal with the help of the lemma
		reasonerOutput.anticidents[2] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[2].addedHypotheses.add(input.lemmaPred);
		reasonerOutput.anticidents[2].subGoal = seq.goal();
				
		return reasonerOutput;
		
	}
	
	
	public static class Input implements ReasonerInput{
		
		String lemma;
		Predicate lemmaPred;
		
		public Input(String lemma){
			this.lemma = lemma;
			this.lemmaPred = null;
		}

		public Input(SerializableReasonerInput serializableReasonerInput) {
			this.lemma = null;
			this.lemmaPred = serializableReasonerInput.getPredicate("lemmaPred");
		}
		
		public SerializableReasonerInput genSerializable(){
			SerializableReasonerInput serializableReasonerInput 
			= new SerializableReasonerInput();
//			serializableReasonerInput.putString("lemma",lemma);
			assert lemmaPred != null;
			serializableReasonerInput.putPredicate("lemmaPred",lemmaPred);
			return serializableReasonerInput;
		}
		
	}

}
