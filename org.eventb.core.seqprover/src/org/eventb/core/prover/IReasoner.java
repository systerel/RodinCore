package org.eventb.core.prover;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.prover.sequent.IProverSequent;

public interface IReasoner {
	
	String getReasonerID();
	
	IReasonerInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException;
	
	ReasonerOutput apply(IProverSequent seq,IReasonerInput input, IProgressMonitor progressMonitor);

}
