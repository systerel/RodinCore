package org.eventb.core.prover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputFail;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

public class ConjE extends SinglePredInputReasoner{
	
	public String getReasonerID() {
		return "conjE";
	}
	
	public ReasonerOutput apply(IProverSequent seq,ReasonerInput reasonerInput, IProgressMonitor progressMonitor){
		
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
