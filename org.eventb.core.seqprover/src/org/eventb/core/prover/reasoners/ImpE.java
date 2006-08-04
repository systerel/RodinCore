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

public class ImpE implements Reasoner{
	
	public String getReasonerID() {
		return "impE";
	}
	
	public ReasonerOutput apply(IProverSequent seq,ReasonerInput reasonerInput){
		
		SinglePredInput input;
		if (reasonerInput instanceof SerializableReasonerInput){
			input = new SinglePredInput((SerializableReasonerInput)reasonerInput);
		} 
		else input = (SinglePredInput) reasonerInput;
		
		Predicate impHypPred = input.getPredicate();
		Hypothesis impHyp = new Hypothesis(impHypPred);
		
		
		if (! seq.hypotheses().contains(impHyp))
			return new ReasonerOutputFail(this,input,
					"Nonexistent hypothesis:"+impHyp);
		if (! Lib.isImp(impHypPred))
			return new ReasonerOutputFail(this,input,
					"Hypothesis is not an implication:"+impHyp);
		
		// Generate the successful reasoner output
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.display = "⇒ hyp ("+impHyp+")";
		reasonerOutput.neededHypotheses.add(impHyp);
		reasonerOutput.goal = seq.goal();

		// Generate the anticident
		Predicate toAssume = Lib.impRight(impHypPred);
		Predicate toShow = Lib.impLeft(impHypPred);
		reasonerOutput.anticidents = new Anticident[2];
		
		reasonerOutput.anticidents[0] = new Anticident();
		reasonerOutput.anticidents[0].subGoal = toShow;
		
		reasonerOutput.anticidents[1] = new Anticident();
		reasonerOutput.anticidents[1].addedHypotheses.add(toShow);
		reasonerOutput.anticidents[1].addedHypotheses.add(toAssume);
		reasonerOutput.anticidents[1].hypAction.add(Lib.deselect(impHyp));
		reasonerOutput.anticidents[1].subGoal = seq.goal();
		
		return reasonerOutput;
	}
	
	
//	public static class Input implements ReasonerInput{
//		
//		Hypothesis impHyp;
//		
//		public Input(Hypothesis impHyp){
//			this.impHyp = impHyp;
//		}
//		
//		public Input(SerializableReasonerInput serializableReasonerInput) {
//			this.impHyp = new Hypothesis(serializableReasonerInput.getPredicate("impHyp"));
//		}
//		
//		public SerializableReasonerInput genSerializable(){
//			SerializableReasonerInput serializableReasonerInput 
//			= new SerializableReasonerInput();
//			serializableReasonerInput.putPredicate("impHyp",impHyp.getPredicate());
//			return serializableReasonerInput;
//		}
//		
//	}

}
