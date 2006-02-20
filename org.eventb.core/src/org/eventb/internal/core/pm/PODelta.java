package org.eventb.internal.core.pm;

import org.eventb.core.pm.IPODelta;
import org.eventb.core.prover.IProofTree;

public class PODelta implements IPODelta {
	IProofTree pt;
	
	public PODelta(IProofTree pt) {
		this.pt = pt;
	}
	
	public IProofTree getProofTree() {
		return pt;
	}

}
