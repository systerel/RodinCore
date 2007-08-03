package org.eventb.internal.pp.ui;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.internal.pp.AutoTactics;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticProvider;

public class PPTacticProviderRestricted extends DefaultTacticProvider implements
		ITacticProvider {

	@Override
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs, String globalInput) {
		return new AutoTactics.PPrestricted();
	}

	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (node != null && node.isOpen())
			return new ArrayList<IPosition>();
		return null;
	}

}
