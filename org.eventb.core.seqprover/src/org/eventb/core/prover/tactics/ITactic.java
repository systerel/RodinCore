package org.eventb.core.prover.tactics;

import org.eventb.core.prover.IProofTreeNode;

public interface ITactic {
	
	public abstract Object apply(IProofTreeNode pt);
	
}
