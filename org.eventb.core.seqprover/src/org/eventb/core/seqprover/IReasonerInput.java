package org.eventb.core.seqprover;

import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;



public interface IReasonerInput {
	
	public boolean hasError();
	
	public String getError();
	
	public void serialize(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException;

	public void applyHints(ReplayHints hints);

}
