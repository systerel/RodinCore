package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.eventb.core.seqprover.ReplayHints;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;

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
