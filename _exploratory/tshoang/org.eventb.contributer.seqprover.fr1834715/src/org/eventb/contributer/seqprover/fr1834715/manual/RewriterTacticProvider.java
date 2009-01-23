package org.eventb.contributer.seqprover.fr1834715.manual;

import static org.eventb.core.ast.Formula.SUBSET;

import java.util.List;

import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticProvider;

public class RewriterTacticProvider extends DefaultTacticProvider implements
		ITacticProvider {

	private final class SubsetFilter extends DefaultFilter {
		@Override
		public boolean select(RelationalPredicate predicate) {
			return predicate.getTag() == SUBSET;
		}
	}

	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		final Predicate pred = hyp != null ? hyp : node.getSequent().goal();
		final List<IPosition> positions = pred.getPositions(new SubsetFilter());
		if (positions.size() == 0)
			return null;
		return positions;
	}

	@Override
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs, String globalInput) {
		return BasicTactics.reasonerTac(new ManualRewrites(),
				new ManualRewrites.Input(hyp, position));
	}

}
