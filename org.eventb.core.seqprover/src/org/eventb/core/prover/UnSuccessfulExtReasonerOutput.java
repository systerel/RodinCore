/**
 * 
 */
package org.eventb.core.prover;


public class UnSuccessfulExtReasonerOutput implements IExtReasonerOutput{
	public Object error;
	private final IExternalReasoner generatedBy;
	private final IExtReasonerInput generatedUsing;
	
	public UnSuccessfulExtReasonerOutput(IExternalReasoner generatedBy, IExtReasonerInput generatedUsing,Object error){
		this.error = error;
		this.generatedBy = generatedBy;
		this.generatedUsing = generatedUsing;
	}
	
	public Object error(){
		return this.error;
	}
	
	public IExternalReasoner generatedBy(){
		return this.generatedBy;
	}
	
	public IExtReasonerInput generatedUsing(){
		return this.generatedUsing;
	}
	
	@Override
	public String toString(){
		// return generatedBy.name()+"["+generatedUsing+"]:"+error;
		return generatedBy.name()+":"+error;
	}
	
	
}