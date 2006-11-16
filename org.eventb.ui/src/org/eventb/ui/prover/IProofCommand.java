package org.eventb.ui.prover;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.Hypothesis;
import org.rodinp.core.RodinDBException;

public interface IProofCommand {

	public boolean isApplicable(IUserSupport us, Hypothesis hyp, String input);

	public void apply(IUserSupport us, Hypothesis hyp, String[] inputs,
			IProgressMonitor monitor) throws RodinDBException;

}
