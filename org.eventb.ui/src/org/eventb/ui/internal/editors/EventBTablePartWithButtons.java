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

package org.eventb.ui.internal.editors;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * An abstract class contains a table part with buttons
 * for displaying Rodin elements (used as master section in Master-Detail block).
 */
public abstract class EventBTablePartWithButtons
	extends SectionPart 
{

	// The table viewer.
	private TableViewer viewer;

	// Array of buttons.
	private Button [] buttons;
	
	// The master detail block contains this part.
	protected EventBMasterDetailsBlock block;

	// The Rodin File where the information belongs to.
	protected IRodinFile rodinFile;
	
	// The indexes for different buttons.
	private static final int ADD_INDEX = 0;
	private static final int DELETE_INDEX = 1;
	private static final int UP_INDEX = 2;
	private static final int DOWN_INDEX = 3;
	
	// The counter used to create automatic name for new elements.
	protected int counter = 0;

	
	/**
	 * Constructor.
	 * <p>
	 * @param managedForm The form used to create the part
	 * @param parent The composite parent
	 * @param toolkit The Form Toolkit used to create the part
	 * @param style The style used to creat the part
	 * @param block The master-detail block contains this part
	 */
	public EventBTablePartWithButtons(IManagedForm managedForm, Composite parent, FormToolkit toolkit, 
			int style, EventBMasterDetailsBlock block) {
		super(parent, toolkit, style);
		this.block = block;
		
		Section section = this.getSection();
		Composite client = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		section.setClient(client);
		toolkit.paintBordersFor(client);
		
		createTableViewer(managedForm, toolkit, client);
		
		String [] buttonLabels = {"Add", "Delete", "Up", "Down"};
		createButtons(toolkit, client, buttonLabels);
	}
	
	
	/*
	 * Create the table view part.
	 * <p>
	 * @param managedForm The Form used to create the viewer.
	 * @param toolkit The Form Toolkit used to create the viewer
	 * @param parent The composite parent
	 */
	private void createTableViewer(final IManagedForm managedForm, FormToolkit toolkit, Composite parent) {
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		gd.widthHint = 100;
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getTable().setLayoutData(gd);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(EventBTablePartWithButtons.this, event.getSelection());
				updateButtons();
			}
		});
		
		setViewerInput();
	}
	
	
	/**
	 * Set the selection in the table viewer.
	 * <p>
	 * @param element A Rodin element
	 */
	public void setSelection(IRodinElement element) {
		TableViewer viewer = this.getViewer();
		viewer.setSelection(new StructuredSelection(element));
	}
	
	
	/*
	 * Creat the layout for buttons.
	 * <p>
	 * @return A new Grid Layout
	 */
	private GridLayout createButtonsLayout() {
		GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = 0;
		return layout;
	}
	
	
	/*
	 * Create buttons
	 * <p>
	 * @param toolkit The Form Toolkit used to create the buttons
	 * @param parent The composite parent
	 * @param buttonLabels The label of the buttons.
	 */
	private void createButtons(FormToolkit toolkit, Composite parent, String [] buttonLabels) {
		if (buttonLabels != null && buttonLabels.length > 0) {
			Composite buttonContainer = toolkit.createComposite(parent);
			GridData gd = new GridData(GridData.FILL_VERTICAL);
			buttonContainer.setLayoutData(gd);
			buttonContainer.setLayout(createButtonsLayout());
			buttons = new Button[buttonLabels.length];
			SelectionHandler listener = new SelectionHandler();
			for (int i = 0; i < buttonLabels.length; i++) {
				String label = buttonLabels[i];
				Button button = createButton(buttonContainer, label, i,
						toolkit);
				buttons[i] = button;
				button.addSelectionListener(listener);
			}
		}		
		updateButtons();
		return;
	}

	
	/*
	 * Create a (single) button.
	 * <p>
	 * @param parent The composite parent
	 * @param label The label of the button
	 * @param index The index of the button
	 * @param toolkit
	 * @return A new push button
	 */
	private Button createButton(Composite parent, String label, int index,
			FormToolkit toolkit) {
		Button button;
		if (toolkit != null)
			button = toolkit.createButton(parent, label, SWT.PUSH);
		else {
			button = new Button(parent, SWT.PUSH);
			button.setText(label);
		}
		GridData gd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		button.setLayoutData(gd);
		button.setData(new Integer(index));
		return button;
	}

	
	/**
	 * @author htson
	 * <p>
	 * A private class for selection listener of the buttons. 
	 */
	private class SelectionHandler implements SelectionListener {
		public void widgetSelected(SelectionEvent e) {
			buttonSelected(e);
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			buttonSelected(e);
		}
		private void buttonSelected(SelectionEvent e) {
			Integer index = (Integer) e.widget.getData();
			EventBTablePartWithButtons.this.buttonSelected(index.intValue());
		}
	}
	
	
	/**
	 * Enable/Disable a button.
	 * <p>
	 * @param index Index of the button
	 * @param enabled <code>true</code> to enable, <code>false</code> to disable
	 */
	private void setButtonEnabled(int index, boolean enabled) {
		if (buttons != null && index >= 0 && buttons.length > index) {
			Button b = buttons[index];
			b.setEnabled(enabled);
		}
	}
		
	
	/**
	 * Return the table viewer
	 * <p>
	 * @return a Table viewer
	 */
	protected TableViewer getViewer() {return viewer;}

	
	/**
	 * Return the Master-Detail block contains this part.
	 * <p>
	 * @return A Master-Detail block
	 */
	protected EventBMasterDetailsBlock getBlock() {return block;}
	

	/*
	 * Handle deletion of elements.
	 */
	private void handleDelete() {
		IStructuredSelection ssel = (IStructuredSelection) this.getViewer().getSelection();
		// TODO Batch the deleting jobs
		Object [] objects = ssel.toArray();
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] instanceof IInternalElement) {
				try {
					counter--;
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
	 * Handle moving up.
	 */
	private void handleUp() {
		Table table = this.getViewer().getTable();
		int index = table.getSelectionIndex();
		IInternalElement current = (IInternalElement) table.getItem(index).getData();
		IInternalElement previous = (IInternalElement) table.getItem(index - 1).getData();
		try {
			swap(current, previous);
			commit();
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
		Table table = this.getViewer().getTable();
		int index = table.getSelectionIndex();
		IInternalElement current = (IInternalElement) table.getItem(index).getData();
		IInternalElement next = (IInternalElement) table.getItem(index + 1).getData();
		try {
			swap(next, current);
			commit();
		}
		catch (RodinDBException e) {
			// TODO Exception handle
			e.printStackTrace();
		}
		return;
	}
	
	
	/**
	 * Swap Internal elements in the Rodin database
	 * @param element1 the first internal element
	 * @param element2 the second internal element
	 * @throws RodinDBException an exception from the database when moving element.
	 */
	private void swap(IInternalElement element1, IInternalElement element2) throws RodinDBException {
		element1.move(element1.getParent(), element2, null, true, null);
	}
	
	
	/**
	 * Update the status of buttons.
	 */
	protected void updateButtons() {
		Table table = this.getViewer().getTable();
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
	 * Commiting the changes within the part.
	 */
	public void commit() {
		this.getViewer().setInput(rodinFile);
		this.getViewer().refresh();
		updateButtons();
		this.markDirty();
		((EventBFormPage) block.getPage()).notifyChangeListeners();
	}
	

	/**
	 * Handle add (new element) action
	 *
	 */
	protected abstract void handleAdd();
	
	
	/**
	 * Setting the input for the (table) viewer.
	 */
	protected abstract void setViewerInput();

}