package org.eventb.core.seqprover;


public class ReasonerOutputFail extends ReasonerOutput{
	
	public String error;
	
	public ReasonerOutputFail(IReasoner generatedBy, IReasonerInput generatedUsing){
		super(generatedBy,generatedUsing);
	}
	
	public ReasonerOutputFail(IReasoner generatedBy, IReasonerInput generatedUsing, String error){
		super(generatedBy,generatedUsing);
		this.error = error;
	}
	
	public String toString(){
		return this.error;
	}

}
