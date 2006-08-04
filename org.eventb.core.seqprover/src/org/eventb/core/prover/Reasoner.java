package org.eventb.core.prover;

import org.eventb.core.prover.sequent.IProverSequent;

public interface Reasoner {
	
	String getReasonerID();
	
	ReasonerOutput apply(IProverSequent seq,ReasonerInput input);

}
