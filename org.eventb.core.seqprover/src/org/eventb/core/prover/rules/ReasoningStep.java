package org.eventb.core.prover.rules;

import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

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

	@Override
	public Set<FreeIdentifier> getNeededFreeIdents() {
		return reasonerOutput.getNeededFreeIdents();
	}
	
	
		
}
