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

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachine;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * An implementation of the Event-B Table part with buttons
 * for displaying constants (used as master section in Master-Detail block).
 */
public class EventMasterSection 
	extends EventBTreePartWithButtons
{
	// The indexes for different buttons.
	private static final int ADD_INDEX = 0;
	private static final int DELETE_INDEX = 1;
	private static final int UP_INDEX = 2;
	private static final int DOWN_INDEX = 3;
	private static final int ADD_INIT_INDEX = 4;

	// The counter used to create automatic name for new elements.
	private int counter;

	// The group of actions for the tree part.
	private ActionGroup groupActionSet;
	

	/**
	 * The content provider class. 
	 */
	class MasterContentProvider
	implements IStructuredContentProvider, ITreeContentProvider
	{
		private IMachine invisibleRoot = null;
		
		public Object getParent(Object child) {
			if (child instanceof IRodinElement) return ((IRodinElement) child).getParent();
			return null;
		}
		
		public Object[] getChildren(Object parent) {
			if (parent instanceof IMachine) {
				try {
					return ((IMachine) parent).getEvents();
				}
				catch (RodinDBException e) {
					// TODO Exception handle
					e.printStackTrace();
				}
			}
			if (parent instanceof IParent) {
				try {
					return ((IParent) parent).getChildren();
				}
				catch (RodinDBException e) {
					// TODO Exception handle
					e.printStackTrace();
				}
			}
			return new Object[0];
		}
		public boolean hasChildren(Object parent) {
			return getChildren(parent).length > 0;
		}
		
		public Object[] getElements(Object parent) {
			if (parent instanceof IRodinFile) {
				if (invisibleRoot == null) {
					invisibleRoot = (IMachine) parent;
					return getChildren(invisibleRoot);
				}
			}
			return getChildren(parent);
		}
		
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			invisibleRoot = null;
		}
	}
	
	
	/**
	 * @author htson
	 * This class provides the label for different elements in the tree.
	 */
	class MasterLabelProvider extends LabelProvider {
		public String getText(Object obj) {
			if (obj instanceof IAction) {
				try {
					return ((IAction) obj).getContents();
				}
				catch (RodinDBException e) {
					// TODO Handle Exception
					e.printStackTrace();
					return "";
				}
			}
			if (obj instanceof IInternalElement) return ((IInternalElement) obj).getElementName();
			return obj.toString();
		}
		public Image getImage(Object obj) {
			return UIUtils.getImage(obj);
		}
	}
	
	
	/**
	 * @author htson
	 * This class sorts the elements by types.
	 */
	private class ElementsSorter extends ViewerSorter {
		
		public int compare(Viewer viewer, Object e1, Object e2) {
	        int cat1 = category(e1);
	        int cat2 = category(e2);
	        return cat1 - cat2;
		}
		
		public int category(Object element) {
			if (element instanceof IVariable) return 1;
			if (element instanceof IGuard) return 2;
			if (element instanceof IAction) return 3;
			
			return 0;
		}
	}
	

	/**
	 * Contructor.
	 * <p>
	 * @param managedForm The form to create this master section
	 * @param parent The composite parent
	 * @param toolkit The Form Toolkit used to create this master section
	 * @param style The style
	 * @param block The master detail block which this master section belong to
	 */
	public EventMasterSection(IManagedForm managedForm, Composite parent, FormToolkit toolkit, 
			int style, EventBMasterDetailsBlock block) {
		super(managedForm, parent, toolkit, style, block);
		try {
			counter = ((IMachine) rodinFile).getEvents().length; // Set the counter;
		}
		catch (RodinDBException e) {
			// TODO Exception handle
			e.printStackTrace();
		}	
		makeActions();
		hookContextMenu();
		getViewer().setSorter(new ElementsSorter());
	}
	
	
	/*
	 * Create the actions that can be used in the tree.
	 */
	private void makeActions() {
		groupActionSet = new EventMasterSectionActionGroup(this);
	}
	
	
	/**
	 * Hook the actions to the menu
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				groupActionSet.setContext(new ActionContext(getViewer().getSelection()));
				groupActionSet.fillContextMenu(manager);
				groupActionSet.setContext(null);
			}
		});
		Viewer viewer = getViewer();
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		this.getBlock().getPage().getSite().registerContextMenu(menuMgr, viewer);
	}

	
	/*
	 * Handle add (new element) action.
	 */
	private void handleAdd() {
		try {
			IInternalElement event = rodinFile.createInternalElement(IEvent.ELEMENT_TYPE, "evt" + counter, null, null);
			counter++;
			commit();
			getViewer().setSelection(new StructuredSelection(event));
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Handle add (new element) action.
	 */
	private void handleAddInit() {
		try {
			IInternalElement event = rodinFile.createInternalElement(IEvent.ELEMENT_TYPE, "INITIALISATION", null, null);
			counter++;
			commit();
			getViewer().setSelection(new StructuredSelection(event));
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Handle delete action.
	 */
	private void handleDelete() {
		this.markDirty();
		IStructuredSelection ssel = (IStructuredSelection) this.getViewer().getSelection();
		//TODO Batch the deleted job
		Object [] objects = ssel.toArray();
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] instanceof IInternalElement) {
				try {
					System.out.println("DELETE " + objects[i].toString());
					((IInternalElement) objects[i]).delete(true, null);
					commit();
				}
				catch (RodinDBException e) {
					e.printStackTrace();
				}
			}
		}
		return;
	}
		
	
	/*
	 * Handle up action.
	 */
	private void handleUp() {
		System.out.println("Up: To be implemented");
		return;
	}
	
	
	/*
	 * Handle down action. 
	 */
	private void handleDown() {
		System.out.println("Down: To be implemented");
		return;
	}
	
	
	/**
	 * Update the status of buttons.
	 */
	protected void updateButtons() {
		ISelection sel = getViewer().getSelection();
		Object [] selections = ((IStructuredSelection) sel).toArray();
		
		boolean hasOneSelection = selections.length == 1;
		
		boolean hasSelection = selections.length > 0;
		
		setButtonEnabled(ADD_INDEX, true);
		setButtonEnabled(DELETE_INDEX, hasSelection);
		setButtonEnabled(UP_INDEX, hasOneSelection);
		setButtonEnabled(DOWN_INDEX, hasOneSelection);
		setButtonEnabled(ADD_INIT_INDEX, true);
	}
	

	/**
	 * Method to response to button selection.
	 * <p>
	 * @param index The index of selected button
	 */
	protected void buttonSelected(int index) {
		switch (index) {
			case ADD_INDEX:
				handleAdd();
				break;
			case DELETE_INDEX:
				handleDelete();
				break;
			case UP_INDEX:
				handleUp();
				break;
			case DOWN_INDEX:
				handleDown();
				break;
			case ADD_INIT_INDEX:
				handleAddInit();
				break;
		}
	}
	

	/**
	 * Setting the input for the (table) viewer.
	 */
	protected void setViewerInput() {
		TreeViewer viewer = this.getViewer();
		viewer.setContentProvider(new MasterContentProvider());
		viewer.setLabelProvider(new MasterLabelProvider());
		rodinFile = ((EventBEditor) this.getBlock().getPage().getEditor()).getRodinInput();
		viewer.setInput(rodinFile);
	}


	/**
	 * Set the selection in the tree viewer.
	 * <p>
	 * @param element A Rodin element
	 */
	public void setSelection(IRodinElement element) {
		TreeViewer viewer = this.getViewer();
		viewer.setSelection(new StructuredSelection(element));
		//EventBEditor editor = (EventBEditor) getBlock().getPage().getEditor();
		//editor.getContentOutlinePage().setRodinElementSelection(element);
	}

}
