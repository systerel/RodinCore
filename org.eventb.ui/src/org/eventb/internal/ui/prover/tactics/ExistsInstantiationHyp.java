package org.eventb.internal.ui.prover.tactics;

import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.ITacticProvider;

public class ExistsInstantiationHyp implements ITacticProvider {

	public ITactic getTactic(IProofTreeNode node, Hypothesis hyp,
			String[] inputs) {
		return Tactics.exE(hyp);
	}

	public boolean isApplicable(IProofTreeNode node, Hypothesis hyp,
			String input) {
		return Tactics.exE_applicable(hyp);
	}

}
