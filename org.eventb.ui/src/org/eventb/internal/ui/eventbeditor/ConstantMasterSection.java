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

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.IConstant;
import org.eventb.core.IContext;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IUnnamedInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * An implementation of the Event-B Table part with buttons
 * for displaying constants (used as master section in Master-Detail block).
 */
public class ConstantMasterSection
extends EventBTablePartWithButtons
{
	
	// The indexes for different buttons.
	private static final int ADD_INDEX = 0;
	private static final int DELETE_INDEX = 1;
	private static final int UP_INDEX = 2;
	private static final int DOWN_INDEX = 3;
	
	private static final String [] buttonLabels = {"Add", "Delete", "Up", "Down"};
	private static final String SECTION_TITLE = "Constants";
	private static final String SECTION_DESCRIPTION = "List of constants of the component"; 
	
	/**
	 * The content provider class. 
	 */
	class ConstantContentProvider
	implements IStructuredContentProvider {
		public Object[] getElements(Object parent) {
			if (parent instanceof IContext)
				try {
					return ((IContext) parent).getChildrenOfType(IConstant.ELEMENT_TYPE);
				}
				catch (RodinDBException e) {
					// TODO Exception handle
					e.printStackTrace();
				}
			return new Object[0];
		}
    	
    	public void dispose() {return;}

    	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    		return;
    	}
    }
	
	class ConstantLabelProvider 
		implements  ITableLabelProvider, ITableFontProvider, ITableColorProvider
	{
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex != 0) return null;
			return UIUtils.getImage(element);
		}
	
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			if (columnIndex == 0) {
				try {
					if (element instanceof IUnnamedInternalElement) return ((IUnnamedInternalElement) element).getContents();
				}
				catch (RodinDBException e) {
					e.printStackTrace();
				}
				if (element instanceof IInternalElement) return ((IInternalElement) element).getElementName();
				else return element.toString();
			}
			return element.toString();
		}
	
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}
	
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose() {
			// TODO Auto-generated method stub
			
		}
	
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
		 */
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}
	
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}
	
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
		 */
		public Color getBackground(Object element, int columnIndex) {
			 Display display = Display.getCurrent();
	         return display.getSystemColor(SWT.COLOR_WHITE);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
		 */
		public Color getForeground(Object element, int columnIndex) {
			Display display = Display.getCurrent();
	        return display.getSystemColor(SWT.COLOR_BLACK);
	   }
	
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableFontProvider#getFont(java.lang.Object, int)
		 */
		public Font getFont(Object element, int columnIndex) {
			return JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
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
	public ConstantMasterSection(IManagedForm managedForm, Composite parent, FormToolkit toolkit, 
			int style, EventBEditor editor) {
		super(managedForm, parent, toolkit, style, editor, buttonLabels, SECTION_TITLE, SECTION_DESCRIPTION);
	}

	

	/**
	 * Setting the input for the (table) viewer.
	 */
	protected void setProvider() {
		TableViewer viewer = (TableViewer) this.getViewer();
		viewer.setContentProvider(new ConstantContentProvider());
		viewer.setLabelProvider(new ConstantLabelProvider());
	}
	
	
	/*
	 * Create the table view part.
	 * <p>
	 * @param managedForm The Form used to create the viewer.
	 * @param toolkit The Form Toolkit used to create the viewer
	 * @param parent The composite parent
	 */
	protected EventBEditableTableViewer createTableViewer(IManagedForm managedForm, FormToolkit toolkit, Composite parent) {
		return new ConstantEditableTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION, editor.getRodinInput());
	}
	
	/**
	 * Update the expanded of buttons.
	 */
	protected void updateButtons() {
		Table table = ((TableViewer) getViewer()).getTable();
		boolean hasOneSelection = table.getSelection().length == 1;
		boolean hasSelection = table.getSelection().length > 0;
		boolean canMove = table.getItemCount() > 1;
		
        setButtonEnabled(
			UP_INDEX,
			canMove && hasOneSelection && table.getSelectionIndex() > 0);
		setButtonEnabled(
			DOWN_INDEX,
			canMove
				&& hasOneSelection
				&& table.getSelectionIndex() < table.getItemCount() - 1);
    		
		setButtonEnabled(ADD_INDEX, true);
		setButtonEnabled(DELETE_INDEX, hasSelection);
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
		}
	}
	
	/**
	 * Handle the adding (new Variable) action.
	 */
	protected void handleAdd() {
		IRodinFile rodinFile = editor.getRodinInput();
		try {
			int counter = rodinFile.getChildrenOfType(IConstant.ELEMENT_TYPE).length;
			IInternalElement element = rodinFile.createInternalElement(IConstant.ELEMENT_TYPE, "cst"+(counter+1), null, null);
			editor.editorDirtyStateChanged();
			TableViewer viewer = (TableViewer) this.getViewer();
			viewer.refresh();
			viewer.reveal(element);
			Table table = viewer.getTable();
			selectRow(table.getItemCount() - 1, 0);
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}


	/*
	 * Handle deletion of elements.
	 */
	private void handleDelete() {
		IStructuredSelection ssel = (IStructuredSelection) ((StructuredViewer) this.getViewer()).getSelection();
		
		Object [] objects = ssel.toArray();
		Collection<IInternalElement> toDelete = new HashSet<IInternalElement>();
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] instanceof IInternalElement) {
					toDelete.add((IInternalElement)objects[i]);
			}
		}
		try {
			EventBUIPlugin.getRodinDatabase().delete(toDelete.toArray(new IInternalElement[toDelete.size()]), true, null);
			editor.editorDirtyStateChanged();
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
		return;
	}


	/*
	 * Handle moving up.
	 */
	private void handleUp() {
		Table table = ((TableViewer) this.getViewer()).getTable();
		int index = table.getSelectionIndex();
		IInternalElement current = (IInternalElement) table.getItem(index).getData();
		IInternalElement previous = (IInternalElement) table.getItem(index - 1).getData();
		try {
			swap(current, previous);
			editor.editorDirtyStateChanged();
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
		return;
	}
	
	
	/*
	 * Handle moving down.
	 *
	 */
	private void handleDown() {
		Table table = ((TableViewer) this.getViewer()).getTable();
		int index = table.getSelectionIndex();
		IInternalElement current = (IInternalElement) table.getItem(index).getData();
		IInternalElement next = (IInternalElement) table.getItem(index + 1).getData();
		try {
			swap(next, current);
			editor.editorDirtyStateChanged();
		}
		catch (RodinDBException e) {
			// TODO Exception handle
			e.printStackTrace();
		}
		return;
	}
	
	
	/**
	 * Swap Internal elements in the Rodin database
	 * @param element1 the object internal element
	 * @param element2 the expanded internal element
	 * @throws RodinDBException an exception from the database when moving element.
	 */
	private void swap(IInternalElement element1, IInternalElement element2) throws RodinDBException {
		element1.move(element1.getParent(), element2, null, true, null);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		IRodinElementDelta delta = event.getDelta();
		processDelta(delta);
	}
	
	private void processDelta(IRodinElementDelta delta) {
		IRodinElement element= delta.getElement();
		if (element instanceof IRodinFile) {
			IRodinElementDelta [] deltas = delta.getAffectedChildren();
			for (int i = 0; i < deltas.length; i++) {
				processDelta(deltas[i]);
			}

			return;
		}
		if (element instanceof IConstant) {
			UIUtils.postRunnable(new Runnable() {
				public void run() {
					getViewer().setInput(editor.getRodinInput());
					updateButtons();
				}
			}, this.getSection().getClient());
		}
		else {
			return;
		}
	}
	

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.EventBTablePartWithButtons#edit(org.rodinp.core.IRodinElement)
	 */
	@Override
	protected void edit(IRodinElement element) {
		TableViewer viewer = (TableViewer) this.getViewer();
		viewer.reveal(element);
		Table table = viewer.getTable();
		TableItem item  = (TableItem) viewer.testFindItem(element);
		int row = table.indexOf(item);
		selectRow(row, 0);
	}

}
