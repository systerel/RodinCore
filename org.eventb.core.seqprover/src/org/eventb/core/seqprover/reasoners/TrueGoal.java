package org.eventb.core.seqprover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.ReasonerOutput;
import org.eventb.core.seqprover.ReasonerOutputFail;
import org.eventb.core.seqprover.ProofRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.ProofRule.Anticident;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;
import org.eventb.core.seqprover.sequent.IProverSequent;

public class TrueGoal extends EmptyInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".trueGoal";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public ReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProgressMonitor progressMonitor){
	
		if (! (seq.goal().equals(Lib.True)))
		{
			ReasonerOutputFail reasonerOutput = new ReasonerOutputFail(this,input);
			reasonerOutput.error = "Goal is not a tautology";
			return reasonerOutput;
		}
		
		ProofRule reasonerOutput = new ProofRule(this,input);
		reasonerOutput.goal = seq.goal();
		reasonerOutput.display = "⊤ goal";
		
		reasonerOutput.anticidents = new Anticident[0];
		
		return reasonerOutput;
	}

}
