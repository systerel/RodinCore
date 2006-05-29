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

package org.eventb.internal.ui.prover;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eventb.core.pm.IProofStateChangedListener;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.ProofState;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.UIUtils;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Proof Tree UI 'page'.
 */
public class ProofTreeUIPage extends Page implements IProofTreeUIPage,
		ISelectionChangedListener, IProofStateChangedListener {

	// list of Selection Changed listeners.
	private ListenerList selectionChangedListeners = new ListenerList();

	// The contained tree viewer.
	private TreeViewer viewer;

	// The invisible root of the tree.
	private IProofTree invisibleRoot = null;

	private IProofTreeNode root = null;

	// TODO Change to Rule class?
	private Object[] filters = { "allI" }; // Default filters

	private boolean byUserSupport;

	private TreeColumn elementColumn;

	// The current editting element.
	private Object fInput;

	// The current associated editor.
	// private ProverUI editor;

	private UserSupport userSupport;

	// Group of action that is used.
	private ProofTreeUIActionGroup groupActionSet;

	/**
	 * @author htson
	 *         <p>
	 *         This class provides the labels for elements in the tree viewer.
	 */
	class ViewLabelProvider implements ITableLabelProvider, ITableFontProvider,
			ITableColorProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
		 *      int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			ImageRegistry registry = EventBUIPlugin.getDefault()
					.getImageRegistry();

			if (element instanceof IProofTreeNode) {
				IProofTreeNode pt = (IProofTreeNode) element;
				if (pt.isOpen())
					return registry.get(EventBImage.IMG_PENDING);
				if (!pt.isDischarged())
					return registry.get(EventBImage.IMG_APPLIED);
				else
					return registry.get(EventBImage.IMG_DISCHARGED);
			}

			// TODO Removed?
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					imageKey);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
		 *      int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof IProofTreeNode) {
				IProofTreeNode proofTree = (IProofTreeNode) element;

				if (!proofTree.isOpen()) {
					return proofTree.getRule().getName() + " : "
							+ proofTree.getSequent().goal();
				} else {
					return proofTree.getSequent().goal().toString();
				}
			}
			return element.toString();

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose() {
			// TODO Auto-generated method stub

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
		 *      java.lang.String)
		 */
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object,
		 *      int)
		 */
		public Color getBackground(Object element, int columnIndex) {
			Display display = Display.getCurrent();
			return display.getSystemColor(SWT.COLOR_WHITE);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object,
		 *      int)
		 */
		public Color getForeground(Object element, int columnIndex) {
			Display display = Display.getCurrent();
			return display.getSystemColor(SWT.COLOR_BLACK);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITableFontProvider#getFont(java.lang.Object,
		 *      int)
		 */
		public Font getFont(Object element, int columnIndex) {
			// UIUtils.debug("Get fonts");
			return JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		}

	}

	/**
	 * Creates a content outline page using the given editor. Register as a
	 * change listener for the Rodin Database.
	 * <p>
	 * 
	 * @param editor
	 *            the editor
	 */
	public ProofTreeUIPage(UserSupport userSupport) {
		super();
		this.userSupport = userSupport;
		byUserSupport = false;
		userSupport.addStateChangedListeners(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPageBookViewPage#init(org.eclipse.ui.part.IPageSite)
	 */
	public void init(IPageSite pageSite) {
		super.init(pageSite);
		pageSite.setSelectionProvider(this);
	}

	public void createControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.setContentProvider(new ProofTreeUIContentProvider(this));
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.addSelectionChangedListener(this);
		Tree tree = viewer.getTree();
		tree.setHeaderVisible(false);
		elementColumn = new TreeColumn(tree, SWT.LEFT);
		// elementColumn.setResizable(true);
		// TODO Implement using ViewerFilter????
		// viewer.addFilter(new ViewerFilter() {
		//
		// /* (non-Javadoc)
		// * @see
		// org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
		// java.lang.Object, java.lang.Object)
		// */
		// @Override
		// public boolean select(Viewer viewer, Object parentElement, Object
		// element) {
		// if (UIUtils.DEBUG) System.out.println("Filter");
		// if (element instanceof IProofTreeNode) {
		// if (((IProofTreeNode) element).isOpen()) return true;
		// if (((IProofTreeNode) element).getRule().getName().equals("hyp"))
		// return false;
		// }
		// return true;
		// }
		//			
		// });
		if (fInput != null)
			viewer.setInput(fInput);
		if (root != null)
			viewer.setSelection(new StructuredSelection(root));
		elementColumn.pack();

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
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
				viewer.setExpandedElements(elements);
				elementColumn.pack();
				// UIUtils.debug("Width: " + elementColumn.getWidth());
				viewer.refresh();
				control.setRedraw(true);
			}
		}
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
	 * Get the list of filters.
	 * <p>
	 * 
	 * @return List of current filters.
	 */
	protected Object[] getFilters() {
		return filters;
	}

	/**
	 * Set the list of filters.
	 * <p>
	 * 
	 * @param filters
	 *            a list of filters
	 */
	protected void setFilters(Object[] filters) {
		this.filters = filters;
	}

	/**
	 * Fires a selection changed event.
	 * 
	 * @param selection
	 *            the new selection
	 */
	protected void fireSelectionChanged(ISelection selection) {
		// create an event
		final SelectionChangedEvent event = new SelectionChangedEvent(this,
				selection);

		// fire the event
		Object[] listeners = selectionChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
			Platform.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}
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
		selectionChangedListeners.add(listener);
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
		selectionChangedListeners.remove(listener);
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
		ISelection sel = event.getSelection();

		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) sel;
			if (!ssel.isEmpty()) {
				if (byUserSupport) { // Do nothing if the selection is from
					// UserSupport
					byUserSupport = false;
					return;
				}

				Object obj = ssel.getFirstElement();
				if (obj instanceof IProofTreeNode) {
					userSupport.selectNode((IProofTreeNode) obj);
				}
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
	 * @return the associated UserSupport
	 */
	public UserSupport getUserSupport() {
		return userSupport;
	}

	/**
	 * Select the root of the tree viewer. 
	 */
	public void selectRoot() {
		this.setSelection(new StructuredSelection(root));
	}


	public void proofStateChanged(IProofStateDelta delta) {
		UIUtils.debug("Proof Tree UI: State Changed");
		byUserSupport = true;
		final ProofState ps = delta.getProofState();
		final ProofTreeUIPage page = this;
		Display display = EventBUIPlugin.getDefault().getWorkbench()
				.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				page.setInput(ps.getProofTree());
				if (ps.getCurrentNode() != null)
					page.getViewer().setSelection(
							new StructuredSelection(ps.getCurrentNode()));
			}
		});		
	}

}