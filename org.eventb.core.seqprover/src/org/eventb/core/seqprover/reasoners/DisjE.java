package org.eventb.core.seqprover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.RuleFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAnticident;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInputReasoner;
import org.eventb.core.seqprover.sequent.Hypothesis;

public class DisjE extends SinglePredInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".disjE";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProgressMonitor progressMonitor){
		
		SinglePredInput input = (SinglePredInput) reasonerInput;
		
		Predicate disjHypPred = input.getPredicate();
		Hypothesis disjHyp = new Hypothesis(disjHypPred);
		
		
		if (! seq.hypotheses().contains(disjHyp))
			return RuleFactory.reasonerFailure(this,input,
					"Nonexistent hypothesis:"+disjHyp);
		if (! Lib.isDisj(disjHypPred))
			return RuleFactory.reasonerFailure(this,input,
					"Hypothesis is not a disjunction:"+disjHyp);
		
		//	 Generate the anticidents
		Predicate[] disjuncts = Lib.disjuncts(disjHypPred);
		IAnticident[] anticidents = new IAnticident[disjuncts.length];
		
		for (int i = 0; i < disjuncts.length; i++) {
			anticidents[i] = RuleFactory.makeAnticident(
					seq.goal(),
					Lib.breakPossibleConjunct(disjuncts[i]),
					Lib.deselect(disjHyp));
		}
		
		//	Generate the successful reasoner output
		IProofRule reasonerOutput = RuleFactory.makeProofRule(
				this,input,
				seq.goal(),
				disjHyp,
				"∨ hyp ("+disjHyp+")",
				anticidents);		
		
//		// Generate the successful reasoner output
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		reasonerOutput.display = "∨ hyp ("+disjHyp+")";
//		reasonerOutput.neededHypotheses.add(disjHyp);
//		reasonerOutput.goal = seq.goal();
//
//		// Generate the anticidents
//		Predicate[] disjuncts = Lib.disjuncts(disjHypPred);
//		reasonerOutput.anticidents = new Anticident[disjuncts.length];
//		
//		for (int i = 0; i < disjuncts.length; i++) {
//			reasonerOutput.anticidents[i] = new Anticident(seq.goal());
//			reasonerOutput.anticidents[i].addConjunctsToAddedHyps(disjuncts[i]);
//			reasonerOutput.anticidents[i].hypAction.add(Lib.deselect(disjHyp));
//		}
		
		return reasonerOutput;
	}

}
