package org.eventb.core.pm;

import java.util.Collection;

import org.eventb.core.prover.IProofTreeNode;


public interface IProofStateDelta {
	
	public IGoalDelta getGoalDelta();
	public Collection<IHypothesisDelta> getHypothesesDelta();
	public Object getInformation();
	public ProofState getNewProofState();
	public IProofTreeNode getNewProofTreeNode();
}
