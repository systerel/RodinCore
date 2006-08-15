package org.eventb.core.prover;

public class ReasonerOutput {
	
	public final IReasoner generatedBy;
	public final IReasonerInput generatedUsing;
	
	public ReasonerOutput(IReasoner generatedBy, IReasonerInput generatedUsing){
		this.generatedBy = generatedBy;
		this.generatedUsing = generatedUsing;
	}
	

}
