package org.eventb.core.seqprover.tactics;

import org.eventb.core.seqprover.IProofTreeNode;

public interface ITactic {
	
	public abstract Object apply(IProofTreeNode pt);
	
}
