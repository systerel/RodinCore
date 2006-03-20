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


public class DisjE implements IExternalReasoner{
	
	public String name(){
		return "do case";
	}

	public IExtReasonerOutput apply(IProverSequent S,IExtReasonerInput I) {
		Hypothesis disjHyp;
		Predicate disjHypPred;
		
		if (I == null || (I instanceof Input &&  ((Input)I).disjHyp == null )){
			// Extract disjubction from goal.
			disjHyp = null;
			if (! (Lib.isImp(S.goal()) && Lib.isDisj(Lib.impLeft(S.goal()))))
				return new UnSuccessfulExtReasonerOutput(this,I,"Empty input and goal not in proper form:"+S.goal());
			disjHypPred = Lib.impLeft(S.goal());
		}
		else
		{
			// Try to use PluginInput
			if (! (I instanceof Input)) throw (new AssertionError(this));
			Input aI = (Input) I;
			disjHyp = aI.disjHyp;
			disjHypPred = disjHyp.getPredicate();
			if (! S.hypotheses().contains(disjHyp))
				return new UnSuccessfulExtReasonerOutput(this,I,"Nonexistent hypothesis:"+disjHyp);
			if (! Lib.isDisj(disjHypPred))
				return new UnSuccessfulExtReasonerOutput(this,I,"Hypothesis is not a disjunction:"+disjHyp);
		}
		
		Predicate[] disjuncts = Lib.disjuncts(disjHypPred);
		Predicate[] cases = new Predicate[disjuncts.length];
		// ITypeEnvironment te = S.typeEnvironment();
		for (int i=0;i<disjuncts.length;i++){
			cases[i] = Lib.makeImp(disjuncts[i],S.goal());
		}
		Predicate newGoal = Lib.makeConj(cases);
		Predicate seqGoal = Lib.makeImp(newGoal,S.goal());
		assert seqGoal.isTypeChecked();
		assert seqGoal.isWellFormed();
		ISequent outputSequent = (disjHyp == null) ? 
				new SimpleSequent(S.typeEnvironment(),seqGoal) :
				new SimpleSequent(S.typeEnvironment(),disjHyp,seqGoal);
		Proof outputProof = new TrustedProof(outputSequent);
		return new SuccessfullExtReasonerOutput(this,I,outputProof,Lib.deselect(disjHyp));
	}
	
	
	public static class Input implements IExtReasonerInput{

		public final Hypothesis disjHyp;
		
		public Input(){
			this.disjHyp = null;
		}
		
		public Input(Hypothesis disjHyp){
			assert Lib.isDisj(disjHyp.getPredicate());
			this.disjHyp = disjHyp;
		}
				
	}

}
