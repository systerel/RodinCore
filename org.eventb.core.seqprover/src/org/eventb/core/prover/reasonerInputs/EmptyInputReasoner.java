package org.eventb.core.prover.reasonerInputs;

import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.IReasoner;
import org.eventb.core.prover.IReasonerInput;

public abstract class EmptyInputReasoner implements IReasoner {

	private static EmptyInput emptyReasonerInput = new EmptyInput();

	public IReasonerInput deserializeInput(
			IReasonerInputSerializer reasonerInputSerializer) {
		return emptyReasonerInput;
	}

}
