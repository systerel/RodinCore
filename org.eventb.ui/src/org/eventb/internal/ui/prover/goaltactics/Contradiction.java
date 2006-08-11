package org.eventb.internal.ui.prover.goaltactics;

import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.tactics.ITactic;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.ui.prover.IGoalTactic;

public class Contradiction implements IGoalTactic {

	public boolean isApplicable(IProofTreeNode node) {
		return true;
	}

	public ITactic getTactic(IProofTreeNode node, String[] inputs) {
		return Tactics.contradictGoal();
	}

}
