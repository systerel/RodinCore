package org.eventb.ui.prover;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.rodinp.core.RodinDBException;

public interface IProofCommand {

	public boolean isApplicable(IUserSupport us, Predicate hyp, String input);

	public void apply(IUserSupport us, Predicate hyp, String[] inputs,
			IProgressMonitor monitor) throws RodinDBException;

}
