package org.eventb.internal.ui.prooftreeui;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.ui.proofSkeletonView.ProofSkeletonView;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.RodinDBException;

public class Prune implements IObjectActionDelegate {

	private ISelection selection;
	
	private IUserSupport userSupport = null;
	
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
		if (targetPart instanceof ProofTreeUI) {
			this.userSupport = ((ProofTreeUIPage) ((ProofTreeUI) targetPart)
					.getCurrentPage()).getUserSupport();
		}
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		assert userSupport != null;
		assert selection instanceof IStructuredSelection;
		IStructuredSelection ssel = (IStructuredSelection) selection;
		assert (ssel.size() == 1);
		assert (ssel.getFirstElement() instanceof IProofTreeNode);

		try {
			userSupport.applyTactic(Tactics.prune(), false, new NullProgressMonitor());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection sel) {
		final IWorkbenchPart part = EventBUIPlugin.getActivePage().getActivePart();
		if (part instanceof ProofSkeletonView){
			action.setEnabled(false);
			return;
		}
		if (userSupport == null) {
			action.setEnabled(false);
			return;
		}
		this.selection = sel;
		assert selection instanceof IStructuredSelection;
		IStructuredSelection ssel = (IStructuredSelection) selection;
		if (ssel.size() != 1) {
			action.setEnabled(false);
			return;
		}
		if (!(ssel.getFirstElement() instanceof IProofTreeNode)) {
			action.setEnabled(false);
			return;			
		}
		IProofTreeNode node = (IProofTreeNode) ssel.getFirstElement();
		if (node.isOpen()) {
			action.setEnabled(false);
		} else {
			action.setEnabled(true);
		}
	}

}
