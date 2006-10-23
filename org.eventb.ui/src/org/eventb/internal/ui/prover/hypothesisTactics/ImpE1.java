package org.eventb.internal.ui.prover.hypothesisTactics;

import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.IHypothesisTactic;

public class ImpE1 implements IHypothesisTactic {

	public ITactic getTactic(IProofTreeNode node, Hypothesis hyp, String[] inputs) {
		return Tactics.impE(hyp, false);
				
	}

	public boolean isApplicable(IProofTreeNode node, Hypothesis hyp) {
		return Tactics.impE_applicable(hyp);
	}

}
