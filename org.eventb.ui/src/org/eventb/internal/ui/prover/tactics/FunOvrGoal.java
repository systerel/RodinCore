package org.eventb.internal.ui.prover.tactics;

import java.util.List;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider;

public class FunOvrGoal extends DefaultTacticProvider {

	private List<IPosition> positions = null;

	@Override
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		// Do not need to pass the sequent
		return Tactics.funOvrGoal(position);
	}

	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (node == null)
			return null;

		internalGetPositions(node.getSequent().goal());
		if (positions.size() == 0)
			return null;
		return positions;
	}

	private void internalGetPositions(Predicate pred) {
		positions = pred.getPositions(new DefaultFilter() {

			@Override
			public boolean select(BinaryExpression expression) {
				if (Tactics.isFunOvrApp(expression))
					return true;
				return false;
			}
		});

	}

}
