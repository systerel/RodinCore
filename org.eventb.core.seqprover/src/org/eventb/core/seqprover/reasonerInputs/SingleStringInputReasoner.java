package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;

public abstract class SingleStringInputReasoner implements IReasoner {
	
	public SingleStringInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException{
		return new SingleStringInput(reasonerInputSerializer);
	}

}
