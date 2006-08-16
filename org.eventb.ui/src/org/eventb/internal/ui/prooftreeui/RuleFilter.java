package org.eventb.internal.ui.prooftreeui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.rules.ReasoningStep;

public class RuleFilter extends ViewerFilter {

	String reasonerID;

	public RuleFilter(String reasonerID) {
		this.reasonerID = reasonerID;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		ProofTreeUI.debug("Filtered reasoner ID: " + reasonerID);
		// Do not filter the first element
		if (parentElement instanceof IProofTree) {
			return true;
		} else if (element instanceof IProofTreeNode) {
			IProofTreeNode node = (IProofTreeNode) element;

			if (node.isOpen())
				return true; // Do not filter open node
			else {
				IProofRule rule = node.getRule();
				ReasoningStep step = (ReasoningStep) rule;
				ProofTreeUI.debug("Rule " + node.getRule().getDisplayName());
				ProofTreeUI.debug("Reasoning Step: "
						+ step.reasonerOutput.generatedBy.getReasonerID());
				if (step.reasonerOutput.generatedBy.getReasonerID().equals(
						reasonerID))
					return false;
				else
					return true;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return reasonerID;
	}

}
