package org.eventb.ui.prover;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;

public interface ITacticProvider {

	public boolean isApplicable(IProofTreeNode node, Predicate hyp, String input);
	
	public ITactic getTactic(IProofTreeNode node, Predicate hyp, String [] inputs);
	
}
