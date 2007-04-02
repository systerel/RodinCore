package org.eventb.internal.ui.prover.tactics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.proofinformation.ProofInformation;
import org.eventb.ui.prover.IProofCommand;
import org.rodinp.core.RodinDBException;

public class Info implements IProofCommand {

	public void apply(IUserSupport us, Predicate hyp, String[] inputs,
			IProgressMonitor monitor) throws RodinDBException {
		// Show the Proof Information View
		UIUtils.showView(ProofInformation.VIEW_ID);
	}

	public boolean isApplicable(IUserSupport us, Predicate hyp, String input) {
		return (us.getCurrentPO() != null);
	}

}
