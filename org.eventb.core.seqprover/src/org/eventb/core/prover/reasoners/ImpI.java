package org.eventb.core.prover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.IReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputFail;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.sequent.IProverSequent;

public class ImpI extends EmptyInputReasoner{
	
	public String getReasonerID() {
		return "impI";
	}
	
	public ReasonerOutput apply(IProverSequent seq,IReasonerInput input, IProgressMonitor progressMonitor){
		
		if (! Lib.isImp(seq.goal()))
		{
			ReasonerOutputFail reasonerOutput = new ReasonerOutputFail(this,input);
			reasonerOutput.error = "Goal is not an implication";
			return reasonerOutput;
		}
		
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.goal = seq.goal();
		reasonerOutput.display = "⇒ goal";
		reasonerOutput.anticidents = new Anticident[1];
		
		reasonerOutput.anticidents[0] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[0].addConjunctsToAddedHyps(Lib.impLeft(seq.goal()));
		reasonerOutput.anticidents[0].subGoal = Lib.impRight(seq.goal());
				
		return reasonerOutput;
	}

}
