package org.eventb.core.pm;

import java.util.List;

import org.eventb.core.prover.IProofTreeDelta;
import org.eventb.core.prover.IProofTreeNode;


public interface IProofStateDelta {
	
	public List<Object> getInformation();
	public ProofState getProofState();
	public IProofTreeNode getNewProofTreeNode();
	public IProofTreeDelta getProofTreeDelta();
	public boolean getNewSearch();
	public boolean getNewCache();
	public boolean isDeleted();
	public UserSupport getSource();
	public boolean isNewProofState();
}
