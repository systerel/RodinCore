package org.eventb.core.prover.rules;

import org.eventb.core.prover.SuccessfullExtReasonerOutput;
import org.eventb.core.prover.sequent.HypothesesManagement.Action;

public class RuleFactory {

	Rule conjI;
	Rule impI;
	Rule allI;
	Rule hyp;
	
	public RuleFactory(){
		this.conjI = new ConjI();
		this.impI = new ImpI();
		this.allI = new AllI();
		this.hyp = new Hyp();
	}
	
	public Rule conjI(){
		return conjI;
	}
	
	public Rule impI(){
		return impI;
	}
	
	public Rule allI(){
		return allI;
	}
	
	public Rule hyp(){
		return hyp;
	}
	
	public Rule pLb(SuccessfullExtReasonerOutput pluginOutput){
		return new PLb(pluginOutput);
	}
	
	public Rule mngHyp(Action action){
		return new MngHyp(action);
	}
}
