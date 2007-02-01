package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInputReasoner;

// TODO : implement the symetric operation (he)
// TODO : reform rule output to use forward inference
public class Eq extends SinglePredInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".eq";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProofMonitor pm){
		
		SinglePredInput input = (SinglePredInput) reasonerInput;

		Predicate eqHypPred = input.getPredicate();
		Predicate eqHyp = eqHypPred;
		
		if (! seq.containsHypothesis(eqHyp))
		return ProverFactory.reasonerFailure(this,input,
					"Nonexistent hypothesis:"+eqHyp);
		if (! Lib.isEq(eqHypPred))
			return ProverFactory.reasonerFailure(this,input,
					"Hypothesis is not an implication:"+eqHyp);
		
		Expression from;
		Expression to;
		 
		from = Lib.eqLeft(eqHypPred);
		to = Lib.eqRight(eqHypPred);
				
		Set<Predicate> toDeselect = new HashSet<Predicate>();
		toDeselect.add(eqHyp);
		Set<Predicate> rewrittenHyps = new HashSet<Predicate>();
		for (Predicate shyp : seq.selectedHypIterable()){
			if (! shyp.equals(eqHyp)){
				Predicate rewritten = (Lib.rewrite(shyp,from,to));
				if (! rewritten.equals(shyp)){
					toDeselect.add(shyp);
					rewrittenHyps.add(rewritten);
				}
			}
		}
		
		Predicate rewrittenGoal = Lib.rewrite(seq.goal(),from,to);
		
		//	Generate the anticident
		IAntecedent[] anticidents = new IAntecedent[1];
		anticidents[0] = ProverFactory.makeAntecedent(
				rewrittenGoal,
				rewrittenHyps,
				ProverFactory.makeDeselectHypAction(toDeselect));
		
		//	 Generate the successful reasoner output
		// no need to clone since toDeselect is not used later
		Set<Predicate> neededHyps = toDeselect;
		neededHyps.add(eqHyp);
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				neededHyps,
				null,
				"eh ("+eqHyp+")",
				anticidents);
		
		return reasonerOutput;
	}

}
