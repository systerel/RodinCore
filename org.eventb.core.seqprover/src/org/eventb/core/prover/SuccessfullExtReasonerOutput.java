/**
 * 
 */
package org.eventb.core.prover;

import java.util.Collections;
import java.util.List;

import org.eventb.core.prover.proofs.Proof;
import org.eventb.core.prover.sequent.HypothesesManagement;
import org.eventb.core.prover.sequent.HypothesesManagement.Action;

public class SuccessfullExtReasonerOutput implements IExtReasonerOutput{
	private final Proof proof;
	private final List<Action> hypActions;
	private final IExternalReasoner generatedBy;
	private final IExtReasonerInput generatedUsing;
	
	public SuccessfullExtReasonerOutput(IExternalReasoner generatedBy, IExtReasonerInput generatedUsing,Proof proof){
		this.proof = proof;
		this.hypActions = null;
		this.generatedBy = generatedBy;
		this.generatedUsing = generatedUsing;
	}
	
	public SuccessfullExtReasonerOutput(IExternalReasoner generatedBy, IExtReasonerInput generatedUsing,Proof proof, List<Action> hypActions){
		this.proof = proof;
		this.hypActions = hypActions;
		this.generatedBy = generatedBy;
		this.generatedUsing = generatedUsing;
	}
	
	public SuccessfullExtReasonerOutput(IExternalReasoner generatedBy, IExtReasonerInput generatedUsing,Proof proof,Action hypAction){
		this.proof = proof;
		this.hypActions = Collections.singletonList(hypAction);
		this.generatedBy = generatedBy;
		this.generatedUsing = generatedUsing;
	}
	
	public Proof proof(){
		return this.proof;
	}
	
	public List<HypothesesManagement.Action> hypActions(){
		return this.hypActions;
	}
	
	public IExternalReasoner generatedBy(){
		return this.generatedBy;
	}
	
	public IExtReasonerInput generatedUsing(){
		return this.generatedUsing;
	}
	
}