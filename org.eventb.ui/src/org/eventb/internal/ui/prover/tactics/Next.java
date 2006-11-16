package org.eventb.internal.ui.prover.tactics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.ui.prover.IProofCommand;
import org.rodinp.core.RodinDBException;

public class Next implements IProofCommand {

	public void apply(UserSupport us, Hypothesis hyp, String[] inputs,
			IProgressMonitor monitor) throws RodinDBException {
		us.nextUndischargedPO(false, monitor);
	}

	public boolean isApplicable(UserSupport us, Hypothesis hyp, String input) {
		return true;
	}

}
