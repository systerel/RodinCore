package org.eventb.core.prover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.IReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputFail;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.reasonerInputs.EmptyInputReasoner;
import org.eventb.core.prover.sequent.IProverSequent;

public class ConjI extends EmptyInputReasoner{
	
	public String getReasonerID() {
		return "conjI";
	}
	
	
	public ReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProgressMonitor progressMonitor){
	
		if (! Lib.isConj(seq.goal()))
		{
			ReasonerOutputFail reasonerOutput = new ReasonerOutputFail(this,input);
			reasonerOutput.error = "Goal is not a conjunction";
			return reasonerOutput;
		}
		
		
		Predicate[] conjuncts = Lib.conjuncts(seq.goal());
		
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.goal = seq.goal();
		reasonerOutput.display = "∧ goal";
		reasonerOutput.anticidents = new Anticident[conjuncts.length];
		for (int i = 0; i < reasonerOutput.anticidents.length; i++) {
			// Generate one anticident per conjunct
			reasonerOutput.anticidents[i] = new ReasonerOutputSucc.Anticident();
			reasonerOutput.anticidents[i].subGoal = conjuncts[i];
		}
		
		return reasonerOutput;
	}

}
