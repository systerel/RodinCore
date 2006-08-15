package org.eventb.core.prover.reasoners;

import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputFail;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

public class Hyp extends EmptyInputReasoner{
	
	public String getReasonerID() {
		return "hyp";
	}
	
	public ReasonerOutput apply(IProverSequent seq, ReasonerInput input){
	
		if (! (Hypothesis.containsPredicate(seq.hypotheses(),seq.goal())))
		{
			ReasonerOutputFail reasonerOutput = new ReasonerOutputFail(this,input);
			reasonerOutput.error = "Goal not in hypotheses";
			return reasonerOutput;
		}
		
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.neededHypotheses.add(new Hypothesis(seq.goal()));
		reasonerOutput.goal = seq.goal();
		reasonerOutput.display = "hyp";
		
		reasonerOutput.anticidents = new Anticident[0];
		
		return reasonerOutput;
	}

}
