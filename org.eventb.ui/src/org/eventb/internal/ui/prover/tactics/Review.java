package org.eventb.internal.ui.prover.tactics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.pm.ProofState;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.IProofCommand;
import org.rodinp.core.RodinDBException;

public class Review implements IProofCommand {

	public void apply(UserSupport us, Hypothesis hyp, String[] inputs,
			IProgressMonitor monitor) throws RodinDBException {
		us.applyTactic(Tactics.review(1), null);
	}

	public boolean isApplicable(UserSupport us, Hypothesis hyp, String input) {
		ProofState currentPO = us.getCurrentPO();
		if (currentPO == null)
			return false;
		IProofTreeNode node = currentPO.getCurrentNode();
		return (node != null) && node.isOpen();
	}

}
