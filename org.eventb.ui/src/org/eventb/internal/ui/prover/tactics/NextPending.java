package org.eventb.internal.ui.prover.tactics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProofTreeNodeFilter;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.ui.prover.IProofCommand;
import org.rodinp.core.RodinDBException;

public class NextPending implements IProofCommand {

	public void apply(IUserSupport us, Predicate hyp, String[] inputs,
			IProgressMonitor monitor) throws RodinDBException {
		us.selectNextSubgoal(false, new IProofTreeNodeFilter() {

			public boolean select(IProofTreeNode node) {
				return node.isOpen();
			}
			
		});
	}

	public boolean isApplicable(IUserSupport us, Predicate hyp, String input) {
		IProofState currentPO = us.getCurrentPO();
		try {
			return currentPO != null && !(currentPO.isClosed());
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleGetAttributeException(e);
		} 
		return false;
	}

}
