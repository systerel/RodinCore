/*******************************************************************************
 * Copyright (c) 2010, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences.tactics;

import static org.eclipse.swt.SWT.NONE;
import static org.eventb.internal.ui.UIUtils.createFilterText;
import static org.eventb.internal.ui.UIUtils.setupFilter;
import static org.eventb.internal.ui.UIUtils.showQuestion;
import static org.eventb.internal.ui.utils.Messages.tacticlist_currentunsaved;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;

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

	private static final String[] NO_ELEMENT = new String[0];
	
	final ListViewer list;
	private final Text filter;
	private final Composite details;
	private final Composite buttons;
	private final java.util.List<String> entries = new ArrayList<String>();

	private final int minWidth = 200;

	private IDetailsProvider provider;

	public DetailedList(String detailsTitle, Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		setTableLayout(composite, 3);

		filter = createFilterText(composite);
		createLabel(composite, detailsTitle);
		createLabel(composite, "");

		list = new ListViewer(new List(composite, SWT.BORDER | SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL));
		details = new Composite(composite, SWT.BORDER | SWT.FILL| SWT.NO_FOCUS);
		buttons = new Composite(composite, SWT.NONE);

		setTableLayout(buttons, 1);

		final GridData gd = getFillData();
		gd.minimumWidth = minWidth;
		list.getList().setLayoutData(gd);
		details.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		list.setContentProvider(ArrayContentProvider.getInstance());
		list.setInput(entries);
		list.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateDetails();
			}

		});
		setupFilter(filter, list);
	}

	boolean filterMatches(String text) {
		final String filterText = filter.getText();
		return text.toUpperCase().contains(filterText.toUpperCase());
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
	 * Adds the element to the end of the main list and selects it.
	 */
	public void addElement(String element) {
		if (entries.contains(element)){
			return;
		}
		entries.add(element);
		list.refresh();
		list.setSelection(new StructuredSelection(element));
		updateDetails();
	}

	private IStructuredSelection internalGetSelection() {
		return (IStructuredSelection) list.getSelection();
	}

	/**
	 * Removes all selected elements in the main list.
	 */
	public void removeSelectedElement() {
		final IStructuredSelection selection = internalGetSelection();
		entries.removeAll(selection.toList());
		list.refresh();
		provider.clear();
	}

	/**
	 * Returns the selected items in the main list. The order of the items is
	 * unspecified. An empty array indicates that no items are selected.
	 */
	public String[] getSelection() {
		final IStructuredSelection selection = internalGetSelection();
		if (selection.isEmpty()) {
			return NO_ELEMENT;
		}
		
		// cannot cast selection.toArray() into String[]
		final Object[] array = selection.toArray();
		final String[] result = new String[array.length];
		System.arraycopy(array, 0, result, 0, array.length);
		return result;
	}

	/**
	 * Returns the number of selected items in the main list.
	 */
	public int getSelectionCount() {
		return internalGetSelection().size();
	}

	/**
	 * Set the selected elements in the main list.
	 */
	public void setSelection(String[] selection) {
		list.setSelection(new StructuredSelection(selection));
	}

	public void setDetailsProvider(IDetailsProvider provider) {
		this.provider = provider;
		provider.setParentComposite(details);
	}

	/**
	 * Remove all items in the main list
	 */
	public void clear() {
		entries.clear();
		list.refresh();
	}

	/**
	 * Sets the main list's items to be the given array of items.
	 */
	public void setList(String[] items) {
		clear();
		for (String item : items) {
			addElement(item);
		}

		list.refresh();
	}

	/**
	 * Sets the text of the item in the main list at the given zero-relative
	 * index to the string argument.
	 */
	public void rename(int indice, String name) {
		entries.set(indice, name);
		list.refresh();
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
	public void addSelectionListener(ISelectionChangedListener listener) {
		list.addSelectionChangedListener(listener);
	}

	public void setEnabled(boolean enabled) {
		list.getList().setEnabled(enabled);
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
		if (provider != null) {
			final IStructuredSelection selection = internalGetSelection();
			if (selection.size() == 1) {
				saveCurrentIfChanges(true);
				provider.putDetails((String) selection.getFirstElement());
			} else {
				provider.clear();
			}
		}
		details.redraw();
	}

	/**
	 * Set a context menu on the main list.
	 */
	public void setMenu(MenuManager menuManager) {
		final List l = list.getList();
		final Menu menu = menuManager.createContextMenu(l);
		l.setMenu(menu);
	}

	/**
	 * Enables context menu of the main list
	 */
	public void setContextMenuEnabled(boolean enabled) {
		list.getList().getMenu().setEnabled(enabled);
	}
	
	public void saveCurrentIfChanges(boolean ask) {
		if (provider.hasChanges()) {
			final boolean save ;
			if (ask) {
				save = showQuestion(tacticlist_currentunsaved);
			} else {
				save = true;
			}
			if (save) {
				provider.save();
			}
		}
	}

}
