package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public class ImpI extends EmptyInputReasoner{
	
	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".impI";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@ProverRule("IMP_R")
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput input, IProofMonitor pm){
		
		if (! Lib.isImp(seq.goal()))
			return ProverFactory.reasonerFailure(this,input,"Goal is not an implication");
		
		IAntecedent[] anticidents = new IAntecedent[1];
		
		anticidents[0] = ProverFactory.makeAntecedent(
				Lib.impRight(seq.goal()),
				Lib.breakPossibleConjunct(Lib.impLeft(seq.goal())),
				null);
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				"⇒ goal",
				anticidents);
		
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		reasonerOutput.goal = seq.goal();
//		reasonerOutput.display = "⇒ goal";
//		reasonerOutput.anticidents = new Antecedent[1];
//		
//		reasonerOutput.anticidents[0] = new ProofRule.Antecedent();
//		reasonerOutput.anticidents[0].addConjunctsToAddedHyps(Lib.impLeft(seq.goal()));
//		reasonerOutput.anticidents[0].goal = Lib.impRight(seq.goal());
				
		return reasonerOutput;
	}

}
