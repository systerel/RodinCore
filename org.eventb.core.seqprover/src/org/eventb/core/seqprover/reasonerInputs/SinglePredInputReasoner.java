package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;

/**
 * @since 1.0
 */
public abstract class SinglePredInputReasoner implements IReasoner {
	
	public void serializeInput(IReasonerInput rInput,
			IReasonerInputWriter writer) throws SerializeException {
		SinglePredInput input = (SinglePredInput) rInput;
		input.serialize(writer);
	}

	public SinglePredInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {

		return new SinglePredInput(reader);
	}

}
