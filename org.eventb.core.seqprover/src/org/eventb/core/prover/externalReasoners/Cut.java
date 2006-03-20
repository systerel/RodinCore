package org.eventb.core.prover.externalReasoners;

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

public class Cut implements IExternalReasoner{
	
	public String name(){
		return "add hypothesis";
	}

	public IExtReasonerOutput apply(IProverSequent S,IExtReasonerInput I) {
		if (! (I instanceof Input)) throw (new AssertionError(this));
		Input cI = (Input) I;
		
		Predicate lemma = Lib.parsePredicate(cI.lemma);
		if (lemma == null) 
			return new UnSuccessfulExtReasonerOutput(this,I,
					"Parse error for lemma: "+ cI.lemma);
		if (! Lib.isWellTyped(lemma,S.typeEnvironment()))
			return new UnSuccessfulExtReasonerOutput(this,I,
					"Type check failed for lemma: "+cI.lemma);
		
		// We can now assume that <code>lemma</code> has been properly parsed and typed.
		
		// ITypeEnvironment te = S.typeEnvironment();
		Predicate lemmaWD = Lib.WD(lemma);
		Predicate lemmaImpGoal = Lib.makeImp(lemma,S.goal());
		Predicate newGoal = Lib.makeConj(lemma,lemmaWD,lemmaImpGoal);
		Predicate seqGoal = Lib.makeImp(newGoal,S.goal());
		assert seqGoal.isTypeChecked();
		assert seqGoal.isWellFormed();
		ISequent outputSequent = new SimpleSequent(S.typeEnvironment(),Hypothesis.Hypotheses(),seqGoal);
		Proof outputProof = new TrustedProof(outputSequent);
		return new SuccessfullExtReasonerOutput(this,I,outputProof);
	}
	
	public static class Input implements IExtReasonerInput{
		public final String lemma;
		public Input(String lemma){
			this.lemma = lemma;
		}
		
		@Override
		public String toString(){
			return this.lemma;
		}
		
	}

}
