/**
 * 
 */
package org.eventb.core.prover;


public interface IExtReasonerOutput{
	
	public abstract IExternalReasoner generatedBy();
	public abstract IExtReasonerInput generatedUsing();
	
//	public PluginOutput regenerate(IProverSequent S){
//		return this.generatedBy().apply(S,this.generatedUsing());
//	}
}