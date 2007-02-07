package org.eventb.internal.ui.prover.tactics;

import java.util.List;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider;

public class DoubleImplHyp extends DefaultTacticProvider {

	private List<IPosition> positions = null;

	@Override
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		return Tactics.doubleImpHyp(hyp, position); // Second level
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

	private void internalGetPositions(Predicate hyp) {
		positions = hyp.getPositions(new DefaultFilter() {

			@Override
			public boolean select(BinaryPredicate predicate) {
				if (predicate.getTag() == Predicate.LIMP) {
					if (Lib.isImp(predicate.getRight()))
						return true;
				}
				return false;
			}
		});

	}

}
