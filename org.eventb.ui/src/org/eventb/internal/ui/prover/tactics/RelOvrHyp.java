package org.eventb.internal.ui.prover.tactics;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider;

public class RelOvrHyp extends DefaultTacticProvider {

	@Override
	@Deprecated
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		// Do not need to pass the sequent
		return Tactics.relOvr(hyp, position);
	}

	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (node == null)
			return null;

		List<IPosition> positions = Tactics.relOvrGetPositions(hyp);

		if (positions.size() == 0)
			return null;
		return positions;
	}

	@Override
	public Point getOperatorPosition(Predicate predicate, String predStr,
			IPosition position) {
		Formula<?> right = predicate.getSubFormula(position);
		IPosition prevPosition = position.getPreviousSibling();
		Formula<?> left = predicate.getSubFormula(prevPosition);
		return getOperatorPosition(predStr,
				left.getSourceLocation().getEnd() + 1, right
						.getSourceLocation().getStart());
	}

}
