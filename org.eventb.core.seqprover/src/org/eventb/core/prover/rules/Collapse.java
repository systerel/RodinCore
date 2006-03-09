package org.eventb.core.prover.rules;

import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.sequent.IProverSequent;

public final class Collapse extends ProofRule {

	IProofTree proofTree;
	
	public Collapse(IProofTree proofTree, String name) {
		super("{"+name+"}");
		this.proofTree = proofTree;
	}
	
	public IProverSequent[] apply(IProverSequent S) {
		
		if (!(Lib.identical(S,proofTree.getSequent())))
			return null;
		IProofTreeNode[] openDescendants = proofTree.getRoot().getOpenDescendants();
		IProverSequent[] anticidents = new IProverSequent[openDescendants.length];
		for (int i = 0; i < openDescendants.length; i++) {
			anticidents[i] = openDescendants[i].getSequent();
		}
		return anticidents;
		
		
//		IProofTree pt = SequentProver.makeProofTree(S);
//		Object tacticOutput = this.tactic.apply(pt.getRoot());
//		if (tacticOutput != null) return null;
//		IProofTreeNode[] openDescendants = pt.getRoot().getOpenDescendants();
//		IProverSequent[] anticidents = new IProverSequent[openDescendants.length];
//		for (int i = 0; i < openDescendants.length; i++) {
//			anticidents[i] = openDescendants[i].getSequent();
//		}
//		return anticidents;
	}

}
