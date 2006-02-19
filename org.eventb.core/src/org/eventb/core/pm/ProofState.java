package org.eventb.core.pm;

import java.util.Collection;
import java.util.HashSet;

import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.SequentProver;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

public class ProofState {
	// TODO Add PO handle to replace IProverSequent
	private IProverSequent ps;
	private IProofTreeNode root;
	private IProofTreeNode current;
	private Collection<Hypothesis> cached;
	private Collection<Hypothesis> search;
	
	public ProofState(IProverSequent ps) {
		this.ps = ps;
		this.root = SequentProver.makeProofTree(ps).getRoot();
		current = null;
		cached = new HashSet<Hypothesis>();
		search = new HashSet<Hypothesis>();
	}
	
	public boolean isDischarged() {
		return root.isDischarged();
	}
	
	public IProofTreeNode getProofTree() {return root;}
	
	public IProofTreeNode getCurrentNode() {return current;}
	
	public void setCurrentNode(IProofTreeNode newNode) {current = newNode;}
	
	public IProofTreeNode getNextPendingSubgoal(IProofTreeNode pt) {
		IProofTreeNode subGoal = pt.getFirstOpenDescendant();
		if (subGoal != null)
			return subGoal;
		return root.getFirstOpenDescendant();
	}

	public IProofTreeNode getNextPendingSubgoal() {
		return root.getFirstOpenDescendant();
	}

	public void addAllToCached(Collection<Hypothesis> hyps) {cached.addAll(hyps);}
}
