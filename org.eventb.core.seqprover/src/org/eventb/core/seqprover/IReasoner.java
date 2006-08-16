package org.eventb.core.seqprover;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.seqprover.sequent.IProverSequent;

public interface IReasoner {
	
	String getReasonerID();
	
	IReasonerInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException;
	
	ReasonerOutput apply(IProverSequent seq,IReasonerInput input, IProgressMonitor progressMonitor);

}
