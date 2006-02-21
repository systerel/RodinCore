package org.eventb.internal.core.pm;

import org.eventb.core.pm.IGoalDelta;
import org.eventb.core.prover.IProofTreeNode;

public class GoalDelta implements IGoalDelta {
	IProofTreeNode node;
	
	public GoalDelta(IProofTreeNode node) {
		this.node = node;
	}
	
	public IProofTreeNode getProofTreeNode() {
		return node;
	}

}
