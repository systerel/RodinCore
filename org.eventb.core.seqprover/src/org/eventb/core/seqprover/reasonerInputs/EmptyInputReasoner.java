package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputSerializer;

public abstract class EmptyInputReasoner implements IReasoner {

	private static EmptyInput emptyReasonerInput = new EmptyInput();

	public IReasonerInput deserializeInput(
			IReasonerInputSerializer reasonerInputSerializer) {
		return emptyReasonerInput;
	}

}
