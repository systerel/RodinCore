package org.eventb.internal.ui.prover.goaltactics;

import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.tactics.ITactic;
import org.eventb.core.seqprover.tactics.Tactics;
import org.eventb.ui.prover.IGoalTactic;

public class Negation implements IGoalTactic {

	public boolean isApplicable(IProofTreeNode node) {
		return (node != null && Tactics.removeNegGoal_applicable(node
				.getSequent().goal()));
	}

	public ITactic getTactic(IProofTreeNode node, String[] inputs) {
		return Tactics.removeNegGoal();
	}

}
