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

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eventb.core.EventBPlugin;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Proof Tree UI 'page'.
 */
public class ProofTreeUIPage extends Page implements IProofTreeUIPage,
		ISelectionChangedListener, IUserSupportManagerChangedListener {

	// private static final int MAX_WIDTH = 1500;

	// The contained tree viewer.
	TreeViewer viewer;

	// private Text comments;

	// The invisible root of the tree.
	private IProofTree invisibleRoot = null;

	private IProofTreeNode root = null;

	// TODO Change to Rule class?
	private Object[] filters = {}; // Default filters

	// private TreeColumn elementColumn;

	// The current editting element.
	private Object fInput;

	// private Combo confidentLevel;

	IUserSupport userSupport;

	// Group of action that is used.
	ProofTreeUIActionGroup groupActionSet;

	/**
	 * @author htson
	 *         <p>
	 *         This class provides the labels for elements in the tree viewer.
	 */
	private class ProofTreeLabelProvider extends LabelProvider implements
			IFontProvider, IPropertyChangeListener {

		public ProofTreeLabelProvider() {
			JFaceResources.getFontRegistry().addListener(this);
		}

		public Font getFont(Object element) {
			return JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
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

		@Override
		public void dispose() {
			JFaceResources.getFontRegistry().removeListener(this);
			super.dispose();
		}

		@Override
		public Image getImage(Object element) {
			if (element instanceof IProofTreeNode) {
				return EventBImage
						.getProofTreeNodeImage((IProofTreeNode) element);
			}
			return super.getImage(element);
		}

		@Override
		public String getText(Object element) {
			if (element instanceof IProofTreeNode) {
				IProofTreeNode proofTree = (IProofTreeNode) element;

				if (!proofTree.isOpen()) {
					if (ProofTreeUI.showGoal)
						return proofTree.getRule().getDisplayName() + " : "
								+ proofTree.getSequent().goal();
					else 
						return proofTree.getRule().getDisplayName();
				} else {
					return proofTree.getSequent().goal().toString();
				}
			}
			return super.getText(element);
		}
	}

	/**
	 * Creates a content outline page using the given editor. Register as a
	 * change listener for the Rodin Database.
	 * <p>
	 * 
	 * @param userSupport
	 *            the User Support
	 */
	public ProofTreeUIPage(IUserSupport userSupport) {
		super();
		this.userSupport = userSupport;
		// byUserSupport = false;
		EventBPlugin.getDefault().getUserSupportManager().addChangeListener(
				this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPageBookViewPage#init(org.eclipse.ui.part.IPageSite)
	 */
	@Override
	public void init(IPageSite pageSite) {
		super.init(pageSite);
		pageSite.setSelectionProvider(this);
	}

	@Override
	public void createControl(Composite parent) {
		Composite comp = parent;

		viewer = new TreeViewer(comp, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ProofTreeUIContentProvider(this));
		viewer.setLabelProvider(new ProofTreeLabelProvider());
		viewer.addSelectionChangedListener(this);
		Tree tree = viewer.getTree();
		tree.setHeaderVisible(false);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		tree.setLayoutData(gd);

		// elementColumn = new TreeColumn(tree, SWT.LEFT);
		ProofTreeUIToolTip handler = new ProofTreeUIToolTip(viewer.getControl()
				.getShell(), userSupport);
		handler.activateHoverHelp(viewer.getControl());

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		if (fInput != null)
			update();
		// elementColumn.pack();
		// elementColumn.setWidth(MAX_WIDTH);

	}

	/**
	 * Sets the input of the outline page
	 * <p>
	 * 
	 * @param input
	 *            the input of this outline page
	 */
	public void setInput(Object input) {
		fInput = input;
		update();
	}

	/**
	 * Updates the outline page. Remember the previous expand states.
	 */
	private void update() {
		if (viewer != null) {
			Control control = viewer.getControl();
			if (control != null && !control.isDisposed()) {
				control.setRedraw(false);

				// Saving the expanded elements
				Object[] elements = viewer.getExpandedElements();
				viewer.setInput(fInput);
				// viewer.refresh();
				if (fInput != null) {
					viewer.setExpandedElements(elements);
					// elementColumn.pack();
					// elementColumn.setWidth(MAX_WIDTH);
					// UIUtils.debug("Width: " + elementColumn.getWidth());
					viewer.refresh();

					IProofState currentPO = userSupport.getCurrentPO();
					if (currentPO != null & currentPO.getCurrentNode() != null)
						viewer.setSelection(new StructuredSelection(currentPO
								.getCurrentNode()));
				}
				control.setRedraw(true);
			}
		}
	}

	@Override
	public void dispose() {
		EventBPlugin.getDefault().getUserSupportManager().removeChangeListener(
				this);
		super.dispose();
	}

	/**
	 * Setup the context menu.
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
		// getSite().registerContextMenu(menuMgr, viewer);
	}

	/**
	 * Setup the action bar.
	 */
	private void contributeToActionBars() {
		IActionBars bars = this.getSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * Setup the local pull down.
	 * <p>
	 * 
	 * @param manager
	 *            the menu manager
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(groupActionSet.filterAction);
		manager.add(new Separator());
		manager.add(groupActionSet.nextPOAction);
		manager.add(groupActionSet.prevPOAction);
	}

	/**
	 * Setup the local tool bar.
	 * <p>
	 * 
	 * @param manager
	 *            the menu manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		groupActionSet.drillDownAdapter.addNavigationActions(manager);
		manager.update(true);
	}

	/**
	 * Create various actions.
	 */
	private void makeActions() {
		groupActionSet = new ProofTreeUIActionGroup(this);
	}

	/**
	 * Hook the double click action.
	 */
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				groupActionSet.normAction.run();
			}
		});
	}

	/**
	 * Refresh the tree viewer from the proof tree node.
	 * <p>
	 * 
	 * @param pt
	 *            a proof tree node
	 */
	protected void refresh(IProofTreeNode pt) {
		// TODO Refresh the parent of this proof tree
		Object[] expands = viewer.getExpandedElements();
		viewer.refresh(true);
		viewer.setExpandedElements(expands);
		return;
	}

	/**
	 * Refresh the whole tree viewer.
	 */
	protected void refresh() {
		viewer.refresh(true);
		return;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 * <p>
	 * 
	 * @see org.eclipse.ui.part.IPage#setFocus()
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * Getting the viewer.
	 * <p>
	 * 
	 * @return the tree viewer contains in the page
	 */
	protected TreeViewer getViewer() {
		return viewer;
	}

	/**
	 * Set the list of filters.
	 * <p>
	 * 
	 * @param newFilters
	 *            a list of filters
	 */
	protected void setFilters(Object[] newFilters) {
		this.filters = newFilters;
		viewer.refresh();
		viewer.expandAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	@Override
	public Control getControl() {
		if (viewer == null)
			return null;
		return viewer.getControl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		viewer.addSelectionChangedListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		if (viewer == null)
			return StructuredSelection.EMPTY;
		return viewer.getSelection();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		viewer.removeSelectionChangedListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		if (viewer != null)
			viewer.setSelection(selection);
	}

	/**
	 * This is called when there is a selection change in the tree. This
	 * responses by selecting the first element of the selection in the editor.
	 * <p>
	 * 
	 * @param event
	 *            the selection event
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		if (ProofTreeUIUtils.DEBUG)
			ProofTreeUIUtils.debug("Selection Changed: " + event);
		ISelection sel = event.getSelection();

		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) sel;
			if (!ssel.isEmpty()) {
				// if (byUserSupport) { // Do nothing if the selection is from
				// // UserSupport
				// byUserSupport = false;
				// return;
				// }

				Object obj = ssel.getFirstElement();
				if (obj instanceof IProofTreeNode) {
					try {
						userSupport.selectNode((IProofTreeNode) obj);
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// if (ssel.size() == 1) {
				// IProofTreeNode node = (IProofTreeNode) ssel
				// .getFirstElement();
				// comments.setText(node.getComment());
				// }
			} else { // Do nothing when there is no selection
				// editor.getUserSupport().selectNode(null);
			}
		}

	}

	/**
	 * Set the invisible root of the tree viewer.
	 * <p>
	 * 
	 * @param pt
	 *            a Proof Tree
	 */
	public void setInvisibleRoot(IProofTree pt) {
		this.invisibleRoot = pt;
	}

	/**
	 * Return the invisible root of the tree viewer.
	 * <p>
	 * 
	 * @return a Proof Tree which is the invisible root of the tree viewer
	 */
	public IProofTree getInvisibleRoot() {
		return invisibleRoot;
	}

	/**
	 * Setting the root of the tree viewer.
	 * <p>
	 * 
	 * @param pt
	 *            a Proof Tree Node
	 */
	public void setRoot(IProofTreeNode pt) {
		this.root = pt;
	}

	/**
	 * Return the roof of the tree viewer.
	 * <p>
	 * 
	 * @return the Proof Tree Node which is the root of the tree viewer
	 */
	public IProofTreeNode getRoot() {
		return root;
	}

	/**
	 * Return the associated UserSupport.
	 * 
	 * @return the associated UserSupport
	 */
	public IUserSupport getUserSupport() {
		return userSupport;
	}

	/**
	 * Select the root of the tree viewer.
	 */
	public void selectRoot() {
		this.setSelection(new StructuredSelection(root));
	}

	public void userSupportManagerChanged(final IUserSupportManagerDelta delta) {
		// byUserSupport = true;
		if (ProofTreeUIUtils.DEBUG)
			ProofTreeUIUtils.debug("Proof Tree UI for "
					+ ProofTreeUIPage.this.userSupport.getInput()
							.getElementName() + ": State Changed: "
					+ delta.toString());

		final IUserSupportDelta affectedUserSupport = ProverUIUtils
				.getUserSupportDelta(delta, userSupport);

		if (affectedUserSupport == null)
			return;

		final int kind = affectedUserSupport.getKind();
		if (kind == IUserSupportDelta.REMOVED) {
			return; // Do nothing
		}

		Display display = EventBUIPlugin.getDefault().getWorkbench()
				.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (kind == IUserSupportDelta.ADDED) {
					IProofState ps = userSupport.getCurrentPO();
					if (ps != null) { // Change only when change the PO
						ProofTreeUIPage page = ProofTreeUIPage.this;
						page.setInput(ps.getProofTree());
						IProofTreeNode currentNode = ps.getCurrentNode();
						page.getViewer().expandAll();
						if (currentNode != null)
							page.getViewer().setSelection(
									new StructuredSelection(currentNode));
					} else {
						ProofTreeUIPage page = ProofTreeUIPage.this;
						page.setInput(null);
					}
				} else if (kind == IUserSupportDelta.CHANGED) {
					int flags = affectedUserSupport.getFlags();
					if ((flags | IUserSupportDelta.F_CURRENT) != 0) {
						IProofState ps = userSupport.getCurrentPO();
						if (ps != null) { // Change only when change the PO
							ProofTreeUIPage page = ProofTreeUIPage.this;
							page.setInput(ps.getProofTree());
							IProofTreeNode currentNode = ps.getCurrentNode();
							page.getViewer().expandAll();
							// elementColumn.pack();
							// elementColumn.setWidth(MAX_WIDTH);
							if (currentNode != null)
								page.getViewer().setSelection(
										new StructuredSelection(currentNode));
						} else {
							ProofTreeUIPage page = ProofTreeUIPage.this;
							page.setInput(null);
							// elementColumn.pack();
							// elementColumn.setWidth(MAX_WIDTH);
						}
					} else if ((flags | IUserSupportDelta.F_STATE) != 0) {
						IProofState proofState = userSupport.getCurrentPO();
						IProofStateDelta affectedProofState = ProverUIUtils
								.getProofStateDelta(affectedUserSupport,
										proofState);
						if (affectedProofState != null) {
							if (affectedProofState.getKind() == IProofStateDelta.ADDED) {
								if (proofState != null) { // Change only when
															// change the PO
									ProofTreeUIPage page = ProofTreeUIPage.this;
									page.setInput(proofState.getProofTree());
									IProofTreeNode currentNode = proofState
											.getCurrentNode();
									page.getViewer().expandAll();
									// elementColumn.pack();
									// elementColumn.setWidth(MAX_WIDTH);
									if (currentNode != null)
										page.getViewer().setSelection(
												new StructuredSelection(
														currentNode));
								} else {
									ProofTreeUIPage page = ProofTreeUIPage.this;
									page.setInput(null);
									// elementColumn.pack();
									// elementColumn.setWidth(MAX_WIDTH);
								}
							} else if (affectedProofState.getKind() == IProofStateDelta.REMOVED) {
								return;
							} else if (affectedProofState.getKind() == IProofStateDelta.CHANGED) {
								if ((affectedProofState.getFlags() | IProofStateDelta.F_PROOFTREE) != 0) {
									viewer.refresh();
								}
								if ((affectedProofState.getFlags() | IProofStateDelta.F_NODE) != 0) {
									IProofTreeNode node = proofState
											.getCurrentNode();
									if (node != null) {
										viewer.setSelection(
												new StructuredSelection(node),
												true);
									}
								}
							}
						}

					}
				}
			}
		});
	}

	public Object[] getFilters() {
		return filters;
	}

}