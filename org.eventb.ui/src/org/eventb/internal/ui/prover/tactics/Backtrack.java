package org.eventb.internal.ui.prover.tactics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.ProofState;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.ui.prover.IProofCommand;
import org.rodinp.core.RodinDBException;

public class Backtrack implements IProofCommand {

	public void apply(IUserSupport us, Hypothesis hyp, String[] inputs,
			IProgressMonitor monitor) throws RodinDBException {
		us.back(monitor);
	}

	public boolean isApplicable(IUserSupport us, Hypothesis hyp, String input) {
		ProofState currentPO = us.getCurrentPO();
		if (currentPO == null)
			return false;
		IProofTreeNode node = currentPO.getCurrentNode();
		return (node != null) && (node.isOpen()) && (node.getParent() != null);
	}

}
