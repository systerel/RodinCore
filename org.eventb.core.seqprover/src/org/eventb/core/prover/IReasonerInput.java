package org.eventb.core.prover;

import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;



public interface IReasonerInput {
	
	public boolean hasError();
	
	public String getError();
	
	public void serialize(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException;

	public void applyHints(ReplayHints hints);

}
