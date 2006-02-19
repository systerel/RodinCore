package org.eventb.core.prover.rules;

import org.eventb.core.prover.SuccessfullExtReasonerOutput;
import org.eventb.core.prover.sequent.HypothesesManagement.Action;

public class RuleFactory {

	IProofRule conjI;
	IProofRule impI;
	IProofRule allI;
	IProofRule hyp;
	
	public RuleFactory(){
		this.conjI = new ConjI();
		this.impI = new ImpI();
		this.allI = new AllI();
		this.hyp = new Hyp();
	}
	
	public IProofRule conjI(){
		return conjI;
	}
	
	public IProofRule impI(){
		return impI;
	}
	
	public IProofRule allI(){
		return allI;
	}
	
	public IProofRule hyp(){
		return hyp;
	}
	
	public IProofRule pLb(SuccessfullExtReasonerOutput pluginOutput){
		return new PLb(pluginOutput);
	}
	
	public IProofRule mngHyp(Action action){
		return new MngHyp(action);
	}
}
