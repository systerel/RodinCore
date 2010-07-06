package org.eventb.core.seqprover.reasoners;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public class Hyp extends EmptyInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".hyp";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@ProverRule("HYP")
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm){
	
		if (! seq.containsHypothesis(seq.goal()))
			return ProverFactory.reasonerFailure(
					this,input,
					"Goal not in hypotheses");
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				seq.goal(),
				"hyp",
				new IAntecedent[0]);
		
		return reasonerOutput;
	}

}
