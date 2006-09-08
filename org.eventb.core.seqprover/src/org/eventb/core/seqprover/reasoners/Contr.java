package org.eventb.core.seqprover.reasoners;

import java.util.HashSet;
import java.util.Set;

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

public class Contr extends SinglePredInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".contr";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProgressMonitor progressMonitor){
		
		SinglePredInput input = (SinglePredInput) reasonerInput;
		
		if (input.hasError())
			return RuleFactory.reasonerFailure(this,reasonerInput,input.getError());
		
		Predicate falseHypPred = input.getPredicate();
		Hypothesis falseHyp = new Hypothesis(falseHypPred);
		
		if ((!falseHypPred.equals(Lib.True)) && (! seq.hypotheses().contains(falseHyp)))
		return RuleFactory.reasonerFailure(this,input,
					"Nonexistent hypothesis:"+falseHyp);

		// Generate the successful reasoner output
		
		Predicate goal;
		String display;
		Set<Hypothesis> neededHypotheses = new HashSet<Hypothesis>();
		
		if (falseHypPred.equals(Lib.True))
		{
			goal = Lib.False;
			display = "ct goal";
		}
		else
		{
			goal = Lib.makeNeg(falseHypPred);
			display = "ct hyp ("+falseHyp+")";
			neededHypotheses.add(falseHyp);
		}
			
		IAnticident[] anticidents = new IAnticident[1];
		anticidents[0] = RuleFactory.makeAnticident(
				goal,
				Lib.breakPossibleConjunct(Lib.makeNeg(seq.goal())),
				null);
		
		IProofRule reasonerOutput = RuleFactory.makeProofRule(
				this,input,
				seq.goal(),
				neededHypotheses,
				null,
				display,
				anticidents);
		
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		String display;
//		
//		if (falseHypPred.equals(Lib.True))
//		{
//			display = "ct goal";
//		}
//		else 
//		{
//			display = "ct hyp ("+falseHyp+")";
//			reasonerOutput.neededHypotheses.add(falseHyp);
//		}
//		reasonerOutput.goal = seq.goal();
//
//		// Generate the anticident
//		reasonerOutput.anticidents = new Anticident[1];
//		
//		Predicate goal;
//		if (falseHypPred.equals(Lib.True))
//			goal = Lib.False;
//		else
//			goal = Lib.makeNeg(falseHypPred);
//		
//		reasonerOutput.anticidents[0] = new Anticident(goal);		
//		reasonerOutput.anticidents[0].addToAddedHyps(Lib.makeNeg(seq.goal()));
		return reasonerOutput;
	}

}
