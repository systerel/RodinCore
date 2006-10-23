package org.eventb.core.seqprover;


public interface ITactic {
	
	public abstract Object apply(IProofTreeNode pt, IProofMonitor pm);
	
}
