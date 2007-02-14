package org.eventb.internal.ui.prover.tactics;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider;

public class FunOvrHyp extends DefaultTacticProvider {

	private List<IPosition> positions = null;

	@Override
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		// Do not need to pass the sequent
		return Tactics.funOvrHyp(hyp, position);
	}

	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (node == null)
			return null;

		internalGetPositions(hyp);
		if (positions.size() == 0)
			return null;
		return positions;
	}

	private void internalGetPositions(final Predicate pred) {
		positions = pred.getPositions(new DefaultFilter() {

			@Override
			public boolean select(BinaryExpression expression) {
				if (Tactics.isFunOvrApp(expression))
					return true;
				return false;
			}
		});

		List<IPosition> toBeRemoved = new ArrayList<IPosition>();
		for (IPosition pos : positions) {
			if (!Tactics.isParentTopLevelPredicate(pred, pos)) {
				toBeRemoved.add(pos);
			}
		}

		positions.remove(toBeRemoved);
	}

	@Override
	public Point getOperatorPosition(Predicate predicate, String predStr, IPosition position) {
		assert Tactics.isFunOvrApp(predicate.getSubFormula(position));
		Formula subFormula = predicate.getSubFormula(position);
		Expression left = ((BinaryExpression) subFormula).getLeft();
		Expression[] children = ((AssociativeExpression) left)
				.getChildren();
		Expression last = children[children.length - 1];
		Expression secondLast = children[children.length - 2];
		return getOperatorPosition(predStr, secondLast.getSourceLocation().getEnd() + 1, last
					.getSourceLocation().getStart());
	}

}
