package org.eventb.ui.prover;

import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;

public interface IHypothesisTactic {

	public ITactic getTactic(IProofTreeNode node, Hypothesis hyp, String [] inputs);
	
	public boolean isApplicable(IProofTreeNode node, Hypothesis hyp);
	
}
