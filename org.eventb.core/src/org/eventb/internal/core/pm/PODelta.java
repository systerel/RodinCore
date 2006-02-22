package org.eventb.internal.core.pm;

import org.eventb.core.pm.IPODelta;
import org.eventb.core.pm.ProofState;

public class PODelta implements IPODelta {
	ProofState ps;
	
	public PODelta(ProofState ps) {
		this.ps = ps;
	}
	
	public ProofState getProofState() {
		return ps;
	}

}
