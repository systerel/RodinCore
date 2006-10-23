package org.eventb.internal.ui.prover.goaltactics;

import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.IGoalTactic;

public class Contradiction implements IGoalTactic {

	public boolean isApplicable(IProofTreeNode node) {
		return Tactics.contradictGoal_applicable(node);
	}

	public ITactic getTactic(IProofTreeNode node, String[] inputs) {
		return Tactics.contradictGoal();
	}

}
