package org.eventb.core.prover.reasonerInputs;

import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.IReasoner;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;

public abstract class SinglePredInputReasoner implements IReasoner {
	
	public SinglePredInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException{
		return new SinglePredInput(reasonerInputSerializer);
	}

}
