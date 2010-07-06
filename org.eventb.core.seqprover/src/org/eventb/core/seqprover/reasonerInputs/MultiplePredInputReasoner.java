package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;

/**
 * @since 1.0
 */
public abstract class MultiplePredInputReasoner implements IReasoner {

	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		return new MultiplePredInput(reader);
	}

	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer)
			throws SerializeException {
		MultiplePredInput mInput = (MultiplePredInput) input;
		mInput.serialize(writer);
	}

}
