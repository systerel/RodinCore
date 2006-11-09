package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Collections;

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

public class Cut extends SinglePredInputReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".cut";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProofMonitor pm){
		
		// Organize Input
		SinglePredInput input = (SinglePredInput) reasonerInput;
		
		if (input.hasError())
			return ProverFactory.reasonerFailure(this,reasonerInput,input.getError());

		Predicate lemma = input.getPredicate();
		
		// This check may be redone for replay since the type environment
		// may have shrunk, making the previous predicate with dangling free vars.

		// This check now done when constructing the sequent.. 
		// so the reasoner is successful, but the rule fails.
		
		//		if (! Lib.typeCheckClosed(lemma,seq.typeEnvironment()))
		//			return new ReasonerOutputFail(this,input,
		//					"Type check failed for predicate: "+lemma);
		
		// We can now assume that lemma has been properly parsed and typed.
		
		// Generate the well definedness condition for the lemma
		Predicate lemmaWD = Lib.WD(lemma);

		// Generate the anticidents
		IAntecedent[] anticidents = new IAntecedent[3];
		
		// Well definedness condition
		anticidents[0] = ProverFactory.makeAntecedent(lemmaWD);
		
		// The lemma to be proven
		anticidents[1] = ProverFactory.makeAntecedent(lemma);
		
		// Proving the original goal with the help of the lemma
		anticidents[2] = ProverFactory.makeAntecedent(
				seq.goal(),
				Collections.singleton(lemma),
				null);
		
		// Generate the proof rule
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				"ah ("+lemma.toString()+")",
				anticidents);
		
//		
//		// Generate the successful reasoner output
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		reasonerOutput.display = "ah ("+lemma.toString()+")";
//		reasonerOutput.goal = seq.goal();
//
//		// Generate the anticidents
//		reasonerOutput.anticidents = new Antecedent[3];
//		
//		// Well definedness condition
//		reasonerOutput.anticidents[0] = new ProofRule.Antecedent();
//		reasonerOutput.anticidents[0].goal = lemmaWD;
//		
//		// The lemma to be proven
//		reasonerOutput.anticidents[1] = new ProofRule.Antecedent();
//		reasonerOutput.anticidents[1].goal = lemma;
//		
//		// Proving the original goal with the help of the lemma
//		reasonerOutput.anticidents[2] = new ProofRule.Antecedent();
//		reasonerOutput.anticidents[2].addConjunctsToAddedHyps(lemma);
//		reasonerOutput.anticidents[2].goal = seq.goal();
				
		return reasonerOutput;
	}

}
