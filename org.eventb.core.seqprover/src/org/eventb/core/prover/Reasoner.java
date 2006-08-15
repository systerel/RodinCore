package org.eventb.core.prover;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.prover.sequent.IProverSequent;

public interface Reasoner {
	
	String getReasonerID();
	
	ReasonerInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException;
	
	// ReasonerOutput apply(IProverSequent seq,ReasonerInput input, IProgressMonitor progressMonitor);

	ReasonerOutput apply(IProverSequent seq,ReasonerInput input, IProgressMonitor progressMonitor);

}
