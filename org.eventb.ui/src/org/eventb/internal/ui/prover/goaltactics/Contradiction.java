package org.eventb.internal.ui.prover.goaltactics;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.tactics.ITactic;
import org.eventb.core.seqprover.tactics.Tactics;
import org.eventb.ui.prover.IGoalTactic;

public class Contradiction implements IGoalTactic {

	public boolean isApplicable(IProofTreeNode node) {
		Predicate goal = node.getSequent().goal();
		if (goal.equals(Lib.False)) return false;
		Predicate negGoal = Lib.makeNeg(goal);
		if (negGoal.equals(Lib.True)) return false;
		if (Hypothesis.containsPredicate(
				node.getSequent().selectedHypotheses(),
				negGoal));
		return true;
	}

	public ITactic getTactic(IProofTreeNode node, String[] inputs) {
		return Tactics.contradictGoal();
	}

}
