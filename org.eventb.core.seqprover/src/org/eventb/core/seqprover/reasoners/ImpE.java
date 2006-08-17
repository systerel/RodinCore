package org.eventb.core.seqprover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.ReasonerOutput;
import org.eventb.core.seqprover.ReasonerOutputFail;
import org.eventb.core.seqprover.ProofRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.ProofRule.Anticident;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInputReasoner;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.core.seqprover.sequent.IProverSequent;

public class ImpE extends SinglePredInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".impE";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public ReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProgressMonitor progressMonitor){
		
		SinglePredInput input = (SinglePredInput) reasonerInput;
		
		Predicate impHypPred = input.getPredicate();
		Hypothesis impHyp = new Hypothesis(impHypPred);
		
		
		if (! seq.hypotheses().contains(impHyp))
			return new ReasonerOutputFail(this,input,
					"Nonexistent hypothesis:"+impHyp);
		if (! Lib.isImp(impHypPred))
			return new ReasonerOutputFail(this,input,
					"Hypothesis is not an implication:"+impHyp);
		
		// Generate the successful reasoner output
		ProofRule reasonerOutput = new ProofRule(this,input);
		reasonerOutput.display = "⇒ hyp ("+impHyp+")";
		reasonerOutput.neededHypotheses.add(impHyp);
		reasonerOutput.goal = seq.goal();

		// Generate the anticident
		Predicate toAssume = Lib.impRight(impHypPred);
		Predicate toShow = Lib.impLeft(impHypPred);
		reasonerOutput.anticidents = new Anticident[2];
		
		reasonerOutput.anticidents[0] = new Anticident();
		reasonerOutput.anticidents[0].subGoal = toShow;
		
		reasonerOutput.anticidents[1] = new Anticident();
		reasonerOutput.anticidents[1].addConjunctsToAddedHyps(toShow);
		reasonerOutput.anticidents[1].addConjunctsToAddedHyps(toAssume);
		reasonerOutput.anticidents[1].hypAction.add(Lib.deselect(impHyp));
		reasonerOutput.anticidents[1].subGoal = seq.goal();
		
		return reasonerOutput;
	}

}
