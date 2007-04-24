package org.eventb.internal.ui.prooftreeui;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.rodinp.core.RodinDBException;

public class Prune implements IObjectActionDelegate {

	private ISelection selection;
	
	private ProofTreeUI proofTreeUI;
	
	/**
	 * Constructor.
	 */
	public Prune() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		assert targetPart instanceof ProofTreeUI;
		this.proofTreeUI = (ProofTreeUI) targetPart;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		assert selection instanceof IStructuredSelection;
		IStructuredSelection ssel = (IStructuredSelection) selection;
		assert (ssel.size() == 1);
		assert (ssel.getFirstElement() instanceof IProofTreeNode);

		try {
			((ProofTreeUIPage) proofTreeUI.getCurrentPage()).getUserSupport()
					.applyTactic(Tactics.prune(), new NullProgressMonitor());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
