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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author htson
 * <p>
 * An abstract class contains a tree part with buttons
 * for displaying Rodin elements (used as master section in Master-Detail block).
 */
public abstract class EventBTreePartWithButtons
	extends SectionPart 
{
	// TODO Have a super class for both tree and table ???
	// TODO Move the buttons here/or the super class
	
	// The table viewer.
	private TreeViewer viewer;
	
	// Array of buttons.
	private Button [] buttons;

	// The master detail block contains this part.
	protected EventBMasterDetailsBlock block;
	
	// The Rodin File where the information belongs to.
	protected IRodinFile rodinFile;

	/**
	 * Constructor.
	 * <p>
	 * @param managedForm The form used to create the part
	 * @param parent The composite parent
	 * @param toolkit The Form Toolkit used to create the part
	 * @param style The style used to creat the part
	 * @param block The master-detail block contains this part
	 */
	public EventBTreePartWithButtons(IManagedForm managedForm, Composite parent, FormToolkit toolkit, 
			int style, EventBMasterDetailsBlock block, String [] buttonLabels) {
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
		
		createTreeViewer(managedForm, toolkit, client);
		
		createButtons(toolkit, client, buttonLabels);
	}
	
	/*
	 * Create the tree view part.
	 * <p>
	 * @param managedForm The Form used to create the viewer.
	 * @param toolkit The Form Toolkit used to create the viewer
	 * @param parent The composite parent
	 */
	private void createTreeViewer(final IManagedForm managedForm, FormToolkit toolkit, Composite parent) {
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		gd.widthHint = 100;
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getTree().setLayoutData(gd);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(EventBTreePartWithButtons.this, event.getSelection());
				//((EventBMachineEditor) EventBTreePartWithButtons.this.getBlock().getPage().getEditor()).getContentOutlinePage().setTreeSelection(event.getSelection());
				updateButtons();
			}
		});
		
		setViewerInput();
	}
	
	protected abstract void setViewerInput();
	
	protected abstract void updateButtons();
	

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
			EventBTreePartWithButtons.this.buttonSelected(index.intValue());
		}
	}
	

	/**
	 * Method to response to button selection.
	 * <p>
	 * @param index The index of selected button
	 */
	protected abstract void buttonSelected(int index);
	

	/**
	 * Set the selection in the table viewer.
	 * <p>
	 * @param element A Rodin element
	 */
	protected abstract void setSelection(IRodinElement element);
	

	/**
	 * Enable/Disable a button.
	 * <p>
	 * @param index Index of the button
	 * @param enabled <code>true</code> to enable, <code>false</code> to disable
	 */
	public void setButtonEnabled(int index, boolean enabled) {
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
	protected TreeViewer getViewer() {return viewer;}


	/**
	 * Return the Master-Detail block contains this part.
	 * <p>
	 * @return A Master-Detail block
	 */
	protected EventBMasterDetailsBlock getBlock() {return block;}
	

	/**
	 * Commiting the changes within the part.
	 */
	protected void commit() {
		this.markDirty();
		TreeViewer viewer = getViewer();
		Object [] objects = viewer.getExpandedElements();
		viewer.setInput(rodinFile);
		viewer.setExpandedElements(objects);
	}
	

}
