package org.eventb.internal.ui.prover.goaltactics;

import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.IGoalTactic;

public class Conjunction implements IGoalTactic {

	public boolean isApplicable(IProofTreeNode node) {
		return (node != null && Tactics.conjI_applicable(node.getSequent()
				.goal()));
	}

	public ITactic getTactic(IProofTreeNode node, String[] inputs) {
		return Tactics.conjI();
	}

}
