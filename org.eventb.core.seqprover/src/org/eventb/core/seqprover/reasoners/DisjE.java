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

public class DisjE extends SinglePredInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".disjE";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public ReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProgressMonitor progressMonitor){
		
		SinglePredInput input = (SinglePredInput) reasonerInput;
		
		Predicate disjHypPred = input.getPredicate();
		Hypothesis disjHyp = new Hypothesis(disjHypPred);
		
		
		if (! seq.hypotheses().contains(disjHyp))
			return new ReasonerOutputFail(this,input,
					"Nonexistent hypothesis:"+disjHyp);
		if (! Lib.isDisj(disjHypPred))
			return new ReasonerOutputFail(this,input,
					"Hypothesis is not a disjunction:"+disjHyp);
		
		// Generate the successful reasoner output
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.display = "∨ hyp ("+disjHyp+")";
		reasonerOutput.neededHypotheses.add(disjHyp);
		reasonerOutput.goal = seq.goal();

		// Generate the anticidents
		Predicate[] disjuncts = Lib.disjuncts(disjHypPred);
		reasonerOutput.anticidents = new Anticident[disjuncts.length];
		
		for (int i = 0; i < disjuncts.length; i++) {
			reasonerOutput.anticidents[i] = new Anticident();
			reasonerOutput.anticidents[i].addConjunctsToAddedHyps(disjuncts[i]);
			reasonerOutput.anticidents[i].hypAction.add(Lib.deselect(disjHyp));
			reasonerOutput.anticidents[i].subGoal = seq.goal();
		}
		
		return reasonerOutput;
	}

}
