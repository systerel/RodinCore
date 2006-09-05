package org.eventb.core.seqprover;


// TODO : rename to ReasonerFailure
public class ReasonerOutputFail extends ReasonerOutput implements IReasonerFailure{
	
	// TODO : rename to reason
	private String error;
	
	@Deprecated
	public ReasonerOutputFail(IReasoner generatedBy, IReasonerInput generatedUsing, String error){
		super(generatedBy,generatedUsing);
		this.error = error;
	}
	
	public String getReason(){
		return this.error;
	}
	
	public String toString(){
		return this.error;
	}

}
