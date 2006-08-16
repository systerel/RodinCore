package org.eventb.core.seqprover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.ReasonerOutput;
import org.eventb.core.seqprover.ReasonerOutputFail;
import org.eventb.core.seqprover.ReasonerOutputSucc;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.ReasonerOutputSucc.Anticident;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInputReasoner;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.core.seqprover.sequent.IProverSequent;

public class ConjE extends SinglePredInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".conjE";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public ReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProgressMonitor progressMonitor){
		
		SinglePredInput input = (SinglePredInput) reasonerInput;
		
		Predicate conjHypPred = input.getPredicate();
		Hypothesis conjHyp = new Hypothesis(conjHypPred);
		
		
		if (! seq.hypotheses().contains(conjHyp))
			return new ReasonerOutputFail(this,input,
					"Nonexistent hypothesis:"+conjHyp);
		if (! Lib.isConj(conjHypPred))
			return new ReasonerOutputFail(this,input,
					"Hypothesis is not a conjunction:"+conjHyp);
		
		// Generate the successful reasoner output
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.display = "∧ hyp ("+conjHyp+")";
		reasonerOutput.neededHypotheses.add(conjHyp);
		reasonerOutput.goal = seq.goal();

		// Generate the anticident
		// Predicate[] conjuncts = Lib.conjuncts(conjHypPred);
		reasonerOutput.anticidents = new Anticident[1];
		reasonerOutput.anticidents[0] = new Anticident();
		reasonerOutput.anticidents[0].addConjunctsToAddedHyps(conjHypPred);
		reasonerOutput.anticidents[0].hypAction.add(Lib.hide(conjHyp));
		reasonerOutput.anticidents[0].subGoal = seq.goal();
		
		return reasonerOutput;
	}

}
