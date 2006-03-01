package org.eventb.core.prover.externalReasoners;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExtReasonerInput;
import org.eventb.core.prover.IExtReasonerOutput;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.SuccessfullExtReasonerOutput;
import org.eventb.core.prover.UnSuccessfulExtReasonerOutput;
import org.eventb.core.prover.externalReasoners.rewriter.Rewriter;
import org.eventb.core.prover.proofs.Proof;
import org.eventb.core.prover.proofs.TrustedProof;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.ISequent;
import org.eventb.core.prover.sequent.SimpleSequent;


public class RewriteGoal implements IExternalReasoner{
	
	public String name(){
		return "rewrite goal";
	}
	

	public IExtReasonerOutput apply(IProverSequent S,IExtReasonerInput I) {
		if (! (I instanceof Input)) throw (new AssertionError(this));
		Input cI = (Input) I;
		
		Rewriter rewriter = cI.rewriter;
		if (rewriter == null) 
			return new UnSuccessfulExtReasonerOutput(this,I,
					"Illegal rewriter: " + rewriter);
		
		ITypeEnvironment te = S.typeEnvironment();
		
		Predicate newGoal = rewriter.apply(te,S.goal());
		if (newGoal == null)
			return new UnSuccessfulExtReasonerOutput(this,I,
					"Rewriter " + rewriter +" inapplicable for goal "+ S.goal());

		Predicate seqGoal = Lib.makeImp(te,newGoal,S.goal());
		ISequent outputSequent = new SimpleSequent(S.typeEnvironment(),Hypothesis.Hypotheses(),seqGoal);
		Proof outputProof = new TrustedProof(outputSequent);
		return new SuccessfullExtReasonerOutput(this,I,outputProof);
	}	
	
	public static class Input implements IExtReasonerInput{

		public final Rewriter rewriter;
		
		public Input(Rewriter rewriter){
			this.rewriter = rewriter;
		}
	}

}
