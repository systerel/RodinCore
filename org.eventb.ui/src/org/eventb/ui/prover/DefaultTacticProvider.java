package org.eventb.ui.prover;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;

public class DefaultTacticProvider implements ITacticProvider {

	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (isApplicable(node, hyp, input))
			return new ArrayList<IPosition>();
		return null;
	}

	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		return null;
	}

	/**
	 * @deprecated Use
	 *             {@link #getApplicablePositions(IProofTreeNode,Predicate,String)}
	 *             instead
	 */
	@Deprecated
	public boolean isApplicable(IProofTreeNode node, Predicate hyp, String input) {
		return false;
	}

}
