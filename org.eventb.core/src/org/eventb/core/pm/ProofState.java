package org.eventb.core.pm;

import java.util.Collection;
import java.util.HashSet;

import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.sequent.Hypothesis;

public class ProofState {
	// TODO Add PO handle to replace IProverSequent
	private IProofTree pt;
	private IProofTreeNode current;
	private Collection<Hypothesis> cached;
	private Collection<Hypothesis> displayCached;
	private Collection<Hypothesis> searched;
	private Collection<Hypothesis> displaySearched;
	
	public ProofState(IProofTree pt) {
		this.pt = pt;
		current = null;
		cached = new HashSet<Hypothesis>();
		searched = new HashSet<Hypothesis>();
		displayCached = new HashSet<Hypothesis>();
		displaySearched = new HashSet<Hypothesis>();
	}
	
	public boolean isDischarged() {
		return pt.isDischarged();
	}
	
	public IProofTree getProofTree() {return pt;}
	
	public IProofTreeNode getCurrentNode() {return current;}
	
	public void setCurrentNode(IProofTreeNode newNode) {current = newNode;}
	
	public IProofTreeNode getNextPendingSubgoal(IProofTreeNode node) {
		IProofTreeNode subGoal = node.getFirstOpenDescendant();
		if (subGoal != null)
			return subGoal;
		return pt.getRoot().getFirstOpenDescendant();
	}

	public IProofTreeNode getNextPendingSubgoal() {
		return pt.getRoot().getFirstOpenDescendant();
	}

	public void addAllToCached(Collection<Hypothesis> hyps) {cached.addAll(hyps);}

	public void removeAllFromCached(Collection<Hypothesis> hyps) {cached.removeAll(hyps);}

	public Collection<Hypothesis> getCached() {return cached;}

	public Collection<Hypothesis> getDisplayCached() {return displayCached;}

	public void setDisplayCached(Collection<Hypothesis> newDisplayCached) {
		displayCached = newDisplayCached;
	}
	
	public void addAllToSearched(Collection<Hypothesis> hyps) {searched.addAll(hyps);}

	public void removeAllFromSeached(Collection<Hypothesis> hyps) {searched.removeAll(hyps);}

	public Collection<Hypothesis> getSearched() {return searched;}

	public Collection<Hypothesis> getDisplaySearched() {return displaySearched;}

	public void setDisplaySearched(Collection<Hypothesis> newDisplaySearched) {
		displayCached = newDisplaySearched;
	}
}
