package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

public class EmptyInput implements IReasonerInput {

	public EmptyInput() {
		// Nothing to initialize
	};
	
	public boolean hasError() {
		return false;
	}

	public String getError() {
		return null;
	}

	public void serialize(IReasonerInputWriter writer)
			throws SerializeException {
		// Nothing to do.
	}

	public void applyHints(ReplayHints hints) {	
		// Nothing to do.
	}
	
}
