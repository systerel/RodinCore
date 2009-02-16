package org.eventb.contributer.seqprover.fr1942714;

import java.util.List;

import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticProvider;

public class RemoveMembershipHyp  extends DefaultTacticProvider implements
		ITacticProvider {

	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (node != null) {
			List<IPosition> positions = hyp.getPositions(new DefaultFilter() {

				@Override
				public boolean select(RelationalPredicate predicate) {
					if (predicate.getTag() == Predicate.IN) {
						Expression right = predicate.getRight();
						int rTag = right.getTag();
						if (rTag == Expression.CSET	)
								return true;
					}
					return super.select(predicate);
				}

			});
			if (positions.size() == 0)
				return null;
			return positions;
		}
		return null;
	}

	@Override
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs, String globalInput) {
		return BasicTactics.reasonerTac(new RemoveMembership(),
				new RemoveMembership.Input(hyp, position));
	}

}
