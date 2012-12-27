/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - introduced read only elements
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import static org.eventb.internal.ui.eventbeditor.EventBEditorUtils.checkAndShowReadOnly;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
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
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         This abstract class extends SectionPart class and provides a part
 *         (either tree or table viewer) with buttons on the right-hand side.
 */
public abstract class EventBPartWithButtons extends SectionPart implements
		IElementChangedListener {

	// Array of buttons.
	private Button[] buttons;

	// The associated Event-B Editor
	protected IEventBEditor<?> editor;

	// A viewer.
	private Viewer viewer;

	/**
	 * Method to response to button selection.
	 * <p>
	 * 
	 * @param index
	 *            The index of selected button
	 */
	protected abstract void buttonSelected(int index);

	/**
	 * Create the viewer (either table or tree viewer).
	 * <p>
	 * 
	 * @param managedForm
	 *            The managed form to create the viewer
	 * @param toolkit
	 *            The FormToolkit used to create the viewer
	 * @param parent
	 *            The composite parent of the viewer
	 * @return A new created tree or table viewer
	 */
	protected abstract Viewer createViewer(IManagedForm managedForm,
			FormToolkit toolkit, Composite parent);

	/**
	 * Update the enable/disable status of buttons.
	 */
	protected abstract void updateButtons();

	/**
	 * Attemp to edit a particular element in the viewer.
	 * <p>
	 * 
	 * @param element
	 *            a Rodin Element
	 */
	protected abstract void edit(IRodinElement element);

	/**
	 * Select the particular element in the viewer.
	 * <p>
	 * 
	 * @param element
	 */
	public abstract void setSelection(IRodinElement element);

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param managedForm
	 *            The managed form used to create the Section
	 * @param parent
	 *            The composite parent of the section
	 * @param toolkit
	 *            The FormToolkit used to create the Section
	 * @param style
	 *            The style used to create the Section
	 * @param editor
	 *            The associated Event-B Editor
	 * @param buttonLabels
	 *            The labels of the buttons
	 * @param title
	 *            The title of the Section
	 * @param description
	 *            The description of the Section
	 */
	public EventBPartWithButtons(final IManagedForm managedForm,
			Composite parent, FormToolkit toolkit, int style,
			IEventBEditor<?> editor, String[] buttonLabels, String title,
			String description) {
		super(parent, toolkit, style);
		this.editor = editor;
		Section section = this.getSection();
		section.setText(title);
		section.setDescription(description);

		Composite client = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		section.setClient(client);
		toolkit.paintBordersFor(client);

		viewer = createViewer(managedForm, toolkit, client);
		viewer.setInput(editor.getRodinInput());

		createButtons(toolkit, (Composite) this.getSection().getClient(),
				buttonLabels);

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(EventBPartWithButtons.this,
						event.getSelection());
				updateButtons();
			}
		});

		editor.addElementChangedListener(this);
	}

	@Override
	public void dispose() {
		editor.removeElementChangedListener(this);
		super.dispose();
	}

	/**
	 * @return The viewer contains in this section part.
	 */
	protected Viewer getViewer() {
		return viewer;
	}

	/**
	 * Enable/Disable a button.
	 * <p>
	 * 
	 * @param index
	 *            Index of the button
	 * @param enabled
	 *            <code>true</code> to enable, <code>false</code> to disable
	 */
	protected void setButtonEnabled(int index, boolean enabled) {
		if (buttons != null && index >= 0 && buttons.length > index) {
			Button b = buttons[index];
			b.setEnabled(enabled);
		}
	}

	/**
	 * Create buttons
	 * <p>
	 * 
	 * @param toolkit
	 *            The Form Toolkit used to create the buttons
	 * @param parent
	 *            The composite parent
	 * @param buttonLabels
	 *            The label of the buttons.
	 */
	private void createButtons(FormToolkit toolkit, Composite parent,
			String[] buttonLabels) {
		if (buttonLabels != null && buttonLabels.length > 0) {
			Composite buttonContainer = toolkit.createComposite(parent);
			GridData gd = new GridData(GridData.FILL_VERTICAL);
			buttonContainer.setLayoutData(gd);
			buttonContainer.setLayout(createButtonsLayout());
			buttons = new Button[buttonLabels.length];
			SelectionHandler listener = new SelectionHandler();
			for (int i = 0; i < buttonLabels.length; i++) {
				String label = buttonLabels[i];
				Button button = createButton(buttonContainer, label, i, toolkit);
				buttons[i] = button;
				button.addSelectionListener(listener);
			}
		}
		updateButtons();
		return;
	}

	/**
	 * Utility method to create a (single) button.
	 * <p>
	 * 
	 * @param parent
	 *            The composite parent
	 * @param label
	 *            The label of the button
	 * @param index
	 *            The index of the button
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
	 *         <p>
	 *         A private class for selection listener of the buttons.
	 */
	class SelectionHandler implements SelectionListener {
		@Override
		public void widgetSelected(SelectionEvent e) {
			buttonSelected(e);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			buttonSelected(e);
		}

		private void buttonSelected(SelectionEvent e) {
			if(checkAndShowReadOnly(editor.getRodinInput())) {
				return;
			}
			Integer index = (Integer) e.widget.getData();
			EventBPartWithButtons.this.buttonSelected(index.intValue());
		}
	}

	/**
	 * Creat the layout for buttons.
	 * <p>
	 * 
	 * @return A new Grid Layout
	 */
	private GridLayout createButtonsLayout() {
		GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = 0;
		return layout;
	}

}
