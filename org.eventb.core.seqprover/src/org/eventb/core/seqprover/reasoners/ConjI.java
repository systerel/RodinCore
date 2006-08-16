package org.eventb.core.seqprover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.ReasonerOutput;
import org.eventb.core.seqprover.ReasonerOutputFail;
import org.eventb.core.seqprover.ReasonerOutputSucc;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.ReasonerOutputSucc.Anticident;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;
import org.eventb.core.seqprover.sequent.IProverSequent;

public class ConjI extends EmptyInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".conjI";
	
	public String getReasonerID() {
		return REASONER_ID;
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
