package org.eventb.core.prover.externalReasoners;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExtReasonerInput;
import org.eventb.core.prover.IExtReasonerOutput;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.SuccessfullExtReasonerOutput;
import org.eventb.core.prover.UnSuccessfulExtReasonerOutput;
import org.eventb.core.prover.proofs.Proof;
import org.eventb.core.prover.proofs.TrustedProof;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.ISequent;
import org.eventb.core.prover.sequent.SimpleSequent;


public class ConjD implements IExternalReasoner{
	
	public String name(){
		return "remove conjunction";
	}

	public IExtReasonerOutput apply(IProverSequent S,IExtReasonerInput I) {
		if (! (I instanceof Input)) throw (new AssertionError(this));
		Input aI = (Input) I;
		
		Hypothesis conjHyp = aI.conjHyp;
		Predicate conjHypPred = conjHyp.getPredicate();
		if (! S.hypotheses().contains(conjHyp))
			return new UnSuccessfulExtReasonerOutput(this,I,"nonexistent hypothesis:"+conjHyp.toString());
		if (! Lib.isConj(conjHypPred))
			return new UnSuccessfulExtReasonerOutput(this,I,"Hypothesis not a conjunction:"+conjHyp.toString());
		
		ITypeEnvironment te = S.typeEnvironment();
		Predicate seqGoal = Lib.makeImp(te,Lib.makeGoal(te,Lib.conjuncts(conjHypPred),S.goal()),S.goal());
		ISequent outputSequent = new SimpleSequent(S.typeEnvironment(),conjHyp,seqGoal);
		Proof outputProof = new TrustedProof(outputSequent);
		return new SuccessfullExtReasonerOutput(this,I,outputProof,Lib.deselect(conjHyp));
	}
	
	public static class Input implements IExtReasonerInput{

		public final Hypothesis conjHyp;
		
		public Input(Hypothesis conjHyp){
			assert Lib.isConj(conjHyp.getPredicate());
			this.conjHyp = conjHyp;
		}
	}

}
