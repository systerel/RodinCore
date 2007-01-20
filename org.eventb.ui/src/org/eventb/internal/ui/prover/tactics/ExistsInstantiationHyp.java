package org.eventb.internal.ui.prover.tactics;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.ITacticProvider;

public class ExistsInstantiationHyp implements ITacticProvider {

	List<IPosition> positions;

	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		return Tactics.exE(hyp);
	}

	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (Tactics.exE_applicable(hyp)) {
			internalGetPositions(node, hyp);
			return positions;
		}
		return null;
	}

	public void internalGetPositions(IProofTreeNode node, Predicate hyp) {
		positions = new ArrayList<IPosition>();
		positions.add(hyp.getPosition(hyp.getSourceLocation()));
	}
	
}
