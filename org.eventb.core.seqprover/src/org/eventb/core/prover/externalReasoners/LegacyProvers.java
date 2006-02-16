package org.eventb.core.prover.externalReasoners;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.prover.IExtReasonerInput;
import org.eventb.core.prover.IExtReasonerOutput;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.SuccessfullExtReasonerOutput;
import org.eventb.core.prover.UnSuccessfulExtReasonerOutput;
import org.eventb.core.prover.proofs.TrustedProof;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.ISequent;
import org.eventb.core.prover.sequent.SimpleSequent;


public class LegacyProvers implements IExternalReasoner{
	
	public String name(){
		return "lagacy provers";
	}

	private static boolean runLegacyProvers(IProverSequent S){
		Set<String> strHyps = new HashSet<String>(S.visibleHypotheses().size());
		@SuppressWarnings("unused") String strGoal = S.goal().toString();
		for (Hypothesis hyp: S.visibleHypotheses()){
			strHyps.add(hyp.getPredicate().toString());
		}
// TODO : 	Add code to call legasy provers here using strHyps and strGoal 
//			and return true of false depending on output
		return false;
	}
	
	public IExtReasonerOutput apply(IProverSequent S,IExtReasonerInput I) {	
		if (runLegacyProvers(S)) {
			ISequent outputSequent = 
				new SimpleSequent(S.typeEnvironment(),S.visibleHypotheses(),S.goal());
			return new SuccessfullExtReasonerOutput(this,I,new TrustedProof(outputSequent));
		}
		else
		{
			return new UnSuccessfulExtReasonerOutput(this,I,"Legacy provers failed");
		}
	}
	
	public IExtReasonerInput defaultInput(){
		return new Input();
	}

	
	public static class Input implements IExtReasonerInput{
		
		public Input(){
		}
	}

}
