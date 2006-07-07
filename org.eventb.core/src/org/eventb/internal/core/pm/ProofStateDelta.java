package org.eventb.internal.core.pm;

import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.ProofState;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.prover.IProofTreeDelta;
import org.eventb.core.prover.IProofTreeNode;

/**
 * @author htson
 *         <p>
 *         This class contains the delta which used to pass the information to
 *         the User Interface.
 * 
 */
public class ProofStateDelta implements IProofStateDelta {

	private ProofState ps;

	private IProofTreeDelta proofTreeDelta;
	
	private IProofTreeNode node;

	private Object information;

	private boolean newSearch;
	
	private boolean newCache;

	private UserSupport userSupport;
	
	public ProofStateDelta(UserSupport userSupport) {
		this.userSupport = userSupport;
		newSearch = false;
		newCache = false;
		ps = null;
		node = null;
		proofTreeDelta = null;
	}

	public void setNewCurrentNode(IProofTreeNode node) {
		this.node = node;
	}

	public Object getInformation() {
		return information;
	}

	public void setNewProofState(ProofState ps) {
		this.ps = ps;
	}

	public ProofState getNewProofState() {
		return ps;
	}

	public IProofTreeNode getNewProofTreeNode() {
		return node;
	}

	public void setNewSearch() {
		newSearch = true;
	}

	public void setProofTreeDelta(IProofTreeDelta proofTreeDelta) {
		this.proofTreeDelta = proofTreeDelta;
	}

	public IProofTreeDelta getProofTreeDelta() {
		return proofTreeDelta;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = "\n***************************";
		result = result + "\n" + "Proof State: " + ps;
		result = result + "\n" + "ProofTreeDelta: " + proofTreeDelta;
		result = result + "\n" + "Current Node: " + node;
		result = result + "\n" + "New Cache: " + newCache;
		result = result + "\n" + "New Search: " + newSearch;
		result = result + "\n***************************";
		return result;
	}

	public void setNewCache() {
		newCache = true;
	}

	public boolean getNewSearch() {
		return newSearch;
	}

	public boolean getNewCache() {
		return newCache;
	}

	public UserSupport getSource() {
		return userSupport;
	}

}
