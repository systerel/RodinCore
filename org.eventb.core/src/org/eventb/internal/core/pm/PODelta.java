package org.eventb.internal.core.pm;

import org.eventb.core.pm.IPODelta;
import org.eventb.core.prover.rules.ProofTree;

public class PODelta implements IPODelta {
	ProofTree pt;
	
	public PODelta(ProofTree pt) {
		this.pt = pt;
	}
	
	public ProofTree getProofTree() {
		return pt;
	}

}
