package org.eventb.internal.ui.prover;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
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
import org.eventb.core.pm.ProofState;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.internal.ui.EventBImage;

public class ProofTreeUIActionGroup 
	extends ActionGroup 
{
	private ProofTreeUIPage proofTreeUI;
	
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

	public ProofTreeUIActionGroup(ProofTreeUIPage proofTreeUI) {
		this.proofTreeUI = proofTreeUI;
		drillDownAdapter = new DrillDownAdapter(proofTreeUI.getViewer());

		filterAction = new Action() {
			public void run() {
				ProofTreeUIFiltersDialog dialog = new ProofTreeUIFiltersDialog(null, ProofTreeUIActionGroup.this.proofTreeUI);
				dialog.open();
				Object [] results = dialog.getResult();
				if (results != null) ProofTreeUIActionGroup.this.proofTreeUI.setFilters(results);
				
				ProofTreeUIActionGroup.this.proofTreeUI.refresh();
				ProofTreeUIActionGroup.this.proofTreeUI.getViewer().expandAll();
			}
		};
		filterAction.setText("Filter");
		filterAction.setToolTipText("Filter the rules");
		filterAction.setImageDescriptor(EventBImage.getImageDescriptor(EventBImage.IMG_NEXTPO_PATH));

		nextPOAction = new Action() {
			public void run() {
				ProofState ps = ProofTreeUIActionGroup.this.proofTreeUI.getUserSupport().nextPO();
				if (ps != null) {
					ProofTreeUIActionGroup.this.proofTreeUI.setInput(ps.getProofTree());
					ProofTreeUIActionGroup.this.proofTreeUI.getViewer().expandAll();
					IProofTreeNode pt = ps.getNextPendingSubgoal();
					if (pt != null) 
						ProofTreeUIActionGroup.this.proofTreeUI.getViewer().setSelection(new StructuredSelection(pt));
				}
			}
		};
		nextPOAction.setText("Next PO");
		nextPOAction.setToolTipText("Next Proof Obligation");
		nextPOAction.setImageDescriptor(EventBImage.getImageDescriptor(EventBImage.IMG_NEXTPO_PATH));

		prevPOAction = new Action() {
			public void run() {
				ProofState ps = ProofTreeUIActionGroup.this.proofTreeUI.getUserSupport().prevPO();
				if (ps != null) {
					ProofTreeUIActionGroup.this.proofTreeUI.setInput(ps.getProofTree());
					ProofTreeUIActionGroup.this.proofTreeUI.getViewer().expandAll();
					IProofTreeNode pt = ps.getNextPendingSubgoal();
					if (pt != null) 
						ProofTreeUIActionGroup.this.proofTreeUI.getViewer().setSelection(new StructuredSelection(pt));
				}
			}
		};
		prevPOAction.setText("Previous PO");
		prevPOAction.setToolTipText("Previous Proof Obligation");
		prevPOAction.setImageDescriptor(EventBImage.getImageDescriptor(EventBImage.IMG_PREVPO_PATH));

		pruneAction = new Action() {
			public void run() {
				TreeViewer viewer = ProofTreeUIActionGroup.this.proofTreeUI.getViewer();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				
				if (obj instanceof IProofTreeNode) {
					IProofTreeNode proofTree = (IProofTreeNode) obj;
					if (!proofTree.isOpen()) {
						Tactics.prune().apply(proofTree);
						viewer.refresh(proofTree);
						viewer.setSelection(new StructuredSelection(proofTree));
					}
				}
			}
		};
		pruneAction.setText("Prune");
		pruneAction.setToolTipText("Prune the proof tree at this current node");
		pruneAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		normAction = new Action() {
			public void run() {
				TreeViewer viewer = ProofTreeUIActionGroup.this.proofTreeUI.getViewer();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();

				if (obj instanceof IProofTreeNode) {
					IProofTreeNode proofTree = (IProofTreeNode) obj;
					if (!proofTree.isDischarged()) {
						Tactics.norm().apply(proofTree);
						viewer.refresh(proofTree);
						// Expand the node
						viewer.expandToLevel(proofTree, AbstractTreeViewer.ALL_LEVELS);
						//viewer.setExpandedState(proofTree, true);
						
						// Select the first pending "subgoal"
						IProofTreeNode subGoal = proofTree.getFirstOpenDescendant();
						if (subGoal != null) {
							viewer.setSelection(new StructuredSelection(subGoal));
						}
					}
				}
			}
		};
		normAction.setText("Normalisation");
		normAction.setToolTipText("Applying some common (normalisation) rules at this node");
		normAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		conjIAction = new Action() {
			public void run() {
				TreeViewer viewer = ProofTreeUIActionGroup.this.proofTreeUI.getViewer();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();

				if (obj instanceof IProofTreeNode) {
					IProofTreeNode proofTree = (IProofTreeNode) obj;
					if (!proofTree.isDischarged()) {
						Tactics.conjI().apply(proofTree);
						viewer.refresh(proofTree);
						// Expand the node
						viewer.expandToLevel(proofTree, AbstractTreeViewer.ALL_LEVELS);
						//viewer.setExpandedState(proofTree, true);
						
						// Select the first pending "subgoal"
						IProofTreeNode subGoal = proofTree.getFirstOpenDescendant();
						if (subGoal != null) {
							viewer.setSelection(new StructuredSelection(subGoal));
						}
					}
				}
			}
		};
		conjIAction.setText("Conj");
		conjIAction.setToolTipText("Applying conjI tactic to the current sequent");
		conjIAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		hypAction = new Action() {
			public void run() {
				TreeViewer viewer = ProofTreeUIActionGroup.this.proofTreeUI.getViewer();

				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();

				if (obj instanceof IProofTreeNode) {
					IProofTreeNode proofTree = (IProofTreeNode) obj;
					if (!proofTree.isDischarged()) {
						Tactics.hyp().apply(proofTree);
						ProofTreeUIActionGroup.this.proofTreeUI.refresh(proofTree);
						// Expand the node
						viewer.expandToLevel(proofTree, AbstractTreeViewer.ALL_LEVELS);
						//viewer.setExpandedState(proofTree, true);

						ProofState ps = ProofTreeUIActionGroup.this.proofTreeUI.getUserSupport().getCurrentPO();
						// Select the next pending "subgoal"
						IProofTreeNode pt = ps.getNextPendingSubgoal(proofTree);
						if (pt != null) 
							ProofTreeUIActionGroup.this.proofTreeUI.getViewer().setSelection(new StructuredSelection(pt));
					}
				}
			}
		};
		hypAction.setText("Hypothesis");
		hypAction.setToolTipText("Applying hyp tactic to the current sequent");
		hypAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		allIAction = new Action() {
			public void run() {
				TreeViewer viewer = ProofTreeUIActionGroup.this.proofTreeUI.getViewer();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();

				if (obj instanceof IProofTreeNode) {
					IProofTreeNode proofTree = (IProofTreeNode) obj;
					if (!proofTree.isDischarged()) {
						Tactics.allI().apply(proofTree);
						viewer.refresh(proofTree);
						// Expand the node
						viewer.expandToLevel(proofTree, AbstractTreeViewer.ALL_LEVELS);
						//viewer.setExpandedState(proofTree, true);
						
						// Select the first pending "subgoal"
						IProofTreeNode subGoal = proofTree.getFirstOpenDescendant();
						if (subGoal != null) {
							viewer.setSelection(new StructuredSelection(subGoal));
						}
					}
				}
			}
		};
		allIAction.setText("AllI");
		allIAction.setToolTipText("Applying allI tactic to the current sequent");
		allIAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		impIAction = new Action() {
			public void run() {
				TreeViewer viewer = ProofTreeUIActionGroup.this.proofTreeUI.getViewer();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();

				if (obj instanceof IProofTreeNode) {
					IProofTreeNode proofTree = (IProofTreeNode) obj;
					if (!proofTree.isDischarged()) {
						Tactics.impI().apply(proofTree);
						viewer.refresh(proofTree);
						// Expand the node
						viewer.expandToLevel(proofTree, AbstractTreeViewer.ALL_LEVELS);
						//viewer.setExpandedState(proofTree, true);
						
						// Select the first pending "subgoal"
						IProofTreeNode subGoal = proofTree.getFirstOpenDescendant();
						if (subGoal != null) {
							viewer.setSelection(new StructuredSelection(subGoal));
						}
					}
				}
			}
		};
		impIAction.setText("impI");
		impIAction.setToolTipText("Applying impI tactic to the current sequent");
		impIAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		trivialAction = new Action() {
			public void run() {
				TreeViewer viewer = ProofTreeUIActionGroup.this.proofTreeUI.getViewer();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();

				if (obj instanceof IProofTreeNode) {
					IProofTreeNode proofTree = (IProofTreeNode) obj;
					if (!proofTree.isDischarged()) {
						Tactics.trivial().apply(proofTree);
						viewer.refresh(proofTree);
						// Expand the node
						viewer.expandToLevel(proofTree, AbstractTreeViewer.ALL_LEVELS);
						//viewer.setExpandedState(proofTree, true);
						
						// Select the first pending "subgoal"
						IProofTreeNode subGoal = proofTree.getFirstOpenDescendant();
						if (subGoal != null) {
							viewer.setSelection(new StructuredSelection(subGoal));
						}
					}
				}
			}
		};
		trivialAction.setText("trivial");
		trivialAction.setToolTipText("Applying trivial tactic to the current sequent");
		trivialAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

	}
	
	/* 
	 * Dynamically fill the context menu (depends on the selection).
	 * <p> 
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillContextMenu(IMenuManager menu) {
		ISelection sel = getContext().getSelection();
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) sel;
			if (ssel.size() == 1) {
				IProofTreeNode pt = (IProofTreeNode) ssel.getFirstElement();

				// TODO Prechecking for displaying here
				if (!pt.isOpen()) {
					menu.add(pruneAction);
				}
				else {
					menu.add(conjIAction);
					menu.add(hypAction);
					menu.add(allIAction);
					menu.add(impIAction);
					menu.add(trivialAction);
					menu.add(new Separator());
				}
			}
			else {
				menu.add(pruneAction);
			}
			// Other plug-ins can contribute there actions here
			menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		}
	}


	
	
}
