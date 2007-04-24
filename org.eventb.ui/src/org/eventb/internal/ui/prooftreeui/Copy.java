package org.eventb.internal.ui.prooftreeui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eventb.core.seqprover.IProofTreeNode;

public class Copy implements IObjectActionDelegate {

	private ISelection selection;
	
	/**
	 * Constructor.
	 */
	public Copy() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// Do nothing
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		assert selection instanceof IStructuredSelection;
		IStructuredSelection ssel = (IStructuredSelection) selection;
		assert (ssel.size() == 1);
		assert (ssel.getFirstElement() instanceof IProofTreeNode);

		ProofTreeUI.buffer = ((IProofTreeNode) ssel.getFirstElement())
				.copyProofSkeleton();
		if (ProofTreeUIUtils.DEBUG)
			ProofTreeUIUtils.debug("Copied : " + ProofTreeUI.buffer);
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection sel) {
		this.selection = sel;
		assert selection instanceof IStructuredSelection;
		IStructuredSelection ssel = (IStructuredSelection) selection;
		assert ssel.size() == 1;
		assert ssel.getFirstElement() instanceof IProofTreeNode;
		IProofTreeNode node = (IProofTreeNode) ssel.getFirstElement();
		if (node.isOpen()) {
			action.setEnabled(false);
		} else {
			action.setEnabled(true);
		}
	}

}
