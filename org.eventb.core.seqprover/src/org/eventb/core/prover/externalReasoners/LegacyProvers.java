package org.eventb.core.prover.externalReasoners;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExtReasonerInput;
import org.eventb.core.prover.IExtReasonerOutput;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.SuccessfullExtReasonerOutput;
import org.eventb.core.prover.UnSuccessfulExtReasonerOutput;
import org.eventb.core.prover.externalReasoners.classicB.ClassicB;
import org.eventb.core.prover.proofs.TrustedProof;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.ISequent;
import org.eventb.core.prover.sequent.SimpleSequent;


public class LegacyProvers implements IExternalReasoner{
	
	public String name(){
		return "legacy provers";
	}

	private static boolean runLegacyProvers(IProverSequent S){
		Set<Predicate> hyps = new HashSet<Predicate>(S.visibleHypotheses().size());
//		@SuppressWarnings("unused") String strGoal = S.goal().toString();
		for (Hypothesis hyp: S.visibleHypotheses()){
			hyps.add(hyp.getPredicate());
		}
		StringBuffer sequent = ClassicB.translateSequent(S.typeEnvironment(), hyps, S.goal());
		try {
			if(ClassicB.proveWithML(sequent))
				return true;
			else
				return ClassicB.proveWithPP(sequent);
		} catch (IOException e) {
			return false;
		} catch (InterruptedException e) {
			return false;
		}
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
