package org.eventb.internal.ui.prover.tactics;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider;

public class DomDistLeftHyp extends DefaultTacticProvider {

	@Override
	@Deprecated
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		return Tactics.domDistLeftRewrites(hyp, position);
	}

	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (node == null)
			return null;

		List<IPosition> positions = Tactics.domDistLeftGetPositions(hyp);
		if (positions.size() == 0) {
			return null;
		}
		return positions;
	}

	@Override
	public Point getOperatorPosition(Predicate predicate, String predStr,
			IPosition position) {
		Formula<?> subFormula = predicate.getSubFormula(position);
		Expression left = ((BinaryExpression) subFormula).getLeft();
		Expression[] children = ((AssociativeExpression) left).getChildren();
		Expression first = children[0];
		Expression second = children[1];
		return getOperatorPosition(predStr, first.getSourceLocation()
				.getEnd() + 1, second.getSourceLocation().getStart());
	}

}
