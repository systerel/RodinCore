package org.eventb.core.prover.externalReasoners;

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


public class RewriteHyp implements IExternalReasoner{
	
	public String name(){
		return "rewrite hyp";
	}
	

	public IExtReasonerOutput apply(IProverSequent S,IExtReasonerInput I) {
		if (! (I instanceof Input)) throw (new AssertionError(this));
		Input cI = (Input) I;
		
		Rewriter rewriter = cI.rewriter;
		Hypothesis hyp = cI.hyp;
		
		if (rewriter == null) 
			return new UnSuccessfulExtReasonerOutput(this,I,
					"Illegal rewriter: " + rewriter);
		
		// ITypeEnvironment te = S.typeEnvironment();
		if (! S.hypotheses().contains(hyp))
			return new UnSuccessfulExtReasonerOutput(this,I,"nonexistent hypothesis:"+hyp.toString());

		Predicate newHyp = rewriter.apply(hyp.getPredicate());
		if (newHyp == null)
			return new UnSuccessfulExtReasonerOutput(this,I,
					"Rewriter " + rewriter +" inapplicable for hypothesis "+ S.goal());

		Predicate newGoal = Lib.makeImp(newHyp,S.goal());
		Predicate seqGoal = Lib.makeImp(newGoal,S.goal());
		assert seqGoal.isTypeChecked();
		assert seqGoal.isWellFormed();
		ISequent outputSequent = new SimpleSequent(S.typeEnvironment(),Hypothesis.Hypotheses(hyp),seqGoal);
		Proof outputProof = new TrustedProof(outputSequent);
		return new SuccessfullExtReasonerOutput(this,I,outputProof);
	}	
	

	public static class Input implements IExtReasonerInput{

		public final Rewriter rewriter;
		public final Hypothesis hyp;
		
		public Input(Hypothesis hyp, Rewriter rewriter){
			this.hyp = hyp;
			this.rewriter = rewriter;
		}
	}
}
