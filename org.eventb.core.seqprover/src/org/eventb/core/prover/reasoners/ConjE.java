package org.eventb.core.prover.reasoners;

import java.util.Arrays;

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

public class ConjE implements Reasoner{
	
	public String getReasonerID() {
		return "conjE";
	}
	
	public ReasonerOutput apply(IProverSequent seq,ReasonerInput reasonerInput){
		
		Input input;
		if (reasonerInput instanceof SerializableReasonerInput){
			input = new Input((SerializableReasonerInput)reasonerInput);
		} 
		else input = (Input) reasonerInput;
		
		Hypothesis conjHyp = input.conjHyp;
		Predicate conjHypPred = input.conjHyp.getPredicate();
		
		
		if (! seq.hypotheses().contains(conjHyp))
			return new ReasonerOutputFail(this,input,
					"Nonexistent hypothesis:"+conjHyp);
		if (! Lib.isConj(conjHypPred))
			return new ReasonerOutputFail(this,input,
					"Hypothesis is not a conjunction:"+conjHyp);
		
		// Generate the successful reasoner output
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.display = "remove ∧ hyp ("+input.conjHyp+")";
		reasonerOutput.neededHypotheses.add(conjHyp);
		reasonerOutput.goal = seq.goal();

		// Generate the anticident
		Predicate[] conjuncts = Lib.conjuncts(conjHypPred);
		reasonerOutput.anticidents = new Anticident[1];
		reasonerOutput.anticidents[0] = new Anticident();
		reasonerOutput.anticidents[0].addedHypotheses.addAll(Arrays.asList(conjuncts));
		reasonerOutput.anticidents[0].hypAction.add(Lib.hide(conjHyp));
		reasonerOutput.anticidents[0].subGoal = seq.goal();
		
		return reasonerOutput;
	}
	
	
	public static class Input implements ReasonerInput{
		
		Hypothesis conjHyp;
		
		public Input(Hypothesis conjHyp){
			this.conjHyp = conjHyp;
		}
		
		public Input(SerializableReasonerInput serializableReasonerInput) {
			this.conjHyp = new Hypothesis(serializableReasonerInput.getPredicate("conjHyp"));
		}
		
		public SerializableReasonerInput genSerializable(){
			SerializableReasonerInput serializableReasonerInput 
			= new SerializableReasonerInput();
			serializableReasonerInput.putPredicate("conjHyp",conjHyp.getPredicate());
			return serializableReasonerInput;
		}
		
	}

}
