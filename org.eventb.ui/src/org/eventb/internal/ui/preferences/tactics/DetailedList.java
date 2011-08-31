/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences.tactics;

import static org.eclipse.swt.SWT.NONE;
import static org.eventb.internal.ui.UIUtils.showQuestion;

import java.util.Arrays;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;

/**
 * Instances of this class represent two list of strings. An main list and a
 * second list which display details when a string in the main list is selected.
 * The details is provided by an instance of {@link IDetailsProvider}. </p>
 * Client may add buttons on the right of the two list with method
 * {@link DetailedList#addButton(String, Listener)}.
 * 
 * @see IDetailsProvider
 */
public class DetailedList {

	private final List list;
	private final Composite details;
	private final Composite buttons;

	private final int minWidth = 200;

	private IDetailsProvider provider;

	public DetailedList(String listTitle, String detailsTitle, Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		setTableLayout(composite, 3);

		createLabel(composite, listTitle);
		createLabel(composite, detailsTitle);
		createLabel(composite, "");

		list = new List(composite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		details = new Composite(composite, SWT.BORDER | SWT.FILL| SWT.NO_FOCUS
				| SWT.V_SCROLL);
		buttons = new Composite(composite, SWT.NONE);

		setTableLayout(buttons, 1);

		final GridData gd = getFillData();
		gd.minimumWidth = minWidth;
		list.setLayoutData(gd);
		details.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		list.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateDetails();
			}

		});
	}

	private GridData getFillData() {
		final GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		return gd;
	}

	private void setTableLayout(Composite composite, int numColumns) {
		final GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		layout.makeColumnsEqualWidth = false;
		composite.setLayout(layout);
		composite.setLayoutData(getFillData());
	}

	private void createLabel(Composite parent, String text) {
		final Label labelList = new Label(parent, NONE);
		labelList.setText(text);
	}

	/**
	 * Adds the element to the end of the main list and selected it.
	 */
	public void addElement(String element) {
		if (Arrays.asList(list.getItems()).contains(element)){
			return;
		}
		list.add(element);
		final int index = list.getItemCount() - 1;
		list.setSelection(index);
		updateDetails();
	}

	/**
	 * Adds all items to the end of main list.
	 */
	public void addElements(String[] items) {
		for (String item : items) {
			list.add(item);
		}
	}

	/**
	 * Removes all selected elements in the main list.
	 */
	public void removeSelectedElement() {
		if (list.getSelectionCount() >= 0) {
			list.remove(list.getSelectionIndices());
			provider.clear();
		}
	}

	/**
	 * Returns the selected items in the main list. The order of the items is
	 * unspecified. An empty array indicates that no items are selected.
	 */
	public String[] getSelection() {
		return list.getSelection();
	}

	/**
	 * Returns the number of selected items in the main list.
	 */
	public int getSelectionCount() {
		return list.getSelectionCount();
	}

	/**
	 * Returns the indices of selected items in the main list. The order of the
	 * items is unspecified. The array is empty if no items are selected.
	 */
	public int[] getSelectionIndices() {
		return list.getSelectionIndices();
	}

	/**
	 * Set the selected elements in the main list.
	 */
	public void setSelection(String[] selection) {
		list.setSelection(selection);
	}

	public void setDetailsProvider(IDetailsProvider provider) {
		this.provider = provider;
		provider.setParentComposite(details);
	}

	/**
	 * Remove all items in the main list
	 */
	public void clear() {
		list.removeAll();
	}

	/**
	 * Sets the main list's items to be the given array of items.
	 */
	public void setList(String[] items) {
		list.setItems(items);
		list.pack();
	}

	/**
	 * Sets the text of the item in the main list at the given zero-relative
	 * index to the string argument.
	 */
	public void rename(int indice, String name) {
		list.setItem(indice, name);
	}

	/**
	 * Create a new button and attach a given listener.
	 * 
	 * @param text
	 *            the text of new button
	 * @param listener
	 *            the listener which should be notified when the selection event
	 *            occurs
	 */
	public Button addButton(String text, Listener listener) {
		final Button button = new Button(buttons, SWT.PUSH);
		button.setText(text);
		final GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		button.setLayoutData(data);
		button.setEnabled(true);
		button.addListener(SWT.Selection, listener);
		return button;
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the user changes the main list's selection, by sending it one of the
	 * messages defined in the <code>SelectionListener</code> interface.
	 */
	public void addSelectionListener(SelectionListener listener) {
		list.addSelectionListener(listener);
	}

	public void setEnabled(boolean enabled) {
		list.setEnabled(enabled);
		details.setEnabled(enabled);
		for (Control child : buttons.getChildren()) {
			final Button button = (Button) child;
			button.setEnabled(enabled);
		}
	}

	/**
	 * Set the details of selected item.
	 */
	public void updateDetails() {
		if (provider != null && list.getSelectionCount() == 1) {
			saveCurrentIfChanges(true);
			provider.putDetails(getSelectedItem());
		} else {
			provider.clear();
		}
		details.redraw();
	}

	private String getSelectedItem() {
		return list.getItem(list.getSelectionIndex());
	}

	/**
	 * Set a context menu on the main list.
	 */
	public void setMenu(MenuManager menuManager) {
		final Menu menu = menuManager.createContextMenu(list);
		list.setMenu(menu);
	}

	/**
	 * Enables context menu of the main list
	 */
	public void setContextMenuEnabled(boolean enabled) {
		list.getMenu().setEnabled(enabled);
	}
	
	public void saveCurrentIfChanges(boolean ask) {
		if (provider.hasChanges()) {
			final boolean save ;
			if (ask) {
				save = showQuestion("Current profile has unsaved changes: save ?");
			} else {
				save = true;
			}
			if (save) {
				provider.save();
			}
		}
	}

}
