package org.eventb.core.seqprover;

import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * Common protocol for reasoner inputs.
 * 
 * @author Farhad Mehta
 */
public interface IReasonerInput {
	
	// TODO document this method
	public boolean hasError();
	
	// TODO document this method
	public String getError();
	
	// TODO document this method
	public void serialize(IReasonerInputWriter reasonerInputWriter)
			throws SerializeException;

	// TODO change return type to IReasonerInput!
	public void applyHints(ReplayHints hints);

}
