package org.eventb.core.prover;

public class ReasonerOutput {
	
	public final Reasoner generatedBy;
	public final ReasonerInput generatedUsing;
	
	public ReasonerOutput(Reasoner generatedBy, ReasonerInput generatedUsing){
		this.generatedBy = generatedBy;
		this.generatedUsing = generatedUsing;
	}
	

}
