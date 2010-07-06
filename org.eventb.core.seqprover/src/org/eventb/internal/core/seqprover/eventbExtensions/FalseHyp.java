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

public class FalseHyp extends EmptyInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".falseHyp";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@ProverRule("FALSE_HYP")
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm){
	
		if (! seq.containsHypothesis(Lib.False))
			return ProverFactory.reasonerFailure(this,input,"no false hypothesis");
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				null,
				Lib.False,
				"⊥ hyp",
				new IAntecedent[0]);
		
		return reasonerOutput;
	}

}
