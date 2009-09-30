package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * @since 1.0
 */
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

	public void applyHints(ReplayHints hints) {	
		// Nothing to do.
	}
	
}
