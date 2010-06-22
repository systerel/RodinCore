package org.eventb.internal.ui.prover.tactics;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticProvider;

public class InclusionSetMinusRightHyp extends DefaultTacticProvider implements
		ITacticProvider {

	@Override
	@Deprecated
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		return Tactics.inclusionSetMinusRightRewrites(hyp, position);
	}

	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (node != null) {
			List<IPosition> positions = Tactics
					.inclusionSetMinusRightRewritesGetPositions(hyp);
			if (positions.size() == 0)
				return null;
			return positions;
		}
		return null;
	}

	@Override
	public Point getOperatorPosition(Predicate predicate, String predStr,
			IPosition position) {
		Formula<?> subFormula = predicate.getSubFormula(position);
		Expression right = ((RelationalPredicate) subFormula).getRight();
		BinaryExpression bExp = (BinaryExpression) right;
		return getOperatorPosition(predStr, bExp.getLeft().getSourceLocation()
				.getEnd() + 1, bExp.getRight().getSourceLocation().getStart());
	}

}
