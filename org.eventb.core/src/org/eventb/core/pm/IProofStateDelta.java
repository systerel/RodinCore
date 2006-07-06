package org.eventb.core.pm;

import org.eventb.core.prover.IProofTreeDelta;
import org.eventb.core.prover.IProofTreeNode;


public interface IProofStateDelta {
	
	public Object getInformation();
	public ProofState getNewProofState();
	public IProofTreeNode getNewProofTreeNode();
	public IProofTreeDelta getProofTreeDelta();
	public boolean getNewSearch();
	public boolean getNewCache();
}
