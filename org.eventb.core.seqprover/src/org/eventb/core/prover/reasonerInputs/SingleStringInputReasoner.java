package org.eventb.core.prover.reasonerInputs;

import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.IReasoner;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;

public abstract class SingleStringInputReasoner implements IReasoner {
	
	public SingleStringInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException{
		return new SingleStringInput(reasonerInputSerializer);
	}

}
