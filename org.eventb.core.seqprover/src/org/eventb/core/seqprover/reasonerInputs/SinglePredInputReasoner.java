package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;

public abstract class SinglePredInputReasoner implements IReasoner {
	
	public SinglePredInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException{
		return new SinglePredInput(reasonerInputSerializer);
	}

}
