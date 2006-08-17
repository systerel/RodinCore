package org.eventb.core.seqprover.reasoners;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
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

public class Eq extends SinglePredInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".eq";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public ReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProgressMonitor progressMonitor){
		
		SinglePredInput input = (SinglePredInput) reasonerInput;

		Predicate eqHypPred = input.getPredicate();
		Hypothesis eqHyp = new Hypothesis(eqHypPred);
		
		if (! seq.hypotheses().contains(eqHyp))
		return new ReasonerOutputFail(this,input,
					"Nonexistent hypothesis:"+eqHyp);
		if (! Lib.isEq(eqHypPred))
			return new ReasonerOutputFail(this,input,
					"Hypothesis is not an implication:"+eqHyp);
		
		Expression from;
		Expression to;
		 
		from = Lib.eqLeft(eqHypPred);
		to = Lib.eqRight(eqHypPred);
		
		// TODO remove when full equality possible.
		if (!(from instanceof FreeIdentifier)) 
			return new ReasonerOutputFail(this,input,"Identifier expected:"+from);
		
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
		
		// Generate the successful reasoner output
		ProofRule reasonerOutput = new ProofRule(this,input);
		
		reasonerOutput.display = "eh ("+eqHyp+")";
		reasonerOutput.neededHypotheses.add(eqHyp);
		reasonerOutput.neededHypotheses.addAll(toDeselect);
		reasonerOutput.goal = seq.goal();

		// Generate the anticident
		reasonerOutput.anticidents = new Anticident[1];
		reasonerOutput.anticidents[0] = new Anticident();
		reasonerOutput.anticidents[0].addedHypotheses.addAll(rewrittenHyps);
		reasonerOutput.anticidents[0].hypAction.add(Lib.deselect(toDeselect));
		reasonerOutput.anticidents[0].subGoal = rewrittenGoal;
		return reasonerOutput;
	}

}
