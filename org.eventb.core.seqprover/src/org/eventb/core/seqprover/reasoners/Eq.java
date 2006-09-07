package org.eventb.core.seqprover.reasoners;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.RuleFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAnticident;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInputReasoner;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.core.seqprover.sequent.IProverSequent;

public class Eq extends SinglePredInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".eq";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProgressMonitor progressMonitor){
		
		SinglePredInput input = (SinglePredInput) reasonerInput;

		Predicate eqHypPred = input.getPredicate();
		Hypothesis eqHyp = new Hypothesis(eqHypPred);
		
		if (! seq.hypotheses().contains(eqHyp))
		return RuleFactory.reasonerFailure(this,input,
					"Nonexistent hypothesis:"+eqHyp);
		if (! Lib.isEq(eqHypPred))
			return RuleFactory.reasonerFailure(this,input,
					"Hypothesis is not an implication:"+eqHyp);
		
		Expression from;
		Expression to;
		 
		from = Lib.eqLeft(eqHypPred);
		to = Lib.eqRight(eqHypPred);
		
		// TODO remove when full equality possible.
		if (!(from instanceof FreeIdentifier)) 
			return RuleFactory.reasonerFailure(this,input,"Identifier expected:"+from);
		
		Set<Hypothesis> toDeselect = Hypothesis.Hypotheses();
		toDeselect.add(eqHyp);
		Set<Predicate> rewrittenHyps = new HashSet<Predicate>();
		for (Hypothesis shyp : seq.selectedHypotheses()){
			if (! shyp.equals(eqHyp)){
				Predicate rewritten = (Lib.rewrite(shyp.getPredicate(),(FreeIdentifier)from,to));
				if (! rewritten.equals(shyp.getPredicate())){
					toDeselect.add(shyp);
					rewrittenHyps.add(rewritten);
				}
			}
		}
		
		Predicate rewrittenGoal = Lib.rewrite(seq.goal(),(FreeIdentifier)from,to);
		
		//	Generate the anticident
		IAnticident[] anticidents = new IAnticident[1];
		anticidents[0] = RuleFactory.makeAnticident(
				rewrittenGoal,
				rewrittenHyps,
				Lib.deselect(toDeselect));
		
		//	 Generate the successful reasoner output
		// no need to clone since toDeselect is not used later
		Set<Hypothesis> neededHyps = toDeselect;
		neededHyps.add(eqHyp);
		IProofRule reasonerOutput = RuleFactory.makeProofRule(
				this,input,
				seq.goal(),
				neededHyps,
				null,
				"eh ("+eqHyp+")",
				anticidents);
		
//		// Generate the successful reasoner output
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		
//		reasonerOutput.display = "eh ("+eqHyp+")";
//		reasonerOutput.neededHypotheses.add(eqHyp);
//		reasonerOutput.neededHypotheses.addAll(toDeselect);
//		reasonerOutput.goal = seq.goal();
//
//		// Generate the anticident
//		reasonerOutput.anticidents = new Anticident[1];
//		reasonerOutput.anticidents[0] = new Anticident(rewrittenGoal);
//		reasonerOutput.anticidents[0].addToAddedHyps(rewrittenHyps);
//		reasonerOutput.anticidents[0].hypAction.add(Lib.deselect(toDeselect));
		return reasonerOutput;
	}

}
