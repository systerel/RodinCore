package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.SerializeException;

public abstract class SinglePredInputReasoner implements IReasoner {
	
	public SinglePredInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {

		return new SinglePredInput(reader);
	}

}
