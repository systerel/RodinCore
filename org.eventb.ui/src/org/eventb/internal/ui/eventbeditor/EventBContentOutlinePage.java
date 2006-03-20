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

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.UIUtils.ElementLabelProvider;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * A content outline page which always represents the content of the
 * connected editor (machines, models, etc.) in segments.
 */
public class EventBContentOutlinePage
	extends ContentOutlinePage
	implements IElementChangedListener
{

	/**
	 * @author htson
	 * <p>
	 * This is the content provider class for the tree display in 
	 * the outline page.
	 */
	class ViewContentProvider
		implements ITreeContentProvider
	{
		// The invisible root of the tree (should be the current editting file).
		private IRodinFile invisibleRoot = null;
		
		
		// When the input is change, reset the invisible root to null.
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			invisibleRoot = null;
			return;
		}
		
		
		// When the tree is dispose, do nothing.
		public void dispose() {
		}
		
		
		// Getting the list of elements, setting the invisible root if neccesary.
		public Object[] getElements(Object parent) {
			if (parent instanceof IRodinFile) {
				if (invisibleRoot == null) 
					invisibleRoot = fEditor.getRodinInput();
				return getChildren(invisibleRoot);
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
			if (parent instanceof IParent) {
				try {
					return ((IParent) parent).getChildren();
				}
				catch (RodinDBException e) {
					e.printStackTrace();
					EventBMachineEditorContributor.sampleAction.refreshAll();
					MessageDialog.openError(null, "Error",
							"Cannot get children of " + parent);
				}
			}
			return new Object[0];
		}
		
		
		// Check if the element has children.
		public boolean hasChildren(Object parent) {
			if (parent instanceof IParent)
				try {
					return ((IParent) parent).hasChildren();
				}
				catch (RodinDBException e) {
					MessageDialog.openError(null, "Error",
							"Cannot check hasChildren of " + parent);
					e.printStackTrace();
				}
			return false;
		}
	}


	/**
	 * @author htson
	 * <p>
	 * This class sorts the RODIN elements by types. 
	 */
	private class ElementsSorter extends ViewerSorter {
		
		public int compare(Viewer viewer, Object e1, Object e2) {
	        int cat1 = category(e1);
	        int cat2 = category(e2);
	        return cat1 - cat2;
		}
		
		public int category(Object element) {
			if (element instanceof IVariable) return 1;
			if (element instanceof IInvariant) return 2;
			if (element instanceof ITheorem) return 4;
			if (element instanceof IEvent) return 5;
			if (element instanceof ICarrierSet) return 1;
			if (element instanceof IConstant) return 2;
			if (element instanceof IAxiom) return 3;
			if (element instanceof IGuard) return 2;
			if (element instanceof IAction) return 3;
			
			return 0;
		}
	}
	
	
	// The current editting element.
	private Object fInput;
	
	// The current associated editor.
	private EventBEditor fEditor;

	
	/**
	 * Creates a content outline page using the given editor.
	 * Register as a change listener for the Rodin Database.
	 * <p> 
	 * @param editor the editor
	 */
	public EventBContentOutlinePage(EventBEditor editor) {
		super();
		fEditor= editor;
		RodinCore.addElementChangedListener(this);
	}
	
	
	/**
	 * Method declared on ContentOutlinePage.
	 * Create the tree content of the page. 
	 * <p>
	 * @param parent the parent composite of the control
	 */
	public void createControl(Composite parent) {

		super.createControl(parent);

		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setSorter(new ElementsSorter());
		viewer.setLabelProvider(new ElementLabelProvider());
		viewer.addSelectionChangedListener(this);

		if (fInput != null)
			viewer.setInput(fInput);
	}
	

	/**
	 * Method declared on ContentOutlinePage.
	 * This is called when there is a selection change in the tree. This responses 
	 * by selecting the object element of the selection in the editor.
	 * <p>
	 * @param event the selection event
	 */
	public void selectionChanged(SelectionChangedEvent event) {

		ISelection selection = event.getSelection();
		if (!(selection.isEmpty())) {
			Object ssel = ((IStructuredSelection) selection).getFirstElement();
			fEditor.setSelection(ssel);
		}

		super.selectionChanged(event);
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
	public void update() {
		TreeViewer viewer= getTreeViewer();
		if (viewer != null) {
			Control control = viewer.getControl();
			if (control != null && !control.isDisposed()) {
				control.setRedraw(false);

				// Saving the expanded elements
				Object [] elements = viewer.getExpandedElements(); 
				viewer.setInput(fInput);
				viewer.setExpandedElements(elements);
				control.setRedraw(true);
			}
		}
	}

	
	
//	public void setTreeSelection(ISelection sel) {
//		StructuredSelection ssel = (StructuredSelection) sel;
//		if (ssel.isEmpty()) return;
//		
//		IRodinElement lsel = (IRodinElement) ssel.getFirstElement();    // Set the object element only
//		
//		this.setRodinElementSelection(lsel);
//		
//		return;
//	}
	
//	public void setRodinElementSelection(IRodinElement element) {	
//		this.removeSelectionChangedListener(this);
//		
//		ISelection current = this.getTreeViewer().getSelection();
//		StructuredSelection ssel = new StructuredSelection(element);
//		if (!ssel.equals(current)) // Stop looping through selection changes
//			this.setSelection(ssel); // Does work now :-)
//		
//		
//		this.addSelectionChangedListener(this);
//		return;
//	}
	
//	public void refresh(Object node) {
//		getTreeViewer().refresh(node, true);
//	}
	
	
	
	/**
	 * This method implements the listener method when there is a change in
	 * the Rodin database
	 * <p>  
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		// TODO Process the delta increamentally, see ObligationExplorerContentProvider
		//if (UIUtils.DEBUG) System.out.println("Update the Explorer " + event.toString());
		IRodinElementDelta [] elements = event.getDelta().getAffectedChildren();
		for (int i = 0; i < elements.length; i++) {
			//if (UIUtils.DEBUG) System.out.println(elements[i].toString() + " which is " + elements[i].getKind() + " contains " + elements[i].getElement());
			if ((elements[i].getKind() & IRodinElementDelta.ADDED) != 0) {
				//if (UIUtils.DEBUG) System.out.println("Refresh add " + elements[i].getElement());
				//refresh(elements[i].getElement().getParent());
				//viewer.refresh(false);
				//viewer.setInput(EventBUIPlugin.getRodinDatabase());
				//viewer.refresh();
				//if (UIUtils.DEBUG) System.out.println("******* Finish Updating *****");
			}
			else if ((elements[i].getKind() & IRodinElementDelta.REMOVED) != 0) {
				//if (UIUtils.DEBUG) System.out.println("Refresh delete " + elements[i].getElement());
				//refresh(elements[i].getElement().getParent());
				//viewer.refresh(false);
				//viewer.setInput(EventBUIPlugin.getRodinDatabase());
				//viewer.refresh();
			}
			else if ((elements[i].getKind() & IRodinElementDelta.CHANGED) != 0) {
				//if (UIUtils.DEBUG) System.out.println("Refresh change " + elements[i].getElement());
				//refresh(elements[i].getElement().getParent());
				//viewer.refresh(false);
				//viewer.setInput(EventBUIPlugin.getRodinDatabase());
				//viewer.refresh();
			}
			Display display = EventBUIPlugin.getDefault().getWorkbench().getDisplay();
			display.asyncExec(new Runnable() {
				/* (non-Javadoc)
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					update();
				}
			});
			//if (UIUtils.DEBUG) System.out.println("********** Finish changing **********");
		}
	}
}
