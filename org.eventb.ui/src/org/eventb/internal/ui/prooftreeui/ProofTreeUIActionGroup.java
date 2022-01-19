/*******************************************************************************
 * Copyright (c) 2005, 2022 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Université de Lorraine - add next pending, next review and info actions
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import static org.eclipse.jface.dialogs.MessageDialog.openError;
import static org.eclipse.jface.window.Window.CANCEL;
import static org.eclipse.ui.ISharedImages.IMG_OBJ_ELEMENT;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_BACK;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_FORWARD;
import static org.eventb.internal.ui.EventBImage.getImageDescriptor;
import static org.eventb.ui.IEventBSharedImages.IMG_INFO_PROVER_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_NEXT_PENDING_PATH;
import static org.eventb.ui.IEventBSharedImages.IMG_NEXT_REVIEW_PATH;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.prover.tactics.Info;
import org.eventb.internal.ui.prover.tactics.NextPending;
import org.eventb.internal.ui.prover.tactics.NextReview;
import org.eventb.ui.prover.IProofCommand;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class provides the actions that are used in the Proof Tree UI
 *         View.
 */
public class ProofTreeUIActionGroup extends ActionGroup {

	private static final class FilterAction extends Action {

		private final ProofTreeUIPage proofTreeUI;

		FilterAction(ProofTreeUIPage proofTreeUI) {
			this.proofTreeUI = proofTreeUI;
		}

		@Override
		public void run() {
			final ProofTreeUIFiltersDialog dialog = new ProofTreeUIFiltersDialog(
					null, proofTreeUI);
			final int code = dialog.open();
			if (code == CANCEL) {
				return;
			}
			final Object[] results = dialog.getResult();
			if (results != null) {
				proofTreeUI.setFilters(results);
			}
			proofTreeUI.refresh();
		}
	}

	private static final class NextPrevPOAction extends Action {

		private final ProofTreeUIPage proofTreeUI;

		final boolean nextAction;

		NextPrevPOAction(ProofTreeUIPage proofTreeUI, boolean nextAction) {
			this.proofTreeUI = proofTreeUI;
			this.nextAction = nextAction;
		}

		@Override
		public void run() {
			final IUserSupport us = proofTreeUI.getUserSupport();
			final Shell shell = proofTreeUI.getControl().getShell();
			UIUtils.runWithProgressDialog(shell, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException {
					try {
						if (nextAction) {
							us.nextUndischargedPO(false, monitor);
						} else {
							us.prevUndischargedPO(false, monitor);
						}
					} catch (RodinDBException e) {
						throw new InvocationTargetException(e);
					}
				}
			});
		}
	}

	/**
	 * Action wrapper around {@link IProofCommand}.
	 *
	 * The action is enabled/disabled based on the proof command's
	 * {@code isApplicable()} and it runs the proof command's {@code apply()}. It is
	 * possible to force the {@code apply()} to run in the UI thread if needed.
	 *
	 * This only works with implementations of {@link IProofCommand} that accept
	 * {@code null} parameters for their hypothesis predicate and inputs.
	 */
	private static final class ProofCommandAction extends Action {
		private final IProofCommand command;

		private final ProofTreeUIPage proofTreeUI;

		private boolean forceInUIThread;

		public ProofCommandAction(IProofCommand command, ProofTreeUIPage proofTreeUI, boolean forceInUIThread) {
			this.command = command;
			this.proofTreeUI = proofTreeUI;
			this.forceInUIThread = forceInUIThread;
			updateEnabledState();
		}

		@Override
		public void run() {
			final IUserSupport us = proofTreeUI.getUserSupport();
			final Shell shell = proofTreeUI.getControl().getShell();
			UIUtils.runWithProgressDialog(shell, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException {
					if (forceInUIThread) {
						shell.getDisplay().syncExec(() -> {
							try {
								command.apply(us, null, null, monitor);
							} catch (RodinDBException e) {
								// We can't throw the exception through syncExec, so we just display it
								openError(shell, "Error", e.getLocalizedMessage());
							}
						});
					} else {
						try {
							command.apply(us, null, null, monitor);
						} catch (RodinDBException e) {
							throw new InvocationTargetException(e);
						}
					}
				}
			});
		}

		public void updateEnabledState() {
			setEnabled(command.isApplicable(proofTreeUI.getUserSupport(), null, null));
		}
	}

	// Different actions.
	protected Action prevPOAction;

	protected Action nextPOAction;

	protected ProofCommandAction infoAction;

	protected ProofCommandAction nextPendingAction;

	protected ProofCommandAction nextReviewAction;

	protected Action filterAction;

	protected DrillDownAdapter drillDownAdapter;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param proofTreeUI
	 *            the associated Proof Tree UI page.
	 */
	public ProofTreeUIActionGroup(ProofTreeUIPage proofTreeUI) {
		drillDownAdapter = new DrillDownAdapter(proofTreeUI.getViewer());

		filterAction = new FilterAction(proofTreeUI);
		filterAction.setText("Filter");
		filterAction.setToolTipText("Filter the rules");
		filterAction.setImageDescriptor(getSharedImageDesc(IMG_OBJ_ELEMENT));

		nextPOAction = new NextPrevPOAction(proofTreeUI, true);
		nextPOAction.setText("Next PO");
		nextPOAction.setToolTipText("Next Proof Obligation");
		nextPOAction.setImageDescriptor(getSharedImageDesc(IMG_TOOL_FORWARD));

		prevPOAction = new NextPrevPOAction(proofTreeUI, false);
		prevPOAction.setText("Previous PO");
		prevPOAction.setToolTipText("Previous Proof Obligation");
		prevPOAction.setImageDescriptor(getSharedImageDesc(IMG_TOOL_BACK));

		infoAction = new ProofCommandAction(new Info(), proofTreeUI, true);
		infoAction.setText("Proof Information");
		infoAction.setToolTipText("Show information related to this proof obligation");
		infoAction.setImageDescriptor(getImageDescriptor(IMG_INFO_PROVER_PATH));

		nextPendingAction = new ProofCommandAction(new NextPending(), proofTreeUI, false);
		nextPendingAction.setText("Next Pending Subgoal");
		nextPendingAction.setToolTipText("Select the next pending subgoal");
		nextPendingAction.setImageDescriptor(getImageDescriptor(IMG_NEXT_PENDING_PATH));

		nextReviewAction = new ProofCommandAction(new NextReview(), proofTreeUI, false);
		nextReviewAction.setText("Next Review Subgoal");
		nextReviewAction.setToolTipText("Select the next review subgoal");
		nextReviewAction.setImageDescriptor(getImageDescriptor(IMG_NEXT_REVIEW_PATH));
	}

	private static ImageDescriptor getSharedImageDesc(String symbolicName) {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				symbolicName);
	}

	/**
	 * Dynamically fill the context menu (depends on the selection).
	 * <p>
	 * 
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void fillContextMenu(IMenuManager menu) {
		ISelection sel = getContext().getSelection();
		if (sel instanceof IStructuredSelection) {
			// Other plug-ins can contribute their actions here
			menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			super.fillContextMenu(menu);
		}
	}

	/**
	 * Update actions to enable/disable them if something changed.
	 */
	public void updateEnabledState() {
		infoAction.updateEnabledState();
		nextPendingAction.updateEnabledState();
		nextReviewAction.updateEnabledState();
	}

}
