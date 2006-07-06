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

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
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
import org.eclipse.swt.layout.GridData;
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
import org.eventb.core.prover.IProofRule;
import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.IProofTreeDelta;
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

	// The contained tree viewer.
	private TreeViewer viewer;

	// private Text comments;

	// The invisible root of the tree.
	private IProofTree invisibleRoot = null;

	private IProofTreeNode root = null;

	// TODO Change to Rule class?
	private Object[] filters = { "allI" }; // Default filters

	private TreeColumn elementColumn;

	// The current editting element.
	private Object fInput;

	// private Combo confidentLevel;

	private UserSupport userSupport;

	// Group of action that is used.
	private ProofTreeUIActionGroup groupActionSet;

	/**
	 * @author htson
	 *         <p>
	 *         This class provides the labels for elements in the tree viewer.
	 */
	class ProofTreeLabelProvider implements ITableLabelProvider,
			ITableFontProvider, ITableColorProvider, IPropertyChangeListener {

		private Font font = null;

		public ProofTreeLabelProvider() {
			// Register as a listener to the font registry
			JFaceResources.getFontRegistry().addListener(this);
		}

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
				int confidence = pt.getConfidence();
				if (confidence <= IProofRule.CONFIDENCE_REVIEWED)
					return registry.get(EventBImage.IMG_REVIEWED);
				if (confidence <= IProofRule.CONFIDENCE_DISCHARGED)
					return registry.get(EventBImage.IMG_DISCHARGED);
				return registry.get(EventBImage.IMG_DEFAULT);
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
					return proofTree.getRule().getDisplayName() + " : "
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
			if (font == null) {
				font = JFaceResources
						.getFont(PreferenceConstants.EVENTB_MATH_FONT);
			}
			return font;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.runtime.Preferences$IPropertyChangeListener#propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent event) {
			font = JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
			ProofTreeUIPage.this.getViewer().refresh();
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
		// byUserSupport = false;
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
		// Composite comp = new Composite(parent, SWT.DEFAULT);
		// GridLayout gl = new GridLayout();
		// gl.numColumns = 1;
		// comp.setLayout(gl);
		// GridData ld = new GridData(GridData.FILL_BOTH);
		// comp.setLayoutData(ld);
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

		elementColumn = new TreeColumn(tree, SWT.LEFT);
		ProofTreeUIToolTip handler = new ProofTreeUIToolTip(viewer.getControl()
				.getShell(), userSupport);
		handler.activateHoverHelp(viewer.getControl());
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
		// Label label = new Label(parent, SWT.CENTER | SWT.WRAP);
		// label.setText("Confident Level: ");
		// gd = new GridData();
		// // gd.heightHint = 50;
		// // gd.widthHint = 100;
		// gd.horizontalAlignment = SWT.CENTER;
		// label.setLayoutData(gd);
		// label.pack();

		// confidentLevel = new Combo(parent, SWT.DEFAULT);
		// for (int i = 0; i <= 10; i++)
		// confidentLevel.add(i+"");
		// gd = new GridData();
		// confidentLevel.setLayoutData(gd);

		// comments = new Text(comp, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		// gd = new GridData(GridData.FILL_HORIZONTAL);
		// gd.heightHint = 150;
		// gd.horizontalSpan = 1;
		// comments.setLayoutData(gd);
		//
		// comments.addFocusListener(new FocusListener() {
		//
		// public void focusGained(FocusEvent e) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// public void focusLost(FocusEvent e) {
		// ISelection sel = viewer.getSelection();
		// UIUtils.debugProverUI("Selection: " + sel);
		// if (sel instanceof IStructuredSelection) {
		// IStructuredSelection ssel = (IStructuredSelection) sel;
		// if (ssel.size() == 1) {
		// IProofTreeNode node = (IProofTreeNode) ssel
		// .getFirstElement();
		// UIUtils.debugProverUI("Node: "
		// + node.getSequent().toString());
		// userSupport.setComment(comments.getText());
		// }
		// }
		// }
		//
		// });
		//

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		if (fInput != null)
			update();
		elementColumn.pack();

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
				viewer.setSelection(new StructuredSelection(userSupport
						.getCurrentPO().getCurrentNode()));
				control.setRedraw(true);
			}
		}
	}

	@Override
	public void dispose() {
		// comments.dispose();
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
		UIUtils.debugProverUI("Selection Changed: " + event);
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
					userSupport.selectNode((IProofTreeNode) obj);
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
	public UserSupport getUserSupport() {
		return userSupport;
	}

	/**
	 * Select the root of the tree viewer.
	 */
	public void selectRoot() {
		this.setSelection(new StructuredSelection(root));
	}

	public void proofStateChanged(final IProofStateDelta delta) {
		// byUserSupport = true;
		final ProofState ps = delta.getNewProofState();
		Display display = EventBUIPlugin.getDefault().getWorkbench()
		.getDisplay();
		UIUtils.debugProverUI("Proof Tree UI: State Changed: " + ps);
		if (ps != null) { // Change only when change the PO
			final ProofTreeUIPage page = this;
			display.syncExec(new Runnable() {
				public void run() {
					page.setInput(ps.getProofTree());
					IProofTreeNode currentNode = ps.getCurrentNode();
					UIUtils.debugProverUI("Current node: "
							+ currentNode.getSequent());
					page.getViewer().expandAll();
					if (currentNode != null)
						page.getViewer().setSelection(
								new StructuredSelection(currentNode));
				}
			});
		} else {
			IProofTreeDelta proofTreeDelta = delta.getProofTreeDelta();
			UIUtils.debugProverUI("Proof Tree UI: " + proofTreeDelta);
			if (proofTreeDelta != null) {
				display.syncExec(new Runnable() {

					public void run() {
						viewer.refresh();

						IProofTreeNode node = delta.getNewProofTreeNode();
						if (node != null) {
							viewer.setSelection(new StructuredSelection(node), true);
						}
					}
					
				});
				
			}

		}
	}

}