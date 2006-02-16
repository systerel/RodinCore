package org.eventb.core.prover.externalReasoners;

import static org.eventb.core.prover.Lib.*;

import java.util.Collections;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.prover.IExtReasonerInput;
import org.eventb.core.prover.IExtReasonerOutput;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.SuccessfullExtReasonerOutput;
import org.eventb.core.prover.UnSuccessfulExtReasonerOutput;
import org.eventb.core.prover.proofs.Proof;
import org.eventb.core.prover.proofs.TrustedProof;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.ISequent;
import org.eventb.core.prover.sequent.SimpleSequent;


public class Trivial implements IExternalReasoner{
	
	public String name(){
		return "trivial";
	}
	
	public boolean isApplicable(IProverSequent S,IExtReasonerInput I) {
		if (S.goal().equals(True)) return true;
		if (S.hypotheses().contains(False)) return true;
		return false;
	}

	private static Proof trueGoal(){
		Set<Hypothesis> emptyHyps = Collections.emptySet();
		ITypeEnvironment emptyTypeEnv = makeTypeEnvironment();
		ISequent outputSequent = 
			new SimpleSequent(emptyTypeEnv,emptyHyps,True);
		return new TrustedProof(outputSequent);
	}
	
	private static Proof falseHyp(){
		Set<Hypothesis> falseHyp = Hypothesis.Hypotheses(False);
		ITypeEnvironment emptyTypeEnv = makeTypeEnvironment();
		ISequent outputSequent = 
			new SimpleSequent(emptyTypeEnv,falseHyp,False);
		return new TrustedProof(outputSequent);
	}
	
	public IExtReasonerOutput apply(IProverSequent S,IExtReasonerInput I) {

//		This plugin ignores input.		
//		if (! (I instanceof Input)) throw (new AssertionError(this));
//		Input aI = (Input) I;
		
		if (S.goal().equals(True)) return new SuccessfullExtReasonerOutput(this,I,trueGoal());
		if (S.hypotheses().contains(False)) return new SuccessfullExtReasonerOutput(this,I,falseHyp());
		return new UnSuccessfulExtReasonerOutput(this,I,"Not applicable");
	}
	
	public IExtReasonerInput defaultInput(){
		return new Input();
	}

	
	public static class Input implements IExtReasonerInput{
		
		public Input(){
		}
	}

}
