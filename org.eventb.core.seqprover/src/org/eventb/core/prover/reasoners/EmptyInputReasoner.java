package org.eventb.core.prover.reasoners;

import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.Reasoner;
import org.eventb.core.prover.ReasonerInput;

public abstract class EmptyInputReasoner implements Reasoner {

	private static EmptyInput emptyReasonerInput = new EmptyInput();

	public ReasonerInput deserializeInput(
			IReasonerInputSerializer reasonerInputSerializer) {
		return emptyReasonerInput;
	}

}
