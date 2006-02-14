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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eventb.core.prover.rules.ProofTree;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIPlugin;
import org.rodinp.core.IRodinElement;


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
		
	// The invisible root of the tree (should be the current editting file).
	private IProverSequent invisibleRoot = null;
	private ProofTree root = null;
	
	// Group of action that is used.
	private ProofTreeUIActionGroup groupActionSet;
	
	// TODO Change to Rule class?
//	private Collection<String> skipRules;
	private Object [] filters = {"hyp", "allI"}; // Default filters 
	
	/**
	 * @author htson
	 * <p>
	 * This is the content provider class for the tree display in 
	 * the outline page.
	 */
	class ViewContentProvider
		implements ITreeContentProvider
	{
		
		// When the input is change, reset the invisible root to null.
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (oldInput != null) System.out.println("oldInput " + oldInput + " class " + oldInput.getClass());
			if (newInput != null) System.out.println("newInput " + newInput + " class " + newInput.getClass());
			if (newInput instanceof IProverSequent) {
				invisibleRoot = null;
				root = null;
			}
			return;
		}
		
		
		// When the tree is dispose, do nothing.
		public void dispose() {
		}
		
		
		// Getting the list of elements, setting the invisible root if neccesary.
		public Object[] getElements(Object parent) {
			if (parent instanceof IProverSequent) {
				if (invisibleRoot == null) {
					invisibleRoot = (IProverSequent) parent;
					return getChildren(invisibleRoot);
				}
			}
			return getChildren(parent);
		}
		
		
		// Getting the parent of the an element.
		public Object getParent(Object child) {
			if (child instanceof IRodinElement) {
				return ((IRodinElement) child).getParent();
			}
			return null;
		}
		
		
		// Getting the list of children.
		public Object [] getChildren(Object parent) {
			if (parent.equals(invisibleRoot)) {
				if (root == null) {
					root = new ProofTree(invisibleRoot);
				}
				Object [] result = {root};
				return result;
			}
			if (parent instanceof ProofTree) {
				ProofTree pt = (ProofTree) parent;
				if (pt.rootHasChildren()) return getChildrenOfList(pt.getChildren());
				else return pt.getChildren();
			}
			
			return new Object[0];
		}
		
		
		
		public Object [] getChildrenOfList(ProofTree [] parents) {
			// TODO Should do it more efficiently using different data structure
//			System.out.println("*****");
			ArrayList<Object> children = new ArrayList<Object>();

			for (int i = 0; i < parents.length; i++) {
				ProofTree pt = parents[i];
				if (!pt.rootIsOpen()) {
					int j;
					for (j = 0; j < filters.length; j++) {
						if (filters[j].equals(pt.getRootRule().name())) {
							Object [] list = getChildrenOfList(pt.getChildren()); 
							for (int k = 0; k < list.length; k++) children.add(list[k]);
							break;
						}
					}
					if (j == filters.length) children.add(pt);
				}
				else children.add(pt);
			}
//			System.out.println("***** " + children +" ******");
			return children.toArray();
		}
		
		// Check if the element has children.
		public boolean hasChildren(Object parent) {
//			System.out.println("Has children " + parent);
			if (parent.equals(invisibleRoot)) return true;
//			System.out.println("Has children again " + parent);
			
			if (parent instanceof ProofTree) {
				return ((ProofTree) parent).rootHasChildren();
			}
			
//			System.out.println("No children");
			return false;
		}
	}
	
	class ViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			if (obj instanceof ProofTree) {
				ProofTree proofTree = (ProofTree) obj;
				
				if (!proofTree.rootIsOpen()) {
					return proofTree.getRootRule().name() +" : " + proofTree.getRootSeq().goal();
				}
				else {
					return proofTree.getRootSeq().goal().toString();
				}
			}
			return obj.toString();
		}
		
		public Image getImage(Object obj) {
			ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		
			if (obj instanceof ProofTree) {
				ProofTree pt = (ProofTree) obj;
				if (pt.rootIsOpen()) return registry.get(EventBImage.IMG_UNDISCHARGED);
				if (!pt.isClosed()) return registry.get(EventBImage.IMG_APPLIED);
				else return registry.get(EventBImage.IMG_DISCHARGED);
			}

			// TODO Removed?
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
	}
	
	
	// The current editting element.
	private Object fInput;
	
	// The current associated editor.
	private ProverUI fEditor;
	
	/**
	 * Creates a content outline page using the given editor.
	 * Register as a change listener for the Rodin Database.
	 * <p> 
	 * @param editor the editor
	 */
	public ProofTreeUIPage(ProverUI fEditor) {
		super();
		this.fEditor= fEditor;
//		skipRules = new HashSet<String>();
		//skipRules.add("hyp");
		//RodinCore.addElementChangedListener(this);
		this.addSelectionChangedListener(fEditor.getUserSupport());
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
		System.out.println("Creat Proof Tree UI");
    	viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.addSelectionChangedListener(this);
		
		if (fInput != null)
			viewer.setInput(fInput);
		viewer.setSelection(new StructuredSelection(root));

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
//	private void showMessage(String message) {
//		MessageDialog.openInformation(
//			viewer.getControl().getShell(),
//			"Proof Tree",
//			message);
//	}
//
	
	protected void refresh(ProofTree pt) {
		// TODO Refresh the parent of this proof tree	
//		if (!pt.rootIsOpen() && pt.isClosed()) {
			Object [] expands = viewer.getExpandedElements();
			viewer.refresh(root, true);
			viewer.setExpandedElements(expands);
//		}
//		else viewer.refresh(pt);
		return;
	}
	
	protected void refresh() {
		viewer.refresh(root);
		return;
	}
	
	protected void selectNextPendingSubgoal(ProofTree pt) {
		// TODO What is the next pending subgoal?
		List<ProofTree> subGoals = pt.pendingSubgoals();
		if (subGoals.size() != 0) {
			viewer.setSelection(new StructuredSelection(subGoals.get(0)));
		}
		else {
			subGoals = root.pendingSubgoals();
			if (subGoals != null && subGoals.size() != 0) {
				viewer.setSelection(new StructuredSelection(subGoals.get(0)));
			}
			else {
				viewer.setSelection(new StructuredSelection(root));
				Dialog dialog = new PenguinDanceDialog(EventBUIPlugin.getActiveWorkbenchShell());
				dialog.open();
			}
		}
	}

	protected void selectNextPendingSubgoal() {
		List<ProofTree> subGoals = root.pendingSubgoals();
		if (subGoals.size() != 0) {
			viewer.setSelection(new StructuredSelection(subGoals.get(0)));
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	protected ProverUI getEditor() {return fEditor;}
	
	protected TreeViewer getViewer() {return viewer;}
	
	protected Object [] getFilters() {return filters;}
	
	protected void setFilters(Object [] filters) {this.filters = filters;}
	
	
	/**
	 * @author htson
	 * An extension of Dialog for choosing seen context. 
	 */
	private class PenguinDanceDialog extends Dialog {
	    
		public PenguinDanceDialog(Shell parentShell) {
			super(parentShell);
		}

		/*
	     * (non-Javadoc)
	     * 
	     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	     */
	    protected void createButtonsForButtonBar(Composite parent) {
	        // create OK and Cancel buttons by default
	        Button ok = createButton(parent, IDialogConstants.OK_ID,
	                IDialogConstants.OK_LABEL, true);
	        ok.setFocus();
	    }

	    /*
	     * (non-Javadoc)
	     * 
	     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	     */
	    protected void configureShell(Shell shell) {
	        super.configureShell(shell);
	    }
	    
	    /*
	     * (non-Javadoc) Method declared on Dialog.
	     */
	    protected Control createDialogArea(Composite parent) {
	        // create composite
	        Composite composite = (Composite) super.createDialogArea(parent);
//	        composite.setLayoutData(gd);
			ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
			Image image = registry.get(EventBImage.IMG_PENGUIN);
	        
	        Browser browser = new Browser(composite, Window.getDefaultOrientation());
	        
	        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
	        gd.widthHint = image.getBounds().width + 20;
	        gd.heightHint = image.getBounds().height + 20;

	        browser.setText("<html><body><img align=\"center\" src=\"/home/htson/work/workspace/org.eventb.ui/icons/penguins-dancing.gif\" alt=\"Penguin tumbler\"></body></html>");
	        browser.setLayoutData(gd);	        
	        
	        applyDialogFont(composite);
	        return composite;
	    }

	    
	}


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

		ISelection selection = event.getSelection();
		
		if (!(selection.isEmpty())) {
			Object ssel = ((IStructuredSelection) selection).getFirstElement();
			fEditor.setSelection(ssel);
		}
	}
}