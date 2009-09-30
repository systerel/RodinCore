package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;

/**
 * @since 1.0
 */
public abstract class SingleStringInputReasoner implements IReasoner {

	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer)
			throws SerializeException {
		((SingleStringInput) input).serialize(writer);
	}

	public SingleStringInput deserializeInput(
			IReasonerInputReader reasonerInputReader) throws SerializeException {
		return new SingleStringInput(reasonerInputReader);
	}

}
