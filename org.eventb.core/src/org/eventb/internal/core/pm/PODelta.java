package org.eventb.internal.core.pm;

import org.eventb.core.pm.IPODelta;
import org.eventb.core.prover.IProofTreeNode;

public class PODelta implements IPODelta {
	IProofTreeNode pt;
	
	public PODelta(IProofTreeNode pt) {
		this.pt = pt;
	}
	
	public IProofTreeNode getProofTree() {
		return pt;
	}

}
