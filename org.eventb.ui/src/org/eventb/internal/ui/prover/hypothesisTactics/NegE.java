package org.eventb.internal.ui.prover.hypothesisTactics;

import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.core.seqprover.tactics.ITactic;
import org.eventb.core.seqprover.tactics.Tactics;
import org.eventb.ui.prover.IHypothesisTactic;

public class NegE implements IHypothesisTactic {

	public ITactic getTactic(IProofTreeNode node, Hypothesis hyp, String[] inputs) {
		return Tactics.removeNegHyp(hyp);
	}

	public boolean isApplicable(IProofTreeNode node, Hypothesis hyp) {
		return Tactics.removeNegHyp_applicable(hyp);
	}
	

}
