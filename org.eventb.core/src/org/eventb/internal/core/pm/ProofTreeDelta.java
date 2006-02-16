package org.eventb.internal.core.pm;

import org.eventb.core.pm.IGoalDelta;
import org.eventb.core.prover.rules.ProofTree;

public class ProofTreeDelta implements IGoalDelta {
	ProofTree pt;
	
	public ProofTreeDelta(ProofTree pt) {
		this.pt = pt;
	}
	
	public ProofTree getProofTree() {
		return pt;
	}

}
