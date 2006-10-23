package org.eventb.internal.ui.prover.hypothesisTactics;

import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.IHypothesisTactic;

public class Falsify implements IHypothesisTactic {

	public ITactic getTactic(IProofTreeNode node, Hypothesis hyp,
			String[] inputs) {
		return Tactics.falsifyHyp(hyp);
	}

	public boolean isApplicable(IProofTreeNode node, Hypothesis hyp) {
		return Tactics.falsifyHyp_applicable(hyp,node.getSequent());
	}

}
