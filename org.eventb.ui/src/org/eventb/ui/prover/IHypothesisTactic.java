package org.eventb.ui.prover;

import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.tactics.ITactic;

public interface IHypothesisTactic {

	public ITactic getTactic(IProofTreeNode node, Hypothesis hyp, String [] inputs);
	
	public boolean isApplicable(IProofTreeNode node, Hypothesis hyp);
	
}
