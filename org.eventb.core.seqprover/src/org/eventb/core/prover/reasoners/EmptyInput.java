package org.eventb.core.prover.reasoners;

import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.IReasonerInput;
import org.eventb.core.prover.ReplayHints;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;

public class EmptyInput implements IReasonerInput{

	public EmptyInput()
	{};
	
	public boolean hasError() {
		return false;
	}

	public String getError() {
		return null;
	}

	public void serialize(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		// TODO Auto-generated method stub
		
	}

	public void applyHints(ReplayHints hints) {
		// TODO Auto-generated method stub
		
	}
	
}
