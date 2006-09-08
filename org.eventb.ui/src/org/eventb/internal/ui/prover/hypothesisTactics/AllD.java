package org.eventb.internal.ui.prover.hypothesisTactics;

import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.tactics.ITactic;
import org.eventb.core.seqprover.tactics.Tactics;
import org.eventb.ui.prover.IHypothesisTactic;

public class AllD implements IHypothesisTactic {

	public ITactic getTactic(IProofTreeNode node, Hypothesis hyp, String[] inputs) {
		return Tactics.allD(hyp, inputs);
	}

	public boolean isApplicable(IProofTreeNode node, Hypothesis hyp) {
		return Tactics.allD_applicable(hyp);
	}
	
}
