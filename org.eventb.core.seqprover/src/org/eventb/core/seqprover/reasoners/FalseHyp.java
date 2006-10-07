package org.eventb.core.seqprover.reasoners;

import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAnticident;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public class FalseHyp extends EmptyInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".falseHyp";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm){
	
		if (! (Hypothesis.containsPredicate(seq.hypotheses(),Lib.False)))
			return ProverFactory.reasonerFailure(this,input,"no false hypothesis");
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				new Hypothesis(Lib.False),
				"⊥ hyp",
				new IAnticident[0]);
		
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		reasonerOutput.goal = seq.goal();
//		reasonerOutput.neededHypotheses.add(new Hypothesis(Lib.False));
//		reasonerOutput.display = "⊥ hyp";
//		
//		reasonerOutput.anticidents = new Anticident[0];
		
		return reasonerOutput;
	}

}
