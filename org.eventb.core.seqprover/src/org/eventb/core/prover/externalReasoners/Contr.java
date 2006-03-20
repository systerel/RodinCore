package org.eventb.core.prover.externalReasoners;

import java.util.Collections;
import java.util.Set;

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


public class Contr implements IExternalReasoner{
	
	public String name(){
		return "contradiction";
	}

	public IExtReasonerOutput apply(IProverSequent S,IExtReasonerInput I) {
		if (! (I instanceof Input)) throw (new AssertionError(this));
		Input aI = (Input) I;
		
		Hypothesis falseHyp = aI.falseHyp;
		Predicate falseHypPred = falseHyp.getPredicate();
		if (!(S.hypotheses().contains(falseHyp) | falseHypPred.equals(Lib.True))) 
			return new UnSuccessfulExtReasonerOutput(this,I,"nonexistent hypothesis:"+falseHyp.toString());
		
		Predicate falseHypPredNeg;
		// ITypeEnvironment te = S.typeEnvironment();
		if (falseHypPred.equals(Lib.True)) falseHypPredNeg = Lib.False;
		else falseHypPredNeg = Lib.makeNeg(falseHypPred);
		Predicate goalNeg = Lib.makeNeg(S.goal());
	
		Predicate newGoal = Lib.makeImp(goalNeg,falseHypPredNeg);	
		Predicate seqGoal = Lib.makeImp(newGoal,S.goal());
		assert seqGoal.isTypeChecked();
		assert seqGoal.isWellFormed();
		Set<Hypothesis> seqHyps;
		if (falseHypPred.equals(Lib.True)) seqHyps = Collections.emptySet();
		else seqHyps = Collections.singleton(falseHyp);
		ISequent outputSequent = new SimpleSequent(S.typeEnvironment(),seqHyps,seqGoal);
		Proof outputProof = new TrustedProof(outputSequent);
		return new SuccessfullExtReasonerOutput(this,I,outputProof);
	}
	
	public static class Input implements IExtReasonerInput{

		public final Hypothesis falseHyp;
		
		public Input(Hypothesis falseHyp){
			this.falseHyp = falseHyp;
		}
		public Input(){
			this.falseHyp = new Hypothesis(Lib.True);
		}
	}

}
