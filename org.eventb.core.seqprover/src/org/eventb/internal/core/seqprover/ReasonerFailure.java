package org.eventb.internal.core.seqprover;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerInput;


public class ReasonerFailure extends ReasonerOutput implements IReasonerFailure{
	
	private String reason;
	
	public ReasonerFailure(IReasoner generatedBy, IReasonerInput generatedUsing, String error){
		super(generatedBy,generatedUsing);
		this.reason = error;
	}
	
	public String getReason(){
		return this.reason;
	}
	
	public String toString(){
		return this.reason;
	}

}
