package org.eventb.core.seqprover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.ReasonerOutput;
import org.eventb.core.seqprover.ReasonerOutputFail;
import org.eventb.core.seqprover.ProofRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.ProofRule.Anticident;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInputReasoner;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.core.seqprover.sequent.IProverSequent;

public class Contr extends SinglePredInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".contr";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public ReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProgressMonitor progressMonitor){
		
		SinglePredInput input = (SinglePredInput) reasonerInput;
		
		if (input.hasError())
		{
			ReasonerOutputFail reasonerOutput = new ReasonerOutputFail(this,reasonerInput);
			reasonerOutput.error = input.getError();
			return reasonerOutput;
		}
		
		Predicate falseHypPred = input.getPredicate();
		Hypothesis falseHyp = new Hypothesis(falseHypPred);
		
		if ((!falseHypPred.equals(Lib.True)) && (! seq.hypotheses().contains(falseHyp)))
		return new ReasonerOutputFail(this,input,
					"Nonexistent hypothesis:"+falseHyp);

		// Generate the successful reasoner output
		ProofRule reasonerOutput = new ProofRule(this,input);
		if (falseHypPred.equals(Lib.True))
		{
			reasonerOutput.display = "ct goal";
		}
		else 
		{
			reasonerOutput.display = "ct hyp ("+falseHyp+")";
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
	
	
//	public static class Input implements ReasonerInput{
//		
//		Hypothesis falseHyp;
//		
//		public Input(Hypothesis falseHyp){
//			this.falseHyp = falseHyp;
//		}
//		
//		public Input(){
//			this.falseHyp = new Hypothesis(Lib.True);
//		}
//		
//		public Input(SerializableReasonerInput serializableReasonerInput) {
//			this.falseHyp = new Hypothesis(serializableReasonerInput.getPredicate("falseHyp"));
//		}
//		
//		public SerializableReasonerInput genSerializable(){
//			SerializableReasonerInput serializableReasonerInput 
//			= new SerializableReasonerInput();
//			serializableReasonerInput.putPredicate("falseHyp",falseHyp.getPredicate());
//			return serializableReasonerInput;
//		}
//		
//	}

}
