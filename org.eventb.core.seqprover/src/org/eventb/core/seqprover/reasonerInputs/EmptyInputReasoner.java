package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;

public abstract class EmptyInputReasoner implements IReasoner {

	private static EmptyInput emptyReasonerInput = new EmptyInput();

	public IReasonerInput deserializeInput(
			IReasonerInputReader reasonerInputReader) {
		return emptyReasonerInput;
	}

}
