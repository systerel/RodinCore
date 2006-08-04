package org.eventb.core.prover.reasoners;

import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.SerializableReasonerInput;

public class EmptyInput implements ReasonerInput{

	public EmptyInput()
	{};
	
	public SerializableReasonerInput genSerializable() {
		return new SerializableReasonerInput();
	}
	
}
