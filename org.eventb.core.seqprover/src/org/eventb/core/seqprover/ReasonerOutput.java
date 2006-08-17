package org.eventb.core.seqprover;

public class ReasonerOutput implements IReasonerOutput {
	
	public final IReasoner generatedBy;
	public final IReasonerInput generatedUsing;
	
	public ReasonerOutput(IReasoner generatedBy, IReasonerInput generatedUsing){
		this.generatedBy = generatedBy;
		this.generatedUsing = generatedUsing;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.seqprover.IReasonerOutput#generatedBy()
	 */
	public IReasoner generatedBy(){
		return generatedBy;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.seqprover.IReasonerOutput#genaratedUsing()
	 */
	public IReasonerInput genaratedUsing(){
		return generatedUsing;
	}
	

}
