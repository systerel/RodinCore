package org.eventb.core.prover;

import org.eventb.core.prover.sequent.IProverSequent;

public interface Reasoner {
	
	public String getReasonerID();
	
	public ReasonerOutput apply(IProverSequent seq,ReasonerInput input);
	
	public static class DefaultInput implements ReasonerInput{

		public DefaultInput()
		{};
		
		public SerializableReasonerInput genSerializable() {
			return new SerializableReasonerInput();
		}
		
	}

}
