package org.eventb.internal.ui.prover.tactics;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.ITacticProvider;

public class ConjunctionElemination implements ITacticProvider {

	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			String[] inputs) {
		return Tactics.conjD(hyp);
	}

	public boolean isApplicable(IProofTreeNode node, Predicate hyp,
			String input) {
		return Tactics.conjD_applicable(hyp);
	}

}
