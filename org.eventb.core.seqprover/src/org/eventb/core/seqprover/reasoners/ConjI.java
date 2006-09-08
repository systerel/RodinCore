package org.eventb.core.seqprover.reasoners;

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
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public class ConjI extends EmptyInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".conjI";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProgressMonitor progressMonitor){
	
		if (! Lib.isConj(seq.goal()))
			return RuleFactory.reasonerFailure(
					this,input,
					"Goal is not a conjunction");
		
		Predicate[] conjuncts = Lib.conjuncts(seq.goal());
		
		IAnticident[] anticidents = new IAnticident[conjuncts.length];
		for (int i = 0; i < anticidents.length; i++) {
			// Generate one anticident per conjunct
			anticidents[i] = RuleFactory.makeAnticident(conjuncts[i]);
		}
		
		IProofRule reasonerOutput = RuleFactory.makeProofRule(
				this,input,
				seq.goal(),
				"∧ goal",
				anticidents
				);
		
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		reasonerOutput.goal = seq.goal();
//		reasonerOutput.display = "∧ goal";
//		reasonerOutput.anticidents = new Anticident[conjuncts.length];
//		for (int i = 0; i < reasonerOutput.anticidents.length; i++) {
//			// Generate one anticident per conjunct
//			reasonerOutput.anticidents[i] = new ProofRule.Anticident();
//			reasonerOutput.anticidents[i].goal = conjuncts[i];
//		}
		
		return reasonerOutput;
	}

}
