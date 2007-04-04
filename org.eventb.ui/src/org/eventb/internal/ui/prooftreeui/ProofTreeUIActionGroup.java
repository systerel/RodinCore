/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.prooftreeui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eventb.core.pm.IProofState;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class provides the actions that are used in the Proof Tree UI
 *         View.
 */
public class ProofTreeUIActionGroup extends ActionGroup {

	// The associated Proof Tree UI page.
	final ProofTreeUIPage proofTreeUI;

	// Different actions.
	private Action copy;

	private Action paste;

	protected Action prevPOAction;

	protected Action nextPOAction;

	protected Action pruneAction;

	protected Action normAction;

	protected Action conjIAction;

	protected Action hypAction;

	protected Action allIAction;

	protected Action impIAction;

	protected Action trivialAction;

	protected Action filterAction;

	protected DrillDownAdapter drillDownAdapter;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param proofTreeUI
	 *            the associated Proof Tree UI page.
	 */
	public ProofTreeUIActionGroup(final ProofTreeUIPage proofTreeUI) {
		this.proofTreeUI = proofTreeUI;
		drillDownAdapter = new DrillDownAdapter(proofTreeUI.getViewer());

		copy = new Action() {
			@Override
			public void run() {
				ISelection sel = ProofTreeUIActionGroup.this.proofTreeUI
						.getSelection();
				if (sel instanceof IStructuredSelection) {
					IStructuredSelection ssel = (IStructuredSelection) sel;
					if (ssel.size() == 1
							&& ssel.getFirstElement() instanceof IProofTreeNode) {
						ProofTreeUI.buffer = ((IProofTreeNode) ssel
								.getFirstElement()).copyProofSkeleton();
						if (ProofTreeUIUtils.DEBUG)
							ProofTreeUIUtils.debug("Copied : "
									+ ProofTreeUI.buffer);
					}
				}
			}
		};
		copy.setText("&Copy");
		copy.setToolTipText("Copy the proof tree");
		copy.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT));

		paste = new Action() {
			@Override
			public void run() {
				ISelection sel = ProofTreeUIActionGroup.this.proofTreeUI
						.getSelection();
				if (sel instanceof IStructuredSelection) {
					IStructuredSelection ssel = (IStructuredSelection) sel;
					if (ssel.size() == 1
							&& ssel.getFirstElement() instanceof IProofTreeNode) {

						if (ProofTreeUI.buffer instanceof IProofSkeleton) {
							final IProofSkeleton copyNode = (IProofSkeleton) ProofTreeUI.buffer;
							ITactic pasteTactic = BasicTactics.rebuildTac(copyNode);
							ProverUIUtils.applyTacticWithProgress(proofTreeUI
									.getControl().getShell(), proofTreeUI
									.getUserSupport(), pasteTactic);
							if (ProofTreeUIUtils.DEBUG)
								ProofTreeUIUtils.debug("Paste: " + copyNode);
						}
					}
				}
			}
		};
		paste.setText("&Paste");
		paste.setToolTipText("Paste the proof tree");
		paste.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT));

		filterAction = new Action() {
			@Override
			public void run() {
				ProofTreeUIFiltersDialog dialog = new ProofTreeUIFiltersDialog(
						null, ProofTreeUIActionGroup.this.proofTreeUI);
				dialog.open();
				Object[] results = dialog.getResult();
				if (results != null) {
					ProofTreeUIActionGroup.this.proofTreeUI.setFilters(results);
				}

				ProofTreeUIActionGroup.this.proofTreeUI.refresh();
			}
		};
		filterAction.setText("Filter");
		filterAction.setToolTipText("Filter the rules");
		filterAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_OBJ_ELEMENT));

		nextPOAction = new Action() {
			@Override
			public void run() {
				UIUtils.runWithProgressDialog(
						ProofTreeUIActionGroup.this.proofTreeUI.getControl()
								.getShell(), new IRunnableWithProgress() {

							public void run(IProgressMonitor monitor)
									throws InvocationTargetException,
									InterruptedException {

								try {
									ProofTreeUIActionGroup.this.proofTreeUI
											.getUserSupport()
											.nextUndischargedPO(false, monitor);
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
							}
				});
			}
		};
		nextPOAction.setText("Next PO");
		nextPOAction.setToolTipText("Next Proof Obligation");
		nextPOAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_TOOL_FORWARD));

		prevPOAction = new Action() {
			@Override
			public void run() {
				UIUtils.runWithProgressDialog(
						ProofTreeUIActionGroup.this.proofTreeUI.getControl()
								.getShell(), new IRunnableWithProgress() {

							public void run(IProgressMonitor monitor)
									throws InvocationTargetException,
									InterruptedException {

								try {
									ProofTreeUIActionGroup.this.proofTreeUI
											.getUserSupport()
											.prevUndischargedPO(false, monitor);
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
							}
				});
			}
		};
		prevPOAction.setText("Previous PO");
		prevPOAction.setToolTipText("Previous Proof Obligation");
		prevPOAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_TOOL_BACK));

		pruneAction = new Action() {
			@Override
			public void run() {
				TreeViewer viewer = ProofTreeUIActionGroup.this.proofTreeUI
						.getViewer();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (obj instanceof IProofTreeNode) {
					IProofTreeNode proofTree = (IProofTreeNode) obj;
					if (!proofTree.isOpen()) {
						Tactics.prune().apply(proofTree, null);
						viewer.refresh(proofTree);
						viewer.setSelection(new StructuredSelection(proofTree));
					}
				}
			}
		};
		pruneAction.setText("Prune");
		pruneAction.setToolTipText("Prune the proof tree at this current node");
		pruneAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_OBJS_INFO_TSK));

		normAction = new Action() {
			@Override
			public void run() {
				TreeViewer viewer = ProofTreeUIActionGroup.this.proofTreeUI
						.getViewer();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (obj instanceof IProofTreeNode) {
					IProofTreeNode proofTree = (IProofTreeNode) obj;
					if (!proofTree.isClosed()) {
						Tactics.norm().apply(proofTree, null);
						viewer.refresh(proofTree);
						// Expand the node
						viewer.expandToLevel(proofTree,
								AbstractTreeViewer.ALL_LEVELS);
						// Select the first pending "subgoal"
						IProofTreeNode subGoal = proofTree
								.getFirstOpenDescendant();
						if (subGoal != null) {
							viewer
									.setSelection(new StructuredSelection(
											subGoal));
						}
					}
				}
			}
		};
		normAction.setText("Normalisation");
		normAction
				.setToolTipText("Applying some common (normalisation) rules at this node");
		normAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_OBJS_INFO_TSK));

		conjIAction = new Action() {
			@Override
			public void run() {
				TreeViewer viewer = ProofTreeUIActionGroup.this.proofTreeUI
						.getViewer();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (obj instanceof IProofTreeNode) {
					IProofTreeNode proofTree = (IProofTreeNode) obj;
					if (!proofTree.isClosed()) {
						Tactics.conjI().apply(proofTree, null);
						viewer.refresh(proofTree);
						// Expand the node
						viewer.expandToLevel(proofTree,
								AbstractTreeViewer.ALL_LEVELS);

						// Select the first pending "subgoal"
						IProofTreeNode subGoal = proofTree
								.getFirstOpenDescendant();
						if (subGoal != null) {
							viewer
									.setSelection(new StructuredSelection(
											subGoal));
						}
					}
				}
			}
		};
		conjIAction.setText("Conj");
		conjIAction
				.setToolTipText("Applying conjI tactic to the current sequent");
		conjIAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_OBJS_INFO_TSK));

		hypAction = new Action() {
			@Override
			public void run() {
				TreeViewer viewer = ProofTreeUIActionGroup.this.proofTreeUI
						.getViewer();

				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (obj instanceof IProofTreeNode) {
					IProofTreeNode proofTree = (IProofTreeNode) obj;
					if (!proofTree.isClosed()) {
						Tactics.hyp().apply(proofTree, null);
						ProofTreeUIActionGroup.this.proofTreeUI
								.refresh(proofTree);
						// Expand the node
						viewer.expandToLevel(proofTree,
								AbstractTreeViewer.ALL_LEVELS);

						IProofState ps = ProofTreeUIActionGroup.this.proofTreeUI
								.getUserSupport().getCurrentPO();
						// Select the next pending "subgoal"
						IProofTreeNode pt = ps.getNextPendingSubgoal(proofTree);
						if (pt != null)
							ProofTreeUIActionGroup.this.proofTreeUI.getViewer()
									.setSelection(new StructuredSelection(pt));
					}
				}
			}
		};
		hypAction.setText("Hypothesis");
		hypAction.setToolTipText("Applying hyp tactic to the current sequent");
		hypAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_OBJS_INFO_TSK));

		allIAction = new Action() {
			@Override
			public void run() {
				TreeViewer viewer = ProofTreeUIActionGroup.this.proofTreeUI
						.getViewer();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (obj instanceof IProofTreeNode) {
					IProofTreeNode proofTree = (IProofTreeNode) obj;
					if (!proofTree.isClosed()) {
						Tactics.allI().apply(proofTree, null);
						viewer.refresh(proofTree);
						// Expand the node
						viewer.expandToLevel(proofTree,
								AbstractTreeViewer.ALL_LEVELS);

						// Select the first pending "subgoal"
						IProofTreeNode subGoal = proofTree
								.getFirstOpenDescendant();
						if (subGoal != null) {
							viewer
									.setSelection(new StructuredSelection(
											subGoal));
						}
					}
				}
			}
		};
		allIAction.setText("AllI");
		allIAction
				.setToolTipText("Applying allI tactic to the current sequent");
		allIAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_OBJS_INFO_TSK));

		impIAction = new Action() {
			@Override
			public void run() {
				TreeViewer viewer = ProofTreeUIActionGroup.this.proofTreeUI
						.getViewer();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (obj instanceof IProofTreeNode) {
					IProofTreeNode proofTree = (IProofTreeNode) obj;
					if (!proofTree.isClosed()) {
						Tactics.impI().apply(proofTree, null);
						viewer.refresh(proofTree);
						// Expand the node
						viewer.expandToLevel(proofTree,
								AbstractTreeViewer.ALL_LEVELS);

						// Select the first pending "subgoal"
						IProofTreeNode subGoal = proofTree
								.getFirstOpenDescendant();
						if (subGoal != null) {
							viewer
									.setSelection(new StructuredSelection(
											subGoal));
						}
					}
				}
			}
		};
		impIAction.setText("impI");
		impIAction
				.setToolTipText("Applying impI tactic to the current sequent");
		impIAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_OBJS_INFO_TSK));

		trivialAction = new Action() {
			@Override
			public void run() {
				TreeViewer viewer = ProofTreeUIActionGroup.this.proofTreeUI
						.getViewer();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (obj instanceof IProofTreeNode) {
					IProofTreeNode proofTree = (IProofTreeNode) obj;
					if (!proofTree.isClosed()) {
						Tactics.trivial().apply(proofTree, null);
						viewer.refresh(proofTree);
						// Expand the node
						viewer.expandToLevel(proofTree,
								AbstractTreeViewer.ALL_LEVELS);

						// Select the first pending "subgoal"
						IProofTreeNode subGoal = proofTree
								.getFirstOpenDescendant();
						if (subGoal != null) {
							viewer
									.setSelection(new StructuredSelection(
											subGoal));
						}
					}
				}
			}
		};
		trivialAction.setText("trivial");
		trivialAction
				.setToolTipText("Applying trivial tactic to the current sequent");
		trivialAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_OBJS_INFO_TSK));

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
			IStructuredSelection ssel = (IStructuredSelection) sel;
			if (ssel.size() == 1) {
				IProofTreeNode pt = (IProofTreeNode) ssel.getFirstElement();

				if (!pt.isOpen()) {
					menu.add(copy);
					menu.add(new Separator());
					menu.add(pruneAction);
				} else {
					if (ProofTreeUI.buffer != null)
						menu.add(paste);
					menu.add(new Separator());
				}
			} else {
				menu.add(pruneAction);
			}
			// Other plug-ins can contribute there actions here
			menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		}
	}

}
