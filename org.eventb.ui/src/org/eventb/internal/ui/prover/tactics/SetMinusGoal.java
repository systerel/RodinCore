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

public class SetMinusGoal extends DefaultTacticProvider {

	@Override
	@Deprecated
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		return Tactics.setMinusRewrites(null, position);
	}

	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (node == null)
			return null;
		Predicate goal = node.getSequent().goal();
		List<IPosition> positions = Tactics.setMinusGetPositions(goal);
		if (positions.size() == 0) {
			return null;
		}
		return positions;
	}

	@Override
	public Point getOperatorPosition(Predicate predicate, String predStr,
			IPosition position) {
		Formula<?> subFormula = predicate.getSubFormula(position);
		Expression right = ((BinaryExpression) subFormula).getRight();
		if (right instanceof AssociativeExpression) {
			Expression[] children = ((AssociativeExpression) right)
					.getChildren();
			Expression first = children[0];
			Expression second = children[1];
			return getOperatorPosition(predStr, first.getSourceLocation()
					.getEnd() + 1, second.getSourceLocation().getStart());
		}
		else {
			BinaryExpression bExp = (BinaryExpression) right;
			return getOperatorPosition(predStr, bExp.getLeft()
					.getSourceLocation().getEnd() + 1, bExp.getRight()
					.getSourceLocation().getStart());
		}
	}

}
