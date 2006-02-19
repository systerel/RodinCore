package org.eventb.internal.core.pm;

import org.eventb.core.pm.IGoalDelta;
import org.eventb.core.prover.IProofTreeNode;

public class ProofTreeDelta implements IGoalDelta {
	IProofTreeNode pt;
	
	public ProofTreeDelta(IProofTreeNode pt) {
		this.pt = pt;
	}
	
	public IProofTreeNode getProofTree() {
		return pt;
	}

}
