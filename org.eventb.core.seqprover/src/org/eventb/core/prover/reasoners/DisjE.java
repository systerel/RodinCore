package org.eventb.core.prover.reasoners;

import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputFail;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

public class DisjE extends SinglePredInputReasoner{
	
	public String getReasonerID() {
		return "disjE";
	}
	
	public ReasonerOutput apply(IProverSequent seq,ReasonerInput reasonerInput){
		
		SinglePredInput input;
		if (reasonerInput instanceof SerializableReasonerInput){
			input = new SinglePredInput((SerializableReasonerInput)reasonerInput);
		} 
		else input = (SinglePredInput) reasonerInput;
		
		Predicate disjHypPred = input.getPredicate();
		Hypothesis disjHyp = new Hypothesis(disjHypPred);
		
		
		if (! seq.hypotheses().contains(disjHyp))
			return new ReasonerOutputFail(this,input,
					"Nonexistent hypothesis:"+disjHyp);
		if (! Lib.isDisj(disjHypPred))
			return new ReasonerOutputFail(this,input,
					"Hypothesis is not a disjunction:"+disjHyp);
		
		// Generate the successful reasoner output
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.display = "∨ hyp ("+disjHyp+")";
		reasonerOutput.neededHypotheses.add(disjHyp);
		reasonerOutput.goal = seq.goal();

		// Generate the anticidents
		Predicate[] disjuncts = Lib.disjuncts(disjHypPred);
		reasonerOutput.anticidents = new Anticident[disjuncts.length];
		
		for (int i = 0; i < disjuncts.length; i++) {
			reasonerOutput.anticidents[i] = new Anticident();
			reasonerOutput.anticidents[i].addConjunctsToAddedHyps(disjuncts[i]);
			reasonerOutput.anticidents[i].hypAction.add(Lib.deselect(disjHyp));
			reasonerOutput.anticidents[i].subGoal = seq.goal();
		}
		
		return reasonerOutput;
	}
	
	
//	public static class Input implements ReasonerInput{
//		
//		Hypothesis disjHyp;
//		
//		public Input(Hypothesis disjHyp){
//			this.disjHyp = disjHyp;
//		}
//		
//		public Input(SerializableReasonerInput serializableReasonerInput) {
//			this.disjHyp = new Hypothesis(serializableReasonerInput.getPredicate("disjHyp"));
//		}
//		
//		public SerializableReasonerInput genSerializable(){
//			SerializableReasonerInput serializableReasonerInput 
//			= new SerializableReasonerInput();
//			serializableReasonerInput.putPredicate("disjHyp",disjHyp.getPredicate());
//			return serializableReasonerInput;
//		}
//		
//	}

}
