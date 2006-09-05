package org.eventb.core.seqprover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.RuleFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAnticident;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;
import org.eventb.core.seqprover.sequent.IProverSequent;

public class TrueGoal extends EmptyInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".trueGoal";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProgressMonitor progressMonitor){
	
		if (! (seq.goal().equals(Lib.True)))
			return RuleFactory.reasonerFailure(this,input,"Goal is not a tautology");
		
		IProofRule reasonerOutput = RuleFactory.makeProofRule(
				this,input,
				seq.goal(),"⊤ goal",
				new IAnticident[0]);
		
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		reasonerOutput.goal = seq.goal();
//		reasonerOutput.display = "⊤ goal";
//		
//		reasonerOutput.anticidents = new Anticident[0];
		
		return reasonerOutput;
	}

}
