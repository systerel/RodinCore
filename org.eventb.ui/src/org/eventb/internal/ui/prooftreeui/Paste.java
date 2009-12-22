package org.eventb.internal.ui.prooftreeui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.ui.proofSkeletonView.ProofSkeletonView;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.ui.EventBUIPlugin;

public class Paste implements IObjectActionDelegate {

	private ISelection selection;
	
	private IUserSupport userSupport = null;
	
	private Shell shell = null;

	/**
	 * Constructor.
	 */
	public Paste() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		if (targetPart instanceof ProofTreeUI) {
			this.userSupport = ((ProofTreeUIPage) ((ProofTreeUI) targetPart)
					.getCurrentPage()).getUserSupport();
			this.shell = targetPart.getSite().getShell();
		}
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		assert userSupport != null;
		assert selection instanceof IStructuredSelection;
		IStructuredSelection ssel = (IStructuredSelection) selection;
		assert ssel.size() == 1;
		assert ssel.getFirstElement() instanceof IProofTreeNode;
		assert ProofTreeUI.buffer instanceof IProofSkeleton;

		final IProofSkeleton copyNode = (IProofSkeleton) ProofTreeUI.buffer;
		ITactic pasteTactic = BasicTactics.rebuildTac(copyNode);
		ProverUIUtils.applyTacticWithProgress(shell, userSupport, pasteTactic, true);
		if (ProofTreeUIUtils.DEBUG)
			ProofTreeUIUtils.debug("Paste: " + copyNode);
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
			if (ProofTreeUIUtils.DEBUG) {
				ProofTreeUIUtils
						.debug("There should be exactly one selected element, disable Paste");
			}
			return;
		}
		if (!(ssel.getFirstElement() instanceof IProofTreeNode)) {
			action.setEnabled(false);
			if (ProofTreeUIUtils.DEBUG) {
				ProofTreeUIUtils
						.debug("The selected element should be a IProofTreeNode, disable Paste");
			}
			return;
		}
		
		IProofTreeNode node = (IProofTreeNode) ssel.getFirstElement();
		if (!node.isOpen() || ProofTreeUI.buffer == null || !(ProofTreeUI.buffer instanceof IProofSkeleton)) {
			if (ProofTreeUIUtils.DEBUG) {
				ProofTreeUIUtils
						.debug("The proof tree node is not open or the buffer is empty or the buffer is not a proof skeleton, disable Paste");
			}
			action.setEnabled(false);
		} else {
			if (ProofTreeUIUtils.DEBUG) {
				ProofTreeUIUtils
						.debug("Enable Paste");
			}
			action.setEnabled(true);
		}
	}

}
