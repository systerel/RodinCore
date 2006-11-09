package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInputReasoner;

public class Eq extends SinglePredInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".eq";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProofMonitor pm){
		
		SinglePredInput input = (SinglePredInput) reasonerInput;

		Predicate eqHypPred = input.getPredicate();
		Hypothesis eqHyp = new Hypothesis(eqHypPred);
		
		if (! seq.hypotheses().contains(eqHyp))
		return ProverFactory.reasonerFailure(this,input,
					"Nonexistent hypothesis:"+eqHyp);
		if (! Lib.isEq(eqHypPred))
			return ProverFactory.reasonerFailure(this,input,
					"Hypothesis is not an implication:"+eqHyp);
		
		Expression from;
		Expression to;
		 
		from = Lib.eqLeft(eqHypPred);
		to = Lib.eqRight(eqHypPred);
		
		// TODO remove when full equality possible.
		if (!(from instanceof FreeIdentifier)) 
			return ProverFactory.reasonerFailure(this,input,"Identifier expected:"+from);
		
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
		IAntecedent[] anticidents = new IAntecedent[1];
		anticidents[0] = ProverFactory.makeAntecedent(
				rewrittenGoal,
				rewrittenHyps,
				ProverLib.deselect(toDeselect));
		
		//	 Generate the successful reasoner output
		// no need to clone since toDeselect is not used later
		Set<Hypothesis> neededHyps = toDeselect;
		neededHyps.add(eqHyp);
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
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
//		reasonerOutput.anticidents = new Antecedent[1];
//		reasonerOutput.anticidents[0] = new Antecedent(rewrittenGoal);
//		reasonerOutput.anticidents[0].addToAddedHyps(rewrittenHyps);
//		reasonerOutput.anticidents[0].hypAction.add(Lib.deselect(toDeselect));
		return reasonerOutput;
	}

}
