package org.eventb.core.seqprover;

public class ReasonerOutput {
	
	public final IReasoner generatedBy;
	public final IReasonerInput generatedUsing;
	
	public ReasonerOutput(IReasoner generatedBy, IReasonerInput generatedUsing){
		this.generatedBy = generatedBy;
		this.generatedUsing = generatedUsing;
	}
	

}
