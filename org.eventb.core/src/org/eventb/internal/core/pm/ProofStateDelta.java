package org.eventb.internal.core.pm;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeDelta;
import org.eventb.core.seqprover.IProofTreeNode;

/**
 * @author htson
 *         <p>
 *         This class contains the delta which used to pass the information to
 *         the User Interface.
 * 
 */
public class ProofStateDelta implements IProofStateDelta {

	private IProofState ps;

	private IProofTreeDelta proofTreeDelta;

	private IProofTreeNode node;

	private List<Object> information;

	private boolean newSearch;

	private boolean newCache;

	private IUserSupport userSupport;

	private boolean isDeleted;

	private boolean newProofState;

	public ProofStateDelta(IUserSupport userSupport) {
		this.userSupport = userSupport;
		newSearch = false;
		newCache = false;
		ps = null;
		node = null;
		proofTreeDelta = null;
		isDeleted = false;
		newProofState = false;
		information = new ArrayList<Object>();
	}

	public void setNewCurrentNode(IProofTreeNode node) {
		this.node = node;
	}

	public List<Object> getInformation() {
		return information;
	}

	public void setNewProofState(IProofState ps) {
		this.ps = ps;
		newProofState = true;
	}

	public void setDeletedProofState(IProofState ps) {
		this.ps = ps;
		isDeleted = true;
	}

	public IProofState getProofState() {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = "\n***************************";
		result = result + "\n" + "Proof State: " + ps;
		result = result + "\n" + "is deleted: " + isDeleted;
		result = result + "\n" + "is new: " + newProofState;
		result = result + "\n" + "ProofTreeDelta: " + proofTreeDelta;
		result = result + "\n" + "Current Node: " + node;
		result = result + "\n" + "New Cache: " + newCache;
		result = result + "\n" + "New Search: " + newSearch;
		result = result + "\n" + "Information:";
		for (Object info : information) {
			result = result + "\n   " + info;
		}
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

	public IUserSupport getSource() {
		return userSupport;
	}

	public void addInformation(Object info) {
		this.information.add(info);
	}

	public void addAllInformation(List<Object> infos) {
		information.addAll(infos);
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public boolean isNewProofState() {
		return newProofState;
	}

}
