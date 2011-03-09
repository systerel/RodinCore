package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;

/**
 * @since 1.0
 */
public abstract class EmptyInputReasoner implements IReasoner {

	private static EmptyInput emptyReasonerInput = new EmptyInput();

	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer) {
		// Nothing to serialize
	}

	public IReasonerInput deserializeInput(IReasonerInputReader reader) {
		return emptyReasonerInput;
	}

}
