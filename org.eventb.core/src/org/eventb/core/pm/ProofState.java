package org.eventb.core.pm;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eventb.core.prover.rules.ProofTree;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

public class ProofState {
	// TODO Add PO handle to replace IProverSequent
	IProverSequent ps;
	ProofTree root;
	ProofTree current;
	Collection<Hypothesis> cached;
	Collection<Hypothesis> search;
	
	public ProofState(IProverSequent ps) {
		this.ps = ps;
		this.root = new ProofTree(ps);
		current = null;
		cached = new HashSet<Hypothesis>();
		search = new HashSet<Hypothesis>();
	}
	
	public boolean isDischarged() {
		return root.isClosed();
	}
	
	public ProofTree getProofTree() {return root;}
	
	public ProofTree getCurrentNode() {return current;}
	
	public void setCurrentNode(ProofTree newNode) {current = newNode;}
	
	public ProofTree getNextPendingSubgoal(ProofTree pt) {
		List<ProofTree> subGoals = pt.pendingSubgoals();
		if (subGoals.size() != 0) return subGoals.get(0);
		else {
			subGoals = root.pendingSubgoals();
			if (subGoals != null && subGoals.size() != 0) {
				return subGoals.get(0);
			}
			else {
				return null;
			}
		}
	}

	public ProofTree getNextPendingSubgoal() {
		List<ProofTree> subGoals = root.pendingSubgoals();
		if (subGoals.size() != 0) return subGoals.get(0);
		else return null;
	}

}
