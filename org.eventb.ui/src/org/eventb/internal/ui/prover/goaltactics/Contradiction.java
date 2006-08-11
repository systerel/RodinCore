package org.eventb.internal.ui.prover.goaltactics;

import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.tactics.ITactic;
import org.eventb.core.prover.tactics.Tactics;
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
