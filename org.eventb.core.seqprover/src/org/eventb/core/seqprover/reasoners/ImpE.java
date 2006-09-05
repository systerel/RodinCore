package org.eventb.core.seqprover.reasoners;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.RuleFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAnticident;
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
	
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProgressMonitor progressMonitor){
		
		SinglePredInput input = (SinglePredInput) reasonerInput;
		
		Predicate impHypPred = input.getPredicate();
		Hypothesis impHyp = new Hypothesis(impHypPred);
		
		
		if (! seq.hypotheses().contains(impHyp))
			return RuleFactory.reasonerFailure(this,input,
					"Nonexistent hypothesis:"+impHyp);
		if (! Lib.isImp(impHypPred))
			return RuleFactory.reasonerFailure(this,input,
					"Hypothesis is not an implication:"+impHyp);
		
		// Generate the anticident
		Predicate toAssume = Lib.impRight(impHypPred);
		Predicate toShow = Lib.impLeft(impHypPred);
		IAnticident[] anticidents = new Anticident[2];
		
		anticidents[0] = RuleFactory.makeAnticident(toShow);
		
		Set<Predicate> addedHyps = Lib.breakPossibleConjunct(toAssume);
		addedHyps.addAll(Lib.breakPossibleConjunct(toShow));
		anticidents[1] = RuleFactory.makeAnticident(
				seq.goal(),
				addedHyps,
				Lib.deselect(impHyp));
		
		// Generate the successful reasoner output
		IProofRule reasonerOutput = RuleFactory.makeProofRule(
				this,input,
				seq.goal(),
				impHyp,
				"⇒ hyp ("+impHyp+")",
				anticidents);
		
//		// Generate the successful reasoner output
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		reasonerOutput.display = "⇒ hyp ("+impHyp+")";
//		reasonerOutput.neededHypotheses.add(impHyp);
//		reasonerOutput.goal = seq.goal();
//
//		// Generate the anticident
//		Predicate toAssume = Lib.impRight(impHypPred);
//		Predicate toShow = Lib.impLeft(impHypPred);
//		reasonerOutput.anticidents = new Anticident[2];
//		
//		reasonerOutput.anticidents[0] = new Anticident();
//		reasonerOutput.anticidents[0].goal = toShow;
//		
//		reasonerOutput.anticidents[1] = new Anticident();
//		reasonerOutput.anticidents[1].addConjunctsToAddedHyps(toShow);
//		reasonerOutput.anticidents[1].addConjunctsToAddedHyps(toAssume);
//		reasonerOutput.anticidents[1].hypAction.add(Lib.deselect(impHyp));
//		reasonerOutput.anticidents[1].goal = seq.goal();
		
		return reasonerOutput;
	}

}
