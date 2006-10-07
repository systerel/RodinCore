package org.eventb.core.seqprover.reasoners;

import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAnticident;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public class Hyp extends EmptyInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".hyp";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm){
	
		if (! (Hypothesis.containsPredicate(seq.hypotheses(),seq.goal())))
			return ProverFactory.reasonerFailure(
					this,input,
					"Goal not in hypotheses");
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				new Hypothesis(seq.goal()),
				"hyp",
				new IAnticident[0]);
		
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		reasonerOutput.neededHypotheses.add(new Hypothesis(seq.goal()));
//		reasonerOutput.goal = seq.goal();
//		reasonerOutput.display = "hyp";
//		
//		reasonerOutput.anticidents = new Anticident[0];
		
		return reasonerOutput;
	}

}
