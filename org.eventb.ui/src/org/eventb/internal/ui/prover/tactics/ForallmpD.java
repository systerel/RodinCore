package org.eventb.internal.ui.prover.tactics;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider;

public class ForallmpD extends DefaultTacticProvider {

	@Override
	@Deprecated
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		return Tactics.allmpD(hyp, inputs);
	}

	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (Tactics.allmpD_applicable(hyp)) {
			List<IPosition> positions = new ArrayList<IPosition>();
			positions.add(IPosition.ROOT);
			return positions;
		}
		return null;
	}

	@Override
	public Point getOperatorPosition(Predicate predicate, String predStr,
			IPosition position) {
		Formula<?> subFormula = predicate.getSubFormula(position);
		assert subFormula instanceof Predicate;
		Predicate pred = (Predicate) subFormula;
		assert Tactics.allD_applicable(pred);
		QuantifiedPredicate qPred = (QuantifiedPredicate) pred;
		BinaryPredicate impPred = (BinaryPredicate) qPred.getPredicate();
		
		Predicate left = impPred.getLeft();
		Predicate right = impPred.getRight();
		return getOperatorPosition(predStr, left.getSourceLocation()
				.getEnd() + 1, right.getSourceLocation().getStart());
	}

}
