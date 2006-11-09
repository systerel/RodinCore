package org.eventb.internal.core.seqprover.eventbExtensions;

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
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public class ConjI extends EmptyInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".conjI";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm){
	
		if (! Lib.isConj(seq.goal()))
			return ProverFactory.reasonerFailure(
					this,input,
					"Goal is not a conjunction");
		
		Predicate[] conjuncts = Lib.conjuncts(seq.goal());
		
		IAntecedent[] anticidents = new IAntecedent[conjuncts.length];
		for (int i = 0; i < anticidents.length; i++) {
			// Generate one anticident per conjunct
			anticidents[i] = ProverFactory.makeAntecedent(conjuncts[i]);
		}
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				"∧ goal",
				anticidents
				);
		
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		reasonerOutput.goal = seq.goal();
//		reasonerOutput.display = "∧ goal";
//		reasonerOutput.anticidents = new Antecedent[conjuncts.length];
//		for (int i = 0; i < reasonerOutput.anticidents.length; i++) {
//			// Generate one anticident per conjunct
//			reasonerOutput.anticidents[i] = new ProofRule.Antecedent();
//			reasonerOutput.anticidents[i].goal = conjuncts[i];
//		}
		
		return reasonerOutput;
	}

}
