package org.eventb.core.prover.reasoners;

import org.eventb.core.prover.Lib;
import org.eventb.core.prover.Reasoner;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputFail;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

public class Contradiction implements Reasoner{
	
	public String getReasonerID() {
		return "contradiction";
	}
	
	public ReasonerOutput apply(IProverSequent seq, ReasonerInput input){
	
		if (! (Hypothesis.containsPredicate(seq.hypotheses(),Lib.False)))
		{
			ReasonerOutputFail reasonerOutput = new ReasonerOutputFail(this,input);
			reasonerOutput.error = "no false hypothesis";
			return reasonerOutput;
		}
		
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.goal = seq.goal();
		reasonerOutput.neededHypotheses.add(new Hypothesis(Lib.False));
		reasonerOutput.display = "⊥ hyp";
		
		reasonerOutput.anticidents = new Anticident[0];
		
		return reasonerOutput;
	}

}
