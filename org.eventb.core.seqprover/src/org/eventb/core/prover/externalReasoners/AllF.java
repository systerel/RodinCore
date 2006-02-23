package org.eventb.core.prover.externalReasoners;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
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

public class AllF implements IExternalReasoner{
	
	public String name(){
		return "instantiate";
	}
	
//	public boolean isApplicable(ProverSequent S,PluginInput I) {
//		return true;
//	}

	public IExtReasonerOutput apply(IProverSequent S,IExtReasonerInput I) {
		if (! (I instanceof Input)) throw (new AssertionError(this));
		Input aI = (Input) I;
		
		Hypothesis univHyp = aI.univHyp;
		Predicate univHypPred = univHyp.getPredicate();
		if (! S.hypotheses().contains(univHyp)) 
			return new UnSuccessfulExtReasonerOutput(this,I,"nonexistent hypothesis:"+univHyp.toString());
		if (! Lib.isUnivQuant(univHypPred)) 
			return new UnSuccessfulExtReasonerOutput(this,I,"hypothesis not universally quantified:"+univHyp.toString());

		BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(univHypPred);
		Expression[] instantiations = new Expression[boundIdentDecls.length];
		
		// Fill the instantiations map using plugin input.
		Expression instantiation;
		for (int i=0;i<boundIdentDecls.length;i++){
			if (aI.instantiations[i] == null || aI.instantiations[i].length() == 0) instantiations[i] = null;
			else
			{
				instantiation = Lib.parseExpression(aI.instantiations[i]);
				if (instantiation == null) 
					return new UnSuccessfulExtReasonerOutput(this,I,
							"Parse error for expression "+aI.instantiations[i]);
				if (! Lib.isWellTyped(instantiation,S.typeEnvironment())) 
					return new UnSuccessfulExtReasonerOutput(this,I,
							"Type check failed for expression "+aI.instantiations[i]);
				if (! boundIdentDecls[i].getType().equals(instantiation.getType())) 
					return new UnSuccessfulExtReasonerOutput(this,I,"types do not match for bounded identifier "+i);
				instantiations[i] = instantiation;
			}
		}
		
		// We can now assume that <code>instantiations</code> have been properly parsed and typed.
		ITypeEnvironment te = S.typeEnvironment();
		Predicate goal = S.goal();
		Predicate WDpred = Lib.WD(te,instantiations);
		Predicate instantiatedPred = Lib.instantiateBoundIdents(te,univHypPred,instantiations);
		Predicate instantiatedPredImpGoal = Lib.makeImp(te,instantiatedPred,goal);
		Predicate newGoal = Lib.makeConj(te,WDpred,instantiatedPredImpGoal);
		Predicate seqGoal = Lib.makeImp(te,newGoal,goal);
		Lib.typeCheck(seqGoal);
		ISequent outputSequent = new SimpleSequent(S.typeEnvironment(),univHyp,seqGoal);
		Proof outputProof = new TrustedProof(outputSequent);
		return new SuccessfullExtReasonerOutput(this,I,outputProof);
	}
	
	public IExtReasonerInput defaultInput(){
		return null;
	}
	
	public static class Input implements IExtReasonerInput{
		public final String[] instantiations;
		public final Hypothesis univHyp;
		
		public Input(String[] instantiations,Hypothesis univHyp){
			assert Lib.isUnivQuant(univHyp.getPredicate());
			assert (Lib.getBoundIdents(univHyp.getPredicate()).length == instantiations.length);
			this.instantiations = instantiations;
			this.univHyp = univHyp;
		}

	}

}
