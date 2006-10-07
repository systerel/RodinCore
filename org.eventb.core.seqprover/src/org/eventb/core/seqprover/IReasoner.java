package org.eventb.core.seqprover;

import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;

public interface IReasoner {

	String getReasonerID();

	IReasonerInput deserializeInput(
			IReasonerInputSerializer reasonerInputSerializer)
			throws SerializeException;

	IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm);

}
