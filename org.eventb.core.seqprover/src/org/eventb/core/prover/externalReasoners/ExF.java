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


public class ExF implements IExternalReasoner{
	
	public String name(){
		return "remove existential";
	}
	
	public IExtReasonerOutput apply(IProverSequent S,IExtReasonerInput I) {
		Hypothesis exHyp;
		Predicate exHypPred;
		
		if (I == null || (I instanceof Input &&  ((Input)I).exHyp == null )){
			// Extract existential from goal.
			exHyp = null;
			if (! (Lib.isImp(S.goal()) && Lib.isExQuant(Lib.impLeft(S.goal()))))
				return new UnSuccessfulExtReasonerOutput(this,I,"Empty input and goal not in proper form:"+S.goal());
			exHypPred = Lib.impLeft(S.goal());
		}
		else
		{
			// Try to use PluginInput
			if (! (I instanceof Input)) throw (new AssertionError(this));
			Input aI = (Input) I;
			exHyp = aI.exHyp;
			exHypPred = exHyp.getPredicate();
			if (! S.hypotheses().contains(exHyp))
				return new UnSuccessfulExtReasonerOutput(this,I,"Nonexistent hypothesis:"+exHyp);
			if (! Lib.isExQuant(exHypPred))
				return new UnSuccessfulExtReasonerOutput(this,I,"Hypothesis is not existentially quantified:"+exHyp);
		}
		
		
		// ITypeEnvironment te = S.typeEnvironment();
		Predicate newGoal = Lib.makeUnivQuant(Lib.getBoundIdents(exHypPred),
				Lib.makeImp(Lib.getBoundPredicate(exHypPred),S.goal()));
		// Note : newGoal may be not be type checkable here since it may have bound idents
		
		Predicate seqGoal = Lib.makeImp(newGoal,S.goal());
		assert seqGoal.isTypeChecked();
		assert seqGoal.isWellFormed();
		ISequent outputSequent = (exHyp == null) ? 
				new SimpleSequent(S.typeEnvironment(),seqGoal) :
				new SimpleSequent(S.typeEnvironment(),exHyp,seqGoal);
		Proof outputProof = new TrustedProof(outputSequent);
		return new SuccessfullExtReasonerOutput(this,I,outputProof,Lib.deselect(exHyp));
	}
	
	public static class Input implements IExtReasonerInput{

		public final Hypothesis exHyp;
		
		public Input(Hypothesis exHyp){
			assert Lib.isExQuant(exHyp.getPredicate());
			this.exHyp = exHyp;
		}
				
	}

}
