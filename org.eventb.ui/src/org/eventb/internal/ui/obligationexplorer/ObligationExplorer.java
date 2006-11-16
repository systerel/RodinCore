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

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.ViewPart;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPSFile;
import org.eventb.core.IPRProofTree;
import org.eventb.core.IPSstatus;
import org.eventb.core.basis.PSstatus;
import org.eventb.core.pm.IProofStateChangedListener;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUSManagerListener;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.ProofState;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.pm.UserSupportManager;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeDelta;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.TimerText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.proofcontrol.ProofControl;
import org.eventb.internal.ui.prooftreeui.ProofTreeUI;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.ui.ElementSorter;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinElement;
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
//	private TreeColumn column;

	/**
	 * The plug-in identifier of the Obligation Explorer (value
	 * <code>"org.eventb.ui.views.ObligationExplorer"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID
			+ ".views.ObligationExplorer";
//
//	private static final int MAX_WIDTH = 500;

	// The tree viewer to display the structure of projects, components, etc.
	private TreeViewer viewer;

	// Group of action that is used.
	private ObligationExplorerActionGroup groupActionSet;

	// A flag to indicate if the selection is made externally.
	private boolean byExternal;

	private ToolItem exclude;

	private ToolItem discharge;

	private Text filterText;

	private static int NULL = 0;

	private static int UNATTEMPTED = 1;

	private static int PENDING_BROKEN = 2;

	private static int PENDING = 3;

	private static int REVIEWED_BROKEN = 4;

	private static int REVIEWED = 5;

	private static int DISCHARGED_BROKEN = 6;

	private static int DISCHARGED;

	private class ObligationTextFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {

			boolean selection = exclude.getSelection();
			if (element instanceof PSstatus) {
				PSstatus sequent = (PSstatus) element;

				if (sequent.getElementName().indexOf(filterText.getText()) == -1)
					return selection;
				else
					return !selection;
			} else {
				return true;
			}
		}

	}

	private class DischargedFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			boolean selection = discharge.getSelection();
			if (element instanceof IPSstatus) {
				IPSstatus sequent = (IPSstatus) element;
				int status = NULL;
				try {
					status = getStatus(sequent);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (status == DISCHARGED)
					return !selection;
				else
					return true;
			} else {
				return true;
			}

		}

	}

	private int getStatus(IPSstatus status) throws RodinDBException {
		// Try to synchronize with the proof tree in memory
		Collection<UserSupport> userSupports = UserSupportManager
				.getUserSupports();
		final boolean proofBroken = (! status.isProofValid(null));
		for (IUserSupport userSupport : userSupports) {
			// UIUtils.debugObligationExplorer("Get US: "
			// + userSupport);
			Collection<ProofState> proofStates = userSupport.getPOs();
			for (ProofState proofState : proofStates) {
				if (proofState.getPRSequent().equals(status)) {
					IProofTree tree = proofState.getProofTree();
					if (!proofState.isDirty() || tree == null)
						break;

					if (!tree.proofAttempted())
						return UNATTEMPTED;

					int confidence = tree.getConfidence();

					if (confidence == IConfidence.PENDING) {
						if (false && proofBroken)
							return PENDING_BROKEN;
						else
							return PENDING;
					}
					if (confidence <= IConfidence.REVIEWED_MAX) {
						if (false && proofBroken)
							return REVIEWED_BROKEN;
						else
							return REVIEWED;
					}
					if (confidence <= IConfidence.DISCHARGED_MAX) {
						if (false && proofBroken)
							return DISCHARGED_BROKEN;
						else
							return DISCHARGED;
					}
					return NULL; // Should not happen
				}
			}
		}

		// Otherwise, setting the label accordingly.

		final IPRProofTree prProofTree = status.getProofTree();

		// TODO : confidence now expresses unattempted as well
		if (prProofTree == null || (prProofTree.getConfidence(null) <= IConfidence.UNATTEMPTED))
			return UNATTEMPTED;

		int confidence = prProofTree.getConfidence(null);
		if (proofBroken) {

			if (confidence == IConfidence.PENDING)
				return PENDING_BROKEN;
			if (confidence <= IConfidence.REVIEWED_MAX)
				return REVIEWED_BROKEN;
			if (confidence <= IConfidence.DISCHARGED_MAX)
				return DISCHARGED_BROKEN;

		} else {

			if (confidence == IConfidence.PENDING)
				return PENDING;
			if (confidence <= IConfidence.REVIEWED_MAX)
				return REVIEWED;
			if (confidence <= IConfidence.DISCHARGED_MAX)
				return DISCHARGED;

		}

		return NULL;

		// Previous code:
		// IProof status = ps.getProof();
		// if (status.getContents().equals("PENDING"))
		// return registry.get(EventBImage.IMG_PENDING);
		// else if (status.getContents().equals("DISCHARGED"))
		// return registry.get(EventBImage.IMG_DISCHARGED);
	}

	/**
	 * The constructor.
	 */
	public ObligationExplorer() {
		byExternal = false;
	}

	private void registerUserSupports() {
		Collection<UserSupport> userSupports = UserSupportManager
				.getUserSupports();
		for (IUserSupport userSupport : userSupports) {
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
	private class ObligationLabelProvider extends LabelProvider implements
			IFontProvider, IColorProvider, IPropertyChangeListener {

		public ObligationLabelProvider() {
			JFaceResources.getFontRegistry().addListener(this);
		}

		@Override
		public Image getImage(Object element) {
			ImageRegistry registry = EventBUIPlugin.getDefault()
					.getImageRegistry();
			if (element instanceof IPSstatus) {
				IPSstatus status = (IPSstatus) element;
				try {

					// Try to synchronize with the proof tree in memory
					Collection<UserSupport> userSupports = UserSupportManager
							.getUserSupports();
					for (IUserSupport userSupport : userSupports) {
						// UIUtils.debugObligationExplorer("Get US: "
						// + userSupport);
						Collection<ProofState> proofStates = userSupport
								.getPOs();
						for (ProofState proofState : proofStates) {
							if (proofState.getPRSequent().equals(element)) {
								IProofTree tree = proofState.getProofTree();

								if (tree != null && proofState.isDirty()) {
									if (!tree.proofAttempted())
										return registry
												.get(IEventBSharedImages.IMG_UNATTEMPTED);

									int confidence = tree.getConfidence();

									final boolean proofBroken = (! status.isProofValid(null));
									if (confidence == IConfidence.PENDING) {
										if (false && proofBroken)
											return registry
													.get(IEventBSharedImages.IMG_PENDING_BROKEN);
										else
											return registry
													.get(IEventBSharedImages.IMG_PENDING);
									}
									if (confidence <= IConfidence.REVIEWED_MAX) {
										if (false && proofBroken)
											return registry
													.get(IEventBSharedImages.IMG_REVIEWED_BROKEN);
										else
											return registry
													.get(IEventBSharedImages.IMG_REVIEWED);
									}
									if (confidence <= IConfidence.DISCHARGED_MAX) {
										if (false && proofBroken)
											return registry
													.get(IEventBSharedImages.IMG_DISCHARGED_BROKEN);
										else
											return registry
													.get(IEventBSharedImages.IMG_DISCHARGED);
									}
									return registry
											.get(IEventBSharedImages.IMG_DEFAULT);
								}
							}
						}
					}

					// Otherwise, setting the label accordingly.
					return EventBImage.getPRSequentImage(status);
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
			}
			if (element instanceof IPSFile) {
				IPSFile prFile = (IPSFile) element;
				if (prFile.getMachine().exists())
					return registry.get(IEventBSharedImages.IMG_MACHINE);
				else if (prFile.getContext().exists())
					return registry.get(IEventBSharedImages.IMG_CONTEXT);
			}
			if (element instanceof IRodinElement)
				return EventBImage.getRodinImage((IRodinElement) element);
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object obj) {
			if (ObligationExplorerUtils.DEBUG)
				ObligationExplorerUtils.debug("Label for: " + obj);
			if (obj instanceof IRodinProject) {
				if (ObligationExplorerUtils.DEBUG)
					ObligationExplorerUtils.debug("Project: "
							+ ((IRodinProject) obj).getElementName());
				return ((IRodinProject) obj).getElementName();
			} else if (obj instanceof IRodinFile) {
				String name = ((IRodinFile) obj).getElementName();
				return EventBPlugin.getComponentName(name);
			} else if (obj instanceof IPSstatus) {

				// Find the label in the list of UserSupport.
				Collection<UserSupport> userSupports = UserSupportManager
						.getUserSupports();
				for (IUserSupport userSupport : userSupports) {
					// UIUtils.debugObligationExplorer("Get US: " +
					// userSupport);
					Collection<ProofState> proofStates = userSupport.getPOs();
					for (ProofState proofState : proofStates) {
						if (proofState.getPRSequent().equals(obj)) {
							if (proofState.isDirty())
								return "* " + ((IPSstatus) obj).getElementName();
							else
								return ((IPSstatus) obj).getElementName();
						}
					}
				}
				return ((IPSstatus) obj).getElementName();
			}

			return obj.toString();
		}

		public void dispose() {
			JFaceResources.getFontRegistry().removeListener(this);
			super.dispose();
		}

		public Font getFont(Object element) {
			return JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		}

		public Color getForeground(Object element) {
			Display display = Display.getCurrent();
			return display.getSystemColor(SWT.COLOR_BLACK);
		}

		public Color getBackground(Object element) {
			Display display = Display.getCurrent();
			Color white = display.getSystemColor(SWT.COLOR_WHITE);
			Color yellow = display.getSystemColor(SWT.COLOR_YELLOW);
			if (element instanceof IRodinProject)
				return white;
			if (element instanceof IRodinFile) {
				return white;
			}
			if (element instanceof IPSstatus) {
				// UIUtils.debugObligationExplorer("Label for: " + obj);

				// Find the label in the list of UserSupport.
				Collection<UserSupport> userSupports = UserSupportManager
						.getUserSupports();
				for (IUserSupport userSupport : userSupports) {
					// UIUtils.debugObligationExplorer("Get US: " +
					// userSupport);
					Collection<ProofState> proofStates = userSupport.getPOs();
					for (ProofState proofState : proofStates) {
						if (proofState.getPRSequent().equals(element)) {
							if (proofState.isDirty())
								return yellow;
							else
								return white;
						}
					}
				}
				return white;
			}

			return white;
		}

		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty()
					.equals(PreferenceConstants.EVENTB_MATH_FONT)) {
				if (event.getProperty().equals(
						PreferenceConstants.EVENTB_MATH_FONT)) {
					viewer.refresh();
				}
			}
		}
		
	}

	CoolItem createToolItem(CoolBar coolBar) {
		ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT);
		discharge = new ToolItem(toolBar, SWT.CHECK);
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		discharge.setImage(registry.get(IEventBSharedImages.IMG_DISCHARGED));
		discharge.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (ObligationExplorerUtils.DEBUG) {
					ObligationExplorerUtils.debug("Event " + e);
					ObligationExplorerUtils.debug("Status "
							+ exclude.getSelection());
				}
				viewer.refresh();
//				column.pack();
//				column.setWidth(MAX_WIDTH);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		exclude = new ToolItem(toolBar, SWT.CHECK);
		exclude.setText("ex");
		exclude.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (ObligationExplorerUtils.DEBUG) {
					ObligationExplorerUtils.debug("Event " + e);
					ObligationExplorerUtils.debug("Status "
							+ exclude.getSelection());
				}
				viewer.refresh();
//				column.pack();
//				column.setWidth(MAX_WIDTH);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

		});

		toolBar.pack();
		Point size = toolBar.getSize();
		CoolItem item = new CoolItem(coolBar, SWT.NONE);
		item.setControl(toolBar);
		Point preferred = item.computeSize(size.x, size.y);
		item.setPreferredSize(preferred);
		return item;
	}

	CoolItem createText(CoolBar coolBar) {
		filterText = new Text(coolBar, SWT.SINGLE | SWT.BORDER);
		new TimerText(filterText, 1000) {

			@Override
			protected void response() {
				viewer.refresh();
			}

		};
		filterText.pack();
		Point size = filterText.getSize();
		CoolItem item = new CoolItem(coolBar, SWT.NONE);
		item.setControl(filterText);
		Point preferred = item.computeSize(size.x, size.y);
		item.setPreferredSize(preferred);
		return item;
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 * <p>
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		FormLayout layout = new FormLayout();
		parent.setLayout(layout);
		CoolBar coolBar = new CoolBar(parent, SWT.FLAT);
		FormData coolData = new FormData();
		coolData.left = new FormAttachment(0);
		coolData.right = new FormAttachment(100);
		coolData.top = new FormAttachment(0);
		coolBar.setLayoutData(coolData);

		createToolItem(coolBar);
		createText(coolBar);

		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.setContentProvider(new ObligationExplorerContentProvider(this));
		viewer.setLabelProvider(new ObligationLabelProvider());
		viewer.setSorter(new ElementSorter());
		viewer.addFilter(new ObligationTextFilter());
		viewer.addFilter(new DischargedFilter());
		FormData textData = new FormData();
		textData.left = new FormAttachment(0);
		textData.right = new FormAttachment(100);
		textData.top = new FormAttachment(coolBar);
		textData.bottom = new FormAttachment(100);
		viewer.getControl().setLayoutData(textData);
		Tree tree = viewer.getTree();
		tree.setHeaderVisible(false);
//		column = new TreeColumn(tree, SWT.LEFT);
		viewer.setInput(EventBUIPlugin.getRodinDatabase());
//		column.pack();
//		column.setWidth(MAX_WIDTH);

		// Sync with the current active ProverUI
		IWorkbenchPage activePage = EventBUIPlugin.getActivePage();
		if (activePage != null) {
			IEditorPart editor = activePage.getActiveEditor();
			if (editor instanceof ProverUI) {
				IPSstatus prSequent = ((ProverUI) editor)
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
		UserSupportManager.addUSManagerListener(this);
		registerUserSupports();
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
//		column.pack();
//		column.setWidth(MAX_WIDTH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		if (byExternal)
			return;

		if (ObligationExplorerUtils.DEBUG)
			ObligationExplorerUtils.debug("Selection changed: ");
		ISelection sel = event.getSelection();

		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) sel;

			if (!ssel.isEmpty()) {
				// UIUtils.debugObligationExplorer("Activate UI "
				// + ssel.toString());
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (obj instanceof IPSstatus) {
					IPSstatus ps = (IPSstatus) obj;

					selectPO(ps);
				}
			} else {
				if (ObligationExplorerUtils.DEBUG)
					ObligationExplorerUtils.debug("De-selected");
				// Do nothing when there is no selection
				// editor.getUserSupport().selectNode(null);
			}
		}

	}

	private void selectPO(IPSstatus ps) {
		UIUtils.linkToProverUI(ps);
		UIUtils.activateView(ProofControl.VIEW_ID);
		UIUtils.activateView(ProofTreeUI.VIEW_ID);
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
			if (ObligationExplorerUtils.DEBUG)
				ObligationExplorerUtils.debug("Set new Selection");
			viewer.getControl().setRedraw(false);
			viewer.setSelection(new StructuredSelection(obj));
			viewer.getControl().setRedraw(true);
		}
		byExternal = false;
	}

	public void USManagerChanged(final IUserSupport userSupport, final int status) {

		Control control = viewer.getControl();
		if (control.isDisposed())
			return;

		Display display = control.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (ObligationExplorerUtils.DEBUG)
					ObligationExplorerUtils.debug("Obligation Explorer: "
							+ userSupport + " : " + status);
				if (status == UserSupportManager.ADDED) {
					userSupport
							.addStateChangedListeners(ObligationExplorer.this);
				}
				if (status == UserSupportManager.REMOVED) {
					userSupport
							.removeStateChangedListeners(ObligationExplorer.this);
				}
				viewer.refresh(userSupport.getInput());
			}
		});
	}

	public void proofStateChanged(final IProofStateDelta delta) {

		Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				if (ObligationExplorerUtils.DEBUG)
					ObligationExplorerUtils
							.debug("Obligation Exprlorer: Proof Changed "
									+ delta);
				final ProofState ps = delta.getProofState();

				final IUserSupport userSupport = delta.getSource();
				if (viewer.getControl().isDisposed())
					return;
				viewer.refresh(userSupport.getInput(), true);
//				column.pack();
//				column.setWidth(MAX_WIDTH);
				if (delta.isNewProofState()) {

					if (ps != null) {
						IPSstatus prSequent = ps.getPRSequent();
						externalSetSelection(prSequent);
					} else { // Empty selection
						clearSelection();
					}
				} else if (delta.isDeleted()) {
					// Do nothing
				} else {
					IProofTreeDelta proofTreeDelta = delta.getProofTreeDelta();
					if (proofTreeDelta != null) {
						ProofState state = userSupport.getCurrentPO();
						IPSstatus prSequent = state.getPRSequent();
						viewer.refresh(prSequent, true);
//						column.pack();
//						column.setWidth(MAX_WIDTH);
					}
				}
			}
		});
	}

	@Override
	public void dispose() {
		if (viewer == null)
			return;
		Collection<UserSupport> userSupports = UserSupportManager
				.getUserSupports();
		for (IUserSupport userSupport : userSupports) {
			userSupport.removeStateChangedListeners(this);
		}
		UserSupportManager.removeUSManagerListener(this);
		viewer.removeSelectionChangedListener(this);
		super.dispose();
	}

	private void clearSelection() {
		viewer.getControl().setRedraw(false);
		viewer.setSelection(new StructuredSelection());
		viewer.getControl().setRedraw(true);
	}

}