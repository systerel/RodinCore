package org.eventb.core.prover;


public class ReasonerOutputFail extends ReasonerOutput{
	
	public String error;
	
	public ReasonerOutputFail(Reasoner generatedBy, ReasonerInput generatedUsing){
		super(generatedBy,generatedUsing);
	}
	
	public ReasonerOutputFail(Reasoner generatedBy, ReasonerInput generatedUsing, String error){
		super(generatedBy,generatedUsing);
		this.error = error;
	}
	
	public String toString(){
		return this.error;
	}

}
