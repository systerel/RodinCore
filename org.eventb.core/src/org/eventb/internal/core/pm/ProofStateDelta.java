package org.eventb.internal.core.pm;

import java.util.Collection;

import org.eventb.core.pm.IGoalDelta;
import org.eventb.core.pm.IHypothesisDelta;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.ProofState;
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


	
	private IGoalDelta goalDelta;

	private Collection<IHypothesisDelta> hypothesesDelta;

	private Object information;

	private boolean newSearch;
	
	private boolean newCache;

	public ProofStateDelta() {
		newSearch = false;
		newCache = false;
		ps = null;
		node = null;
		proofTreeDelta = null;
	}

	public ProofStateDelta(IGoalDelta goalDelta,
			Collection<IHypothesisDelta> hypothesesDelta, Object information,
			ProofState proofState) {
		this.goalDelta = goalDelta;
		this.hypothesesDelta = hypothesesDelta;
		this.information = information;
		this.ps = proofState;
	}

	public void setNewCurrentNode(IProofTreeNode node) {
		this.node = node;
	}

	public IGoalDelta getGoalDelta() {
		return goalDelta;
	}

	public Collection<IHypothesisDelta> getHypothesesDelta() {
		return hypothesesDelta;
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

}
