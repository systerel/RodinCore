package org.eventb.core.pm;

import java.util.Collection;
import java.util.HashSet;

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
	
}
