package org.eventb.internal.ui.prover.tactics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProofTreeNodeFilter;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.IProofCommand;
import org.rodinp.core.RodinDBException;

public class NextReview implements IProofCommand {

	public void apply(final IUserSupport us, Predicate hyp, String[] inputs,
			final IProgressMonitor monitor) throws RodinDBException {
		EventBPlugin.getDefault().getUserSupportManager().run(new Runnable() {

			public void run() {
				try {
					us.selectNextSubgoal(false, new IProofTreeNodeFilter() {

						public boolean select(IProofTreeNode node) {
							int confidence = node.getConfidence();
							return !node.isOpen() && !node.hasChildren()
									&& confidence <= IConfidence.REVIEWED_MAX
									&& IConfidence.PENDING <= confidence;
						}
						
					});
					us.applyTactic(Tactics.prune(), false, monitor);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		
	}

	public boolean isApplicable(IUserSupport us, Predicate hyp, String input) {
		IProofState currentPO = us.getCurrentPO();
		return (currentPO != null);
	}

}
