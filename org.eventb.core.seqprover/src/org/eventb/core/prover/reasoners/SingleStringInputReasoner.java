package org.eventb.core.prover.reasoners;

import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.Reasoner;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;

public abstract class SingleStringInputReasoner implements Reasoner {
	
	public SingleStringInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException{
		return new SingleStringInput(reasonerInputSerializer);
	}

}
