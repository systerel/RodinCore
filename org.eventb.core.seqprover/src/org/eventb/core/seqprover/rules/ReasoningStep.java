package org.eventb.core.seqprover.rules;

import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.seqprover.ReasonerOutputSucc;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.core.seqprover.sequent.IProverSequent;

public class ReasoningStep extends ProofRule {

	public final ReasonerOutputSucc reasonerOutput;
	
	public ReasoningStep(ReasonerOutputSucc reasonerOutput){
		super(reasonerOutput.display,"reasoningStep");
		this.reasonerOutput = reasonerOutput;
	}
	
	public boolean isApplicable(IProverSequent seq) {
		// Check if all the needed hyps are there
		if (! seq.hypotheses().containsAll(reasonerOutput.neededHypotheses))
			return false;
		// Check if the goal is compatible
		if (reasonerOutput.goal.equals(seq.goal())) return true;
		
		return false;
	}

	public IProverSequent[] apply(IProverSequent seq) {
		// Check if all the needed hyps are there
		if (! seq.hypotheses().containsAll(reasonerOutput.neededHypotheses))
			return null;
		// Check if the goal is the same
		if (! reasonerOutput.goal.equals(seq.goal())) return null;
		
		// Generate new anticidents
		// Anticident[] anticidents = reasonerOutput.anticidents;
		IProverSequent[] anticidents 
			= new IProverSequent[reasonerOutput.anticidents.length];
		for (int i = 0; i < anticidents.length; i++) {
			anticidents[i] = reasonerOutput.anticidents[i].genSequent(seq);
			if (anticidents[i] == null)
				// most probably a name clash occured
				// or an invalid type env.
				// add renaming/refactoring code here
				return null;
		}
		
		return anticidents;
	}
	

	public int getRuleConfidence() {
		return reasonerOutput.reasonerConfidence;
	}
	
	public Set<Hypothesis> getNeededHypotheses(){
		return reasonerOutput.neededHypotheses;
	}

	
	public void addFreeIdents(ITypeEnvironment typeEnv) {
		reasonerOutput.addFreeIdents(typeEnv);
	}
	
	
		
}
