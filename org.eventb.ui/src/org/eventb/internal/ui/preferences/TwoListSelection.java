/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - fixed the movement of a  list of selected items
 *     Systerel - refactored and extracted widget from TwoListSelectionEditor
 ******************************************************************************/
package org.eventb.internal.ui.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Widget;
import org.eventb.internal.ui.utils.Messages;

public class TwoListSelection<T> {

	public static interface ILabelProvider {

		public String getLabel(Object object);

	}

	/**
	 * The right table viewer: selected objects; can be <code>null</code>
	 * (before creation or after disposal).
	 */
	List selected;

	/**
	 * The left list widget: available objects; can be <code>null</code> (before
	 * creation or after disposal).
	 */
	List available;

	/**
	 * The button box containing the Add, Remove, Up, and Down buttons;
	 * <code>null</code> if none (before creation or after disposal).
	 */
	Composite buttonBox;

	/**
	 * The Add button.
	 */
	Button addButton;

	/**
	 * The Remove button.
	 */
	Button removeButton;

	/**
	 * The Up button.
	 */
	Button upButton;

	/**
	 * The Down button.
	 */
	Button downButton;

	/**
	 * The selection listener.
	 */
	private SelectionListener selectionListener;

	/**
	 * The list of selected objects.
	 */
	private ArrayList<T> selectedElements;

	/**
	 * The list of available objects.
	 */
	private ArrayList<T> availableElements;

	/**
	 * The minimum width for the two lists.
	 */
	private int minWidth = 200;

	private final ILabelProvider labelProvider;

	public TwoListSelection(Composite parent, ILabelProvider labelProvider) {
		this.labelProvider = labelProvider;
		selectedElements = new ArrayList<T>();
		availableElements = new ArrayList<T>();
		createComposite(parent);
	}

	/**
	 * Notifies that the Add button has been pressed.
	 */
	void addPressed() {
		move(available, selected, availableElements, selectedElements);
	}

	/**
	 * Notifies that the Remove button has been pressed.
	 */
	void removePressed() {
		move(selected, available, selectedElements, availableElements);
	}

	/**
	 * Utility method for moving an object from one list (the source list) to
	 * another list (the destination list).
	 * 
	 * @param sourceList
	 *            the list where the elements move from
	 * 
	 * @param destinationList
	 *            the list where the elements move to
	 */
	private void move(List sourceList, List destinationList,
			ArrayList<T> sourceElements, ArrayList<T> destinationElements) {
		int[] indices = sourceList.getSelectionIndices();
		if (indices.length != 0) {
			ArrayList<T> elements = new ArrayList<T>();

			for (int index : indices) {
				elements.add(sourceElements.get(index));
			}

			// Remove all selected objects in "from"
			sourceElements.removeAll(elements);
			sourceList.remove(indices);

			// Add to the objects to "to" at the correct index
			int index = destinationList.getSelectionIndex();

			if (index < 0)
				index = destinationList.getItemCount();

			destinationElements.addAll(index, elements);

			for (Object object : elements) {
				destinationList.add(getLabel(object), index++);
			}
			selectionChanged();
			selected.getParent().layout(true);
		}
	}

	/**
	 * Gets the label for an object. This label will be used to display the
	 * elements in the lists.
	 * 
	 * @param object
	 *            an object
	 * @return the label for the input object
	 */
	private String getLabel(Object object) {
		return labelProvider.getLabel(object);
	}

	/**
	 * Creates the Add, Remove, Up, and Down button in the given button box.
	 * 
	 * @param box
	 *            the box for the buttons
	 */
	private void createButtons(Composite box) {
		addButton = createPushButton(box, ">>");//$NON-NLS-1$
		removeButton = createPushButton(box, "<<");//$NON-NLS-1$
		upButton = createPushButton(box, "ListEditor.up");//$NON-NLS-1$
		downButton = createPushButton(box, "ListEditor.down");//$NON-NLS-1$
	}

	/**
	 * Helper method to create a push button.
	 * 
	 * @param parent
	 *            the parent control
	 * @param key
	 *            the resource name used to supply the button's label text
	 * @return the newly created push button
	 */
	private Button createPushButton(Composite parent, String key) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(JFaceResources.getString(key));
		button.setFont(parent.getFont());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		button.setLayoutData(data);
		button.addSelectionListener(getSelectionListener());
		return button;
	}

	/**
	 * Returns this field editor's selection listener. The listener is created
	 * if necessary.
	 * 
	 * @return the selection listener
	 */
	private SelectionListener getSelectionListener() {
		if (selectionListener == null) {
			createSelectionListener();
		}
		return selectionListener;
	}

	/**
	 * Creates a selection listener.
	 */
	private void createSelectionListener() {
		selectionListener = new SelectionAdapter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				Widget widget = event.widget;
				if (widget == addButton) {
					addPressed();
				} else if (widget == removeButton) {
					removePressed();
				} else if (widget == upButton) {
					upPressed();
				} else if (widget == downButton) {
					downPressed();
				} else if (widget == selected) {
					selectionChanged();
				} else if (widget == available) {
					selectionChanged();
				}
			}
		};
	}

	/**
	 * Notifies that the list selection has changed.
	 */
	void selectionChanged() {
		final int selectedSize = selectedElements.size();
		final boolean isSelected = selected.getSelectionIndex() >= 0;
		final int availableIndex = available.getSelectionIndex();

		addButton.setEnabled(availableIndex >= 0);
		removeButton.setEnabled(isSelected);

		if (isSelected && selectedSize > 1) {
			final int[] selectedIndices = selected.getSelectionIndices();
			Arrays.sort(selectedIndices);
			final boolean isContiguous = isContiguousSelection(selectedIndices);
			final int firstIndex = selectedIndices[0];
			final int lastIndex = selectedIndices[selectedIndices.length - 1];
			upButton.setEnabled(isContiguous && firstIndex > 0);
			downButton.setEnabled(isContiguous && lastIndex < selectedSize - 1);
		} else {
			upButton.setEnabled(false);
			downButton.setEnabled(false);
		}
	}

	/**
	 * Returns <code>true</code> if the indexes (given in a sorted array) are
	 * contiguous, <code>false</code> otherwise.
	 * 
	 * @param indices
	 *            a sorted array of integers
	 */
	private boolean isContiguousSelection(int[] indices) {
		if (indices.length == 0 || indices.length == 1) {
			return true;
		} else {
			for (int i = 1; i < indices.length; i++) {
				if (indices[i - 1] + 1 != indices[i]) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Notifies that the Up button has been pressed.
	 */
	void upPressed() {
		swap(true);
	}

	/**
	 * Notifies that the Down button has been pressed.
	 */
	void downPressed() {
		swap(false);
	}

	/**
	 * Moves the currently selected item up or down.
	 * 
	 * The selected items must be contiguous and not empty. If up is
	 * <code>true</code>, the first selected item have to be different than the
	 * first item. If up is <code>false</code> the last selected item have to be
	 * different than the last item.
	 * 
	 * @param up
	 *            <code>true</code> if the item should move up, and
	 *            <code>false</code> if it should move down
	 */
	private void swap(boolean up) {
		final int[] selectedIndices = selected.getSelectionIndices();
		final int firstIndex = selectedIndices[0];
		final int lastIndex = selectedIndices[selectedIndices.length - 1];
		// element to move
		final int move = up ? firstIndex - 1 : lastIndex + 1;
		// new position of moved element
		final int target = up ? lastIndex : firstIndex;
		final String[] selection = selected.getItems();
		Assert.isTrue(selection.length > Math.max(target, move));
		selected.remove(move);
		selected.add(selection[move], target);
		final T object = selectedElements.get(move);
		selectedElements.remove(move);
		selectedElements.add(target, object);
		selectionChanged();
	}

	private void createComposite(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);
		AbstractEventBPreferencePage.setFillParent(composite);

		final Label availableLabel = new Label(composite, SWT.LEFT);
		availableLabel
				.setText(Messages.preferencepage_twolistselectioneditor_availablelabel);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		availableLabel.setLayoutData(gd);

		final Label tmpLabel = new Label(composite, SWT.CENTER);
		gd = new GridData();
		gd.verticalAlignment = GridData.CENTER;
		tmpLabel.setLayoutData(gd);

		final Label selectedLabel = new Label(composite, SWT.LEFT);
		selectedLabel
				.setText(Messages.preferencepage_twolistselectioneditor_selectedlabel);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		selectedLabel.setLayoutData(gd);

		available = getListControl(composite);
		gd = getFillData();
		gd.horizontalSpan = 1;
		gd.minimumWidth = minWidth;
		available.setLayoutData(gd);

		buttonBox = getButtonBoxControl(composite);
		gd = new GridData();
		gd.verticalAlignment = GridData.CENTER;
		buttonBox.setLayoutData(gd);

		selected = getListControl(composite);
		gd = getFillData();
		gd.minimumWidth = minWidth;
		selected.setLayoutData(gd);

		selectionChanged();
	}

	private GridData getFillData() {
		final GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		return gd;
	}

	/**
	 * Returns this field editor's list control.
	 * 
	 * @param parent
	 *            the parent control
	 * @return the list control
	 */
	public List getListControl(Composite parent) {
		List list;
		list = new List(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL);
		list.setFont(parent.getFont());
		list.addSelectionListener(getSelectionListener());
		return list;
	}

	/**
	 * Returns this field editor's button box containing the Add, Remove, Up,
	 * and Down button.
	 * 
	 * @param parent
	 *            the parent control
	 * @return the button box
	 */
	public Composite getButtonBoxControl(Composite parent) {
		buttonBox = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		buttonBox.setLayout(layout);
		createButtons(buttonBox);
		buttonBox.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				addButton = null;
				removeButton = null;
				upButton = null;
				downButton = null;
				buttonBox = null;
			}
		});
		return buttonBox;
	}

	public void setLists(Collection<T> newAvailable, Collection<T> newSelected) {
		setList(newSelected, selected, selectedElements);
		setList(newAvailable, available, availableElements);
	}

	private void setList(Collection<T> newList, List listUI,
			ArrayList<T> listElement) {
		listUI.removeAll();
		listElement.clear();
		for (T object : newList) {
			listElement.add(object);
			listUI.add(getLabel(object));
		}
	}

	/**
	 * Gets the list of selected objects.
	 * 
	 * @return the list of selected objects
	 */
	public ArrayList<T> getSelectedObjects() {
		return selectedElements;
	}

	public void setEnabled(boolean enabled) {
		selected.setEnabled(enabled);
		available.setEnabled(enabled);
		addButton.setEnabled(enabled);
		removeButton.setEnabled(enabled);
		upButton.setEnabled(enabled);
		downButton.setEnabled(enabled);
	}

	public void addSelectionListener(SelectionListener listener) {
		addButton.addSelectionListener(listener);
		removeButton.addSelectionListener(listener);
		upButton.addSelectionListener(listener);
		downButton.addSelectionListener(listener);
	}
}
