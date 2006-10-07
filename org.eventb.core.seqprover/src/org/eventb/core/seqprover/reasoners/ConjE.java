package org.eventb.core.seqprover.reasoners;

import org.eventb.core.ast.Predicate;
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
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInputReasoner;

public class ConjE extends SinglePredInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".conjE";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProofMonitor pm){
		
		SinglePredInput input = (SinglePredInput) reasonerInput;
		
		Predicate conjHypPred = input.getPredicate();
		Hypothesis conjHyp = new Hypothesis(conjHypPred);
		
		
		if (! seq.hypotheses().contains(conjHyp))
			return ProverFactory.reasonerFailure(this,input,
					"Nonexistent hypothesis:"+conjHyp);
		if (! Lib.isConj(conjHypPred))
			return ProverFactory.reasonerFailure(this,input,
					"Hypothesis is not a conjunction:"+conjHyp);
		
		// Generate the successful reasoner output
		
		IAnticident[] anticidents = new IAnticident[1];
		anticidents[0] = ProverFactory.makeAnticident(
				seq.goal(),
				Lib.breakPossibleConjunct(conjHypPred),
				Lib.hide(conjHyp));
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				"∧ hyp ("+conjHyp+")",
				anticidents
				);
		
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		reasonerOutput.display = "∧ hyp ("+conjHyp+")";
//		reasonerOutput.neededHypotheses.add(conjHyp);
//		reasonerOutput.goal = seq.goal();
//
//		// Generate the anticident
//		// Predicate[] conjuncts = Lib.conjuncts(conjHypPred);
//		reasonerOutput.anticidents = new Anticident[1];
//		reasonerOutput.anticidents[0] = new Anticident();
//		reasonerOutput.anticidents[0].addConjunctsToAddedHyps(conjHypPred);
//		reasonerOutput.anticidents[0].hypAction.add(Lib.hide(conjHyp));
//		reasonerOutput.anticidents[0].goal = seq.goal();
		
		return reasonerOutput;
	}

}
