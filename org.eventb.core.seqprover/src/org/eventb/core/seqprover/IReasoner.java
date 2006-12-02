package org.eventb.core.seqprover;


public interface IReasoner {

	String getReasonerID();

	IReasonerInput deserializeInput(
			IReasonerInputReader reasonerInputReader)
			throws SerializeException;

	IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm);

}
