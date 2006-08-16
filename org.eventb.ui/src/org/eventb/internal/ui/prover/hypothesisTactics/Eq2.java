package org.eventb.internal.ui.prover.hypothesisTactics;

import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.core.seqprover.tactics.ITactic;
import org.eventb.core.seqprover.tactics.Tactics;
import org.eventb.ui.prover.IHypothesisTactic;

public class Eq2 implements IHypothesisTactic {

	public ITactic getTactic(IProofTreeNode node, Hypothesis hyp, String[] inputs) {
		return Tactics.eqE(hyp, true);
	}

	public boolean isApplicable(IProofTreeNode node, Hypothesis hyp) {
		return Tactics.eqE_applicable(hyp);
	}
	

}
