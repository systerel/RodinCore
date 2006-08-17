package org.eventb.core.seqprover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.ReasonerOutput;
import org.eventb.core.seqprover.ReasonerOutputFail;
import org.eventb.core.seqprover.ProofRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.ProofRule.Anticident;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInputReasoner;
import org.eventb.core.seqprover.sequent.IProverSequent;

public class Cut extends SinglePredInputReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".cut";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public ReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProgressMonitor progressMonitor){
		
		// Organize Input
		SinglePredInput input = (SinglePredInput) reasonerInput;
		
		if (input.hasError())
		{
			return new ReasonerOutputFail(this,input,input.getError());
		}

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
		
		// Generate the successful reasoner output
		ProofRule reasonerOutput = new ProofRule(this,input);
		reasonerOutput.display = "ah ("+lemma.toString()+")";
		reasonerOutput.goal = seq.goal();

		// Generate the anticidents
		reasonerOutput.anticidents = new Anticident[3];
		
		// Well definedness condition
		reasonerOutput.anticidents[0] = new ProofRule.Anticident();
		reasonerOutput.anticidents[0].subGoal = lemmaWD;
		
		// The lemma to be proven
		reasonerOutput.anticidents[1] = new ProofRule.Anticident();
		reasonerOutput.anticidents[1].subGoal = lemma;
		
		// Proving the original goal with the help of the lemma
		reasonerOutput.anticidents[2] = new ProofRule.Anticident();
		reasonerOutput.anticidents[2].addConjunctsToAddedHyps(lemma);
		reasonerOutput.anticidents[2].subGoal = seq.goal();
				
		return reasonerOutput;
	}

}
