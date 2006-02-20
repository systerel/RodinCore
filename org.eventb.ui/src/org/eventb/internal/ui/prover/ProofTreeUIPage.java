/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.internal.ui.prover;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIPlugin;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class ProofTreeUIPage
	extends Page 
	implements	IProofTreeUIPage,
				ISelectionChangedListener
{
	private ListenerList selectionChangedListeners = new ListenerList();

	private TreeViewer viewer;
	// The invisible root of the tree.
	
	private IProofTree invisibleRoot = null;
	private IProofTreeNode root = null;
	// TODO Change to Rule class?
	private Object [] filters = {"hyp", "allI"}; // Default filters 
		
	
	// The current editting element.
	private Object fInput;
	
	// The current associated editor.
	private ProverUI editor;
	

	// Group of action that is used.
	private ProofTreeUIActionGroup groupActionSet;
	
	class ViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			if (obj instanceof IProofTreeNode) {
				IProofTreeNode proofTree = (IProofTreeNode) obj;
				
				if (!proofTree.isOpen()) {
					return proofTree.getRule().getName() +" : " + proofTree.getSequent().goal();
				}
				else {
					return proofTree.getSequent().goal().toString();
				}
			}
			return obj.toString();
		}
		
		public Image getImage(Object obj) {
			ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		
			if (obj instanceof IProofTreeNode) {
				IProofTreeNode pt = (IProofTreeNode) obj;
				if (pt.isOpen()) return registry.get(EventBImage.IMG_UNDISCHARGED);
				if (!pt.isDischarged()) return registry.get(EventBImage.IMG_APPLIED);
				else return registry.get(EventBImage.IMG_DISCHARGED);
			}

			// TODO Removed?
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
	}
	
	/**
	 * Creates a content outline page using the given editor.
	 * Register as a change listener for the Rodin Database.
	 * <p> 
	 * @param editor the editor
	 */
	public ProofTreeUIPage(ProverUI editor) {
		super();
		this.editor= editor;
	}
	
	/*
     *  (non-Javadoc)
     * @see org.eclipse.ui.part.IPageBookViewPage#init(org.eclipse.ui.part.IPageSite)
     */
    public void init(IPageSite pageSite) {
        super.init(pageSite);
        pageSite.setSelectionProvider(this);
    }
        
    
    public void createControl(Composite parent) {
    	viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ProofTreeUIContentProvider(this));
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.addSelectionChangedListener(this);

		if (fInput != null) viewer.setInput(fInput);
		if (root != null) viewer.setSelection(new StructuredSelection(root));

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	/**
	 * Sets the input of the outline page
	 * <p>
	 * @param input the input of this outline page
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
				Object [] elements = viewer.getExpandedElements(); 
				viewer.setInput(fInput);
				viewer.refresh();
				viewer.setExpandedElements(elements);
				control.setRedraw(true);
			}
		}
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				groupActionSet.setContext(new ActionContext(viewer.getSelection()));
				groupActionSet.fillContextMenu(manager);
				groupActionSet.setContext(null);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
//		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = this.getSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
//		manager.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager manager) {
//				groupActionSet.setContext(new ActionContext(viewer.getSelection()));
//				groupActionSet.fillContextMenu(manager);
//				groupActionSet.setContext(null);
//			}
//		});
//		manager.add(groupActionSet.pruneAction);
//		manager.add(groupActionSet.conjIAction);
//		manager.add(groupActionSet.hypAction);
//		manager.add(groupActionSet.allIAction);
//		manager.add(groupActionSet.impIAction);
//		manager.add(groupActionSet.trivialAction);
		manager.add(groupActionSet.filterAction);
		manager.add(new Separator());
		manager.add(groupActionSet.nextPOAction);
		manager.add(groupActionSet.prevPOAction);
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		groupActionSet.drillDownAdapter.addNavigationActions(manager);
//		manager.add(groupActionSet.prevPOAction);
//		manager.add(groupActionSet.nextPOAction);
//		manager.add(new Separator());
	}

	private void makeActions() {
		groupActionSet = new ProofTreeUIActionGroup(this);

	}
	
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				groupActionSet.normAction.run();
			}
		});
	}
	
	protected void refresh(IProofTreeNode pt) {
		// TODO Refresh the parent of this proof tree	
		Object [] expands = viewer.getExpandedElements();
		viewer.refresh(true);
		viewer.setExpandedElements(expands);
		return;
	}
	
	protected void refresh() {
		viewer.refresh(true);
		return;
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public ProverUI getEditor() {return editor;}
	
	protected TreeViewer getViewer() {return viewer;}
	
	protected Object [] getFilters() {return filters;}
	
	protected void setFilters(Object [] filters) {this.filters = filters;}
	
	
	/**
	 * @author htson
	 * An extension of Dialog for choosing seen context. 
	 */
	/**
     * Fires a selection changed event.
     *
     * @param selection the new selection
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

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	@Override
	public Control getControl() {
        if (viewer == null)
            return null;
        return viewer.getControl();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
        if (viewer == null)
            return StructuredSelection.EMPTY;
        return viewer.getSelection();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		if (viewer != null)
			viewer.setSelection(selection);
	}

	/**
	 * Method declared on ContentOutlinePage.
	 * This is called when there is a selection change in the tree. This responses
	 * by selecting the first element of the selection in the editor.
	 * <p>
	 * @param event the selection event
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection sel = event.getSelection();

		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) sel;
			if (!ssel.isEmpty()) {
				Object obj = ssel.getFirstElement();
				if (obj instanceof IProofTreeNode) {
					editor.getUserSupport().selectNode((IProofTreeNode) obj);
				}
			}
			else {
				editor.getUserSupport().selectNode(null);
			}
		}
		
	}

	public void setInvisibleRoot(IProofTree pt) {this.invisibleRoot = pt;}
	
	public IProofTree getInvisibleRoot() {return invisibleRoot;}
	
	public void setRoot(IProofTreeNode pt) {this.root = pt;}
	
	public IProofTreeNode getRoot() {return root;}
}