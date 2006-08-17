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

public class ImpI extends EmptyInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".impI";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public ReasonerOutput apply(IProverSequent seq,IReasonerInput input, IProgressMonitor progressMonitor){
		
		if (! Lib.isImp(seq.goal()))
		{
			ReasonerOutputFail reasonerOutput = new ReasonerOutputFail(this,input);
			reasonerOutput.error = "Goal is not an implication";
			return reasonerOutput;
		}
		
		ProofRule reasonerOutput = new ProofRule(this,input);
		reasonerOutput.goal = seq.goal();
		reasonerOutput.display = "⇒ goal";
		reasonerOutput.anticidents = new Anticident[1];
		
		reasonerOutput.anticidents[0] = new ProofRule.Anticident();
		reasonerOutput.anticidents[0].addConjunctsToAddedHyps(Lib.impLeft(seq.goal()));
		reasonerOutput.anticidents[0].subGoal = Lib.impRight(seq.goal());
				
		return reasonerOutput;
	}

}
