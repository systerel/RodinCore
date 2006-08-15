package org.eventb.core.prover.reasoners;

import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.Reasoner;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;

public abstract class SinglePredInputReasoner implements Reasoner {
	
	public SinglePredInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException{
		return new SinglePredInput(reasonerInputSerializer);
	}

}
