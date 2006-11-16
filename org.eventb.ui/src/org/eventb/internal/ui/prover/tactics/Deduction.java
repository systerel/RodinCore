package org.eventb.internal.ui.prover.tactics;

import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.ITacticProvider;

public class Deduction implements ITacticProvider {

	public ITactic getTactic(IProofTreeNode node, Hypothesis hyp,
			String[] inputs) {
		return Tactics.impI();
	}

	public boolean isApplicable(IProofTreeNode node, Hypothesis hyp,
			String input) {
		return (node != null && Tactics.impI_applicable(node.getSequent()
				.goal()));
	}

}
