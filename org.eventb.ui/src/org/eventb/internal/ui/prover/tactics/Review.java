package org.eventb.internal.ui.prover.tactics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.IProofCommand;
import org.rodinp.core.RodinDBException;

public class Review implements IProofCommand {

	public void apply(IUserSupport us, Predicate hyp, String[] inputs,
			IProgressMonitor monitor) throws RodinDBException {
		us.applyTactic(Tactics.review(1), false, null);
	}

	public boolean isApplicable(IUserSupport us, Predicate hyp, String input) {
		IProofState currentPO = us.getCurrentPO();
		if (currentPO == null)
			return false;
		IProofTreeNode node = currentPO.getCurrentNode();
		return (node != null) && node.isOpen();
	}

}
