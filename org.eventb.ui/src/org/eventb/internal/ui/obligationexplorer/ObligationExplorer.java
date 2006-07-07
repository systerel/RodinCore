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

package org.eventb.internal.ui.obligationexplorer;

import java.util.Collection;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.ViewPart;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.eventb.core.IProof.Status;
import org.eventb.core.pm.IProofStateChangedListener;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUSManagerListener;
import org.eventb.core.pm.ProofState;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.pm.UserSupportManager;
import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.IProofTreeDelta;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.prover.ProofControl;
import org.eventb.internal.ui.prover.ProofTreeUI;
import org.eventb.internal.ui.prover.ProverUI;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         The view shows data obtained from the model. The view gets a list of
 *         models and obligations from the RodinDB. The view is connected to the
 *         model using a content provider.
 */
public class ObligationExplorer extends ViewPart implements
		ISelectionChangedListener, IUSManagerListener,
		IProofStateChangedListener {

	/**
	 * The plug-in identifier of the Obligation Explorer (value
	 * <code>"org.eventb.ui.views.ObligationExplorer"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID
			+ ".views.ObligationExplorer";

	// Debug flag
	public static boolean DEBUG = false;

	// The tree viewer to display the structure of projects, components, etc.
	private TreeViewer viewer;

	// Action when double clicking.
	private Action doubleClickAction;

	// Group of action that is used.
	private ObligationExplorerActionGroup groupActionSet;

	// A flag to indicate if the selection is made externally.
	private boolean byExternal;

	/**
	 * The constructor.
	 */
	public ObligationExplorer() {
		byExternal = false;
		UserSupportManager.addUSManagerListener(this);
		registerUserSupports();
	}

	private void registerUserSupports() {
		Collection<UserSupport> userSupports = UserSupportManager
				.getUserSupports();
		for (UserSupport userSupport : userSupports) {
			userSupport.addStateChangedListeners(this);
		}
	}

	/**
	 * Get the contained tree viewer
	 * <p>
	 * 
	 * @return a tree viewer
	 */
	public TreeViewer getTreeViewer() {
		return viewer;
	}

	/**
	 * @author htson
	 *         <p>
	 *         This class provides the label for object in the tree.
	 */
	private class ObligationLabelProvider extends LabelProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object obj) {
			if (obj instanceof IRodinProject)
				return ((IRodinProject) obj).getElementName();
			if (obj instanceof IRodinFile) {
				String name = ((IRodinFile) obj).getElementName();
				return EventBPlugin.getComponentName(name);
			}
			if (obj instanceof IPRSequent) {
				UIUtils.debugObligationExplorer("Label for: " + obj);
				Collection<UserSupport> userSupports = UserSupportManager
						.getUserSupports();
				for (UserSupport userSupport : userSupports) {
					UIUtils.debugObligationExplorer("Get US: " + userSupport);
					Collection<ProofState> proofStates = userSupport.getPOs();
					for (ProofState proofState : proofStates) {
						if (proofState.getPRSequent().equals(obj)) {
							if (proofState.isDirty())
								return "> " + ((IPRSequent) obj).getName();
							else
								return ((IPRSequent) obj).getName();
						}
					}
				}
				return ((IPRSequent) obj).getName();
			}

			return obj.toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object obj) {
			ImageRegistry registry = EventBUIPlugin.getDefault()
					.getImageRegistry();
			if (obj instanceof IPRSequent) {
				IPRSequent prSequent = (IPRSequent) obj;
				try {
					// Replaced check on proof with check on sequent
					// TODO: synchronize with the proof tree in memory
					Collection<UserSupport> userSupports = UserSupportManager
							.getUserSupports();
					for (UserSupport userSupport : userSupports) {
						UIUtils.debugObligationExplorer("Get US: "
								+ userSupport);
						Collection<ProofState> proofStates = userSupport
								.getPOs();
						for (ProofState proofState : proofStates) {
							if (proofState.getPRSequent().equals(obj)) {
								IProofTree tree = proofState.getProofTree();
								if (tree.isDischarged()) {
									return registry
											.get(EventBImage.IMG_DISCHARGED);
								} else {
									return registry
											.get(EventBImage.IMG_PENDING);
								}
							}
						}
					}
					if (!prSequent.proofAttempted())
						return registry.get(EventBImage.IMG_UNATTEMPTED);

					if (prSequent.isProofBroken()) {
						Status proofStatus = prSequent.getProof().getStatus();
						if (proofStatus.equals(Status.PENDING))
							return registry.get(EventBImage.IMG_PENDING_BROKEN);
						if (proofStatus.equals(Status.REVIEWED))
							return registry
									.get(EventBImage.IMG_REVIEWED_BROKEN);
						if (proofStatus.equals(Status.DISCHARGED))
							return registry
									.get(EventBImage.IMG_DISCHARGED_BROKEN);

					} else {
						Status proofStatus = prSequent.getProof().getStatus();
						if (proofStatus.equals(Status.PENDING))
							return registry.get(EventBImage.IMG_PENDING);
						if (proofStatus.equals(Status.REVIEWED))
							return registry.get(EventBImage.IMG_REVIEWED);
						if (proofStatus.equals(Status.DISCHARGED))
							return registry.get(EventBImage.IMG_DISCHARGED);

					}

					return registry.get(EventBImage.IMG_DEFAULT);

					// Previous code:
					// IProof status = ps.getProof();
					// if (status.getContents().equals("PENDING"))
					// return registry.get(EventBImage.IMG_PENDING);
					// else if (status.getContents().equals("DISCHARGED"))
					// return registry.get(EventBImage.IMG_DISCHARGED);
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
			}
			if (obj instanceof IPRFile) {
				IPRFile prFile = (IPRFile) obj;
				if (prFile.getMachine().exists())
					return registry.get(EventBImage.IMG_MACHINE);
				else if (prFile.getContext().exists())
					return registry.get(EventBImage.IMG_CONTEXT);
			}
			return UIUtils.getImage(obj);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 * <p>
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.setContentProvider(new ObligationExplorerContentProvider(this));
		viewer.setLabelProvider(new ObligationLabelProvider());
		// viewer.setSorter(new ProjectsSorter());
		viewer.setInput(EventBUIPlugin.getRodinDatabase());

		// Sync with the current active ProverUI
		IWorkbenchPage activePage = EventBUIPlugin.getActivePage();
		if (activePage != null) {
			IEditorPart editor = activePage.getActiveEditor();
			if (editor instanceof ProverUI) {
				IPRSequent prSequent = ((ProverUI) editor)
						.getCurrentProverSequent();
				if (prSequent != null) {
					viewer.setSelection(new StructuredSelection(prSequent));
					viewer.reveal(prSequent);
				} else {
					IRodinFile prFile = ((ProverUI) editor).getRodinInput();
					viewer.setSelection(new StructuredSelection(prFile));
					viewer.reveal(prFile);
				}
			}
		}
		viewer.addSelectionChangedListener(this);
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	/**
	 * Hook the actions to the context menu.
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				groupActionSet.setContext(new ActionContext(viewer
						.getSelection()));
				groupActionSet.fillContextMenu(manager);
				groupActionSet.setContext(null);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	/**
	 * Put the actions to to the pull down menu and toolbar.
	 */
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * Fill the local pull down menu with actions.
	 * <p>
	 * 
	 * @param manager
	 *            a menu manager
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		MenuManager newMenu = new MenuManager("&New");
		manager.add(newMenu);
		manager.add(new Separator());
		manager.add(ObligationExplorerActionGroup.refreshAction);
	}

	/**
	 * Fill the toolbar with actions.
	 * <p>
	 * 
	 * @param manager
	 *            a menu manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(new Separator());
		ObligationExplorerActionGroup.drillDownAdapter
				.addNavigationActions(manager);
	}

	/**
	 * Creat the actions.
	 */
	private void makeActions() {
		groupActionSet = new ObligationExplorerActionGroup(this);

		// Double click to link with editor
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (obj instanceof IPRSequent) {
					IPRSequent ps = (IPRSequent) obj;

					UIUtils.linkToProverUI(ps);
					UIUtils.activateView(ProofControl.VIEW_ID);
					UIUtils.activateView(ProofTreeUI.VIEW_ID);

				}
			}
		};
	}

	/**
	 * Passing the focus request to the viewer's control.
	 * <p>
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * Refersh the view by refreshing the tree viewer.
	 */
	public void refresh() {
		viewer.refresh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		if (byExternal)
			return;

		UIUtils.debugObligationExplorer("Selection changed: ");
		ISelection sel = event.getSelection();

		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) sel;

			if (!ssel.isEmpty()) {
				UIUtils.debugObligationExplorer("Activate UI "
						+ ssel.toString());
				doubleClickAction.run();
			} else {
				UIUtils.debugObligationExplorer("De-selected");
				// Do nothing when there is no selection
				// editor.getUserSupport().selectNode(null);
			}
		}

	}

	/**
	 * External object need to call this methods to set the selection in the
	 * viewer.
	 * <p>
	 * 
	 * @param obj
	 *            the object will be selected
	 */
	public void externalSetSelection(Object obj) {
		byExternal = true;
		if (!((IStructuredSelection) viewer.getSelection()).toList().contains(
				obj)) {
			UIUtils.debugObligationExplorer("Set new Selection");
			viewer.getControl().setRedraw(false);
			viewer.setSelection(new StructuredSelection(obj));
			viewer.getControl().setRedraw(true);
		}
		byExternal = false;
	}

	public void USManagerChanged(UserSupport userSupport, boolean added) {
		UIUtils.debugObligationExplorer("Obligation Explorer: "
				+ userSupport.getCurrentPO() + " : " + added);
		if (added) {
			userSupport.addStateChangedListeners(this);
		} else {
			userSupport.removeStateChangedListeners(this);
		}

	}

	public void proofStateChanged(IProofStateDelta delta) {
		UIUtils.debugObligationExplorer("Obligation Exprlorer: Proof Changed "
				+ delta);
		final ProofState ps = delta.getNewProofState();
		Display display = viewer.getControl().getDisplay();

		if (ps != null) {
			display.syncExec(new Runnable() {

				public void run() {
					viewer.setSelection(new StructuredSelection(ps
							.getPRSequent()));
				}

			});

		} else {
			IProofTreeDelta proofTreeDelta = delta.getProofTreeDelta();
			if (proofTreeDelta != null) {
				UserSupport userSupport = delta.getSource();
				ProofState state = userSupport.getCurrentPO();
				final IPRSequent prSequent = state.getPRSequent();
				display.syncExec(new Runnable() {

					public void run() {
						viewer.refresh(prSequent, true);
					}

				});
			}
			// Refresh the PRSequent
		}

	}

}