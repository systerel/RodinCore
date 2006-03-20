package org.eventb.core.prover.externalReasoners;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExtReasonerInput;
import org.eventb.core.prover.IExtReasonerOutput;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.SuccessfullExtReasonerOutput;
import org.eventb.core.prover.UnSuccessfulExtReasonerOutput;
import org.eventb.core.prover.proofs.Proof;
import org.eventb.core.prover.proofs.TrustedProof;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.ISequent;
import org.eventb.core.prover.sequent.SimpleSequent;

public class ExI implements IExternalReasoner{
	
	public String name(){
		return "provide witness";
	}

	public IExtReasonerOutput apply(IProverSequent S,IExtReasonerInput I) {
		if (! (I instanceof Input)) throw (new AssertionError(this));
		
		if (! Lib.isExQuant(S.goal())) 
			return new UnSuccessfulExtReasonerOutput(this,I,"Goal is not existentially quantified:"+S.goal());
		
		BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(S.goal());
		Input eI = (Input) I;
		
		Expression[] witnesses = new Expression[boundIdentDecls.length];
		// Fill the witnesses array using plugin input.		
		Expression witness;
		for (int i=0;i<boundIdentDecls.length;i++){
			if (i >= eI.witnesses.length || 
					eI.witnesses[i] == null || 
					eI.witnesses[i].length() == 0) 
				witnesses[i] = null;
			else
			{
				witness = Lib.parseExpression(eI.witnesses[i]);
				if (witness == null) 
					return new UnSuccessfulExtReasonerOutput(this,I,
							"Parse error for expression "+eI.witnesses[i]);
				if (! Lib.isWellTypedInstantiation(witness,boundIdentDecls[i].getType(),S.typeEnvironment())) 
					return new UnSuccessfulExtReasonerOutput(this,I,
							"Type check failed : "+eI.witnesses[i]+" expected type "+ boundIdentDecls[i].getType());
				witnesses[i] = witness;
			}
		}
		
		// We can now assume that <code>witnesses</code> have been properly parsed and typed.
		// ITypeEnvironment te = S.typeEnvironment();
		Predicate WDpred = Lib.WD(witnesses);
		Predicate instantiatedPred = Lib.instantiateBoundIdents(S.goal(),witnesses);
		Predicate newGoal = Lib.makeConj(WDpred,instantiatedPred);
		Predicate seqGoal = Lib.makeImp(newGoal,S.goal());
		assert seqGoal.isTypeChecked();
		assert seqGoal.isWellFormed();
		ISequent outputSequent = new SimpleSequent(S.typeEnvironment(),seqGoal);
		Proof outputProof = new TrustedProof(outputSequent);
		return new SuccessfullExtReasonerOutput(this,I,outputProof);
	}
	
	
	public static class Input implements IExtReasonerInput{
		public final String[] witnesses;
		public Input(String[] witnesses){
			this.witnesses = witnesses;
		}
		
	}

}
