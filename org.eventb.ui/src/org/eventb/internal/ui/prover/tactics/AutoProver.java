package org.eventb.internal.ui.prover.tactics;

import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.ui.prover.ITacticProvider;

public class AutoProver implements ITacticProvider {

	public ITactic getTactic(IProofTreeNode node, Hypothesis hyp,
			String[] inputs) {
		return org.eventb.internal.core.pom.AutoProver.autoTactic();
	}

	public boolean isApplicable(IProofTreeNode node, Hypothesis hyp,
			String input) {
		return node != null && node.isOpen();
	}

}
