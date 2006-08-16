package org.eventb.core.seqprover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.ReasonerOutput;
import org.eventb.core.seqprover.ReasonerOutputFail;
import org.eventb.core.seqprover.ReasonerOutputSucc;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.ReasonerOutputSucc.Anticident;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.core.seqprover.sequent.IProverSequent;

public class FalseHyp extends EmptyInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".falseHyp";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public ReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProgressMonitor progressMonitor){
	
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
