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

public class ImpI extends EmptyInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".impI";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput input, IProgressMonitor progressMonitor){
		
		if (! Lib.isImp(seq.goal()))
			return RuleFactory.reasonerFailure(this,input,"Goal is not an implication");
		
		IAnticident[] anticidents = new IAnticident[1];
		
		anticidents[0] = RuleFactory.makeAnticident(
				Lib.impRight(seq.goal()),
				Lib.breakPossibleConjunct(Lib.impLeft(seq.goal())),
				null);
		
		IProofRule reasonerOutput = RuleFactory.makeProofRule(
				this,input,
				seq.goal(),
				"⇒ goal",
				anticidents);
		
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		reasonerOutput.goal = seq.goal();
//		reasonerOutput.display = "⇒ goal";
//		reasonerOutput.anticidents = new Anticident[1];
//		
//		reasonerOutput.anticidents[0] = new ProofRule.Anticident();
//		reasonerOutput.anticidents[0].addConjunctsToAddedHyps(Lib.impLeft(seq.goal()));
//		reasonerOutput.anticidents[0].goal = Lib.impRight(seq.goal());
				
		return reasonerOutput;
	}

}
