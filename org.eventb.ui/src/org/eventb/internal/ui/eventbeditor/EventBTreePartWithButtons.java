package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.rodinp.core.IRodinElement;

public abstract class EventBTreePartWithButtons
	extends EventBPartWithButtons
	implements IStatusChangedListener
{
	// The group of actions for the tree part.
	protected EventBMasterSectionActionGroup groupActionSet;
	
	public EventBTreePartWithButtons(final IManagedForm managedForm, Composite parent, FormToolkit toolkit, 
			int style, EventBEditor editor, String [] buttonLabels, String title, String description) {
		super(managedForm, parent, toolkit, style, editor, buttonLabels, title, description);
		makeActions();
		editor.addStatusListener(this);
	}
	
	@Override
	protected Viewer createViewer(IManagedForm managedForm, FormToolkit toolkit, Composite parent) {
		return createTreeViewer(managedForm, toolkit, parent);
	}

	/*
	 * Create the actions that can be used in the tree.
	 */
	private void makeActions() {
		groupActionSet = new EventBMasterSectionActionGroup(editor, (TreeViewer) this.getViewer());
	}
	
	abstract protected EventBEditableTreeViewer createTreeViewer(IManagedForm managedForm, FormToolkit toolkit, Composite parent);

	/**
	 * Set the selection in the table viewer.
	 * <p>
	 * @param element A Rodin element
	 */
	public void setSelection(IRodinElement element) {
		StructuredViewer viewer = (StructuredViewer) this.getViewer();
		viewer.setSelection(new StructuredSelection(element));
		edit(element);
	}

	protected void selectItem(TreeItem item, int column) {
		((EventBEditableTreeViewer) getViewer()).selectItem(item, column);
	}
	

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.IStatusChangedListener#statusChanged(java.util.Collection)
	 */
	public void statusChanged(IRodinElement element) {
		((EventBEditableTreeViewer) this.getViewer()).statusChanged(element);
		updateButtons();
	}
	

	/*
	 * Handle deletion of elements.
	 */
//	protected void handleDelete() {
//		IStructuredSelection ssel = (IStructuredSelection) ((StructuredViewer) this.getViewer()).getSelection();
//		
//		Object [] objects = ssel.toArray();
//		Collection<IInternalElement> toDelete = new HashSet<IInternalElement>();
//		for (int i = 0; i < objects.length; i++) {
//			if (objects[i] instanceof IInternalElement) {
//					toDelete.add((IInternalElement)objects[i]);
//			}
//		}
//		try {
//			EventBUIPlugin.getRodinDatabase().delete(toDelete.toArray(new IInternalElement[toDelete.size()]), true, null);
//			editor.editorDirtyStateChanged();
//		}
//		catch (RodinDBException e) {
//			e.printStackTrace();
//		}
//		return;
//	}


	
	/*
	 * Handle moving up.
	 */
//	protected void handleUp() {
//		Tree tree = ((TreeViewer) getViewer()).getTree();
//		TreeItem [] items = tree.getSelection();
//		TreeItem item = items[0];
//		TreeItem prev = TreeSupports.findPrevItem(tree, item);
//		IRodinElement currObj = ((Leaf) item.getData()).getElement();
//		IRodinElement prevObj = ((Leaf) prev.getData()).getElement();
//		
//		try {
//			swap(currObj, prevObj);
//			TreeItem newItem = TreeSupports.findItem(tree, currObj);
//			getViewer().setSelection(new StructuredSelection(newItem.getData()));
//		}
//		catch (RodinDBException e) {
//			e.printStackTrace();
//		}
//		return;
//	}
	
	
	/*
	 * Handle moving down.
	 *
	 */
//	protected void handleDown() {
//		Tree tree = ((TreeViewer) getViewer()).getTree();
//		TreeItem [] items = tree.getSelection();
//		TreeItem item = items[0];
//		TreeItem next = TreeSupports.findNextItem(tree, item);
//		IRodinElement currObj = ((Leaf) item.getData()).getElement();
//		IRodinElement nextObj = ((Leaf) next.getData()).getElement();
//		
//		try {
//			swap(nextObj, currObj);
//			TreeItem newItem = TreeSupports.findItem(tree, currObj);
//			getViewer().setSelection(new StructuredSelection(newItem.getData()));
//		}
//		catch (RodinDBException e) {
//			e.printStackTrace();
//		}
//		return;
//	}
	
	
	/**
	 * Swap Internal elements in the Rodin database
	 * @param element1 the object internal element
	 * @param element2 the expanded internal element
	 * @throws RodinDBException an exception from the database when moving element.
	 */
//	private void swap(IRodinElement element1, IRodinElement element2) throws RodinDBException {
//		((IInternalElement) element1).move(element1.getParent(), element2, null, true, null);
//	}
}
