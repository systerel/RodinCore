/*******************************************************************************
 * Copyright (c) 2007-2008 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * ETH Zurich - initial API and implementation
 * Systerel - fixed the movement of a  list of selected items
 ******************************************************************************/

package org.eventb.internal.ui.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.FieldEditor;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Widget;
import org.eventb.internal.ui.utils.Messages;

/**
 * @author htson
 *         <p>
 *         This is the base implementation of a two list selection editor, which
 *         is a subclass of {@link FieldEditor}, i.e. it is used for editing a
 *         preference and it knows how to save and restore itself.
 *         </p>
 *         <p>
 *         This field editor contains two list: available objects on the left
 *         and selected objects on the right. There are functions to select
 *         (move object from left to right) or deselect (move object from right
 *         to left). The objects in the selected list are ordered and there are
 *         functions to chang this order (i.e. moving object up/down).
 *         </p>
 *         <p>
 *         The preference is stored as a comma separated list of the selected
 *         objects.
 *         </p>
 */
public abstract class TwoListSelectionEditor extends FieldEditor {

    /**
	 * The right table viewer: selected objects; can be <code>null</code>
	 * (before creation or after disposal).
	 */
    List selected;

    /**
     * The left list widget: available objects; can be <code>null</code>
     * (before creation or after disposal).
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
    private ArrayList<Object> selectedElements;
    
    /**
     * The list of available objects.
     */
    private ArrayList<Object> availableElements;
       
    /**
     * The minimum width for the two lists.
     */
    private int minWidth = 200;

    /**
     * Creates a new field editor 
     */
    protected TwoListSelectionEditor() {
    	// Do nothing
    }

    /**
     * Creates a list field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
	protected TwoListSelectionEditor(String name, String labelText,
			Composite parent) {
		selectedElements = new ArrayList<Object>();
		availableElements = new ArrayList<Object>();
        init(name, labelText);
        createControl(parent);
    }

    /**
     * Notifies that the Add button has been pressed.
     */
    void addPressed() {
        setPresentsDefaultValue(false);
        move(available, selected, availableElements, selectedElements);
    }

    /**
     * Notifies that the Remove button has been pressed.
     */
    void removePressed() {
        setPresentsDefaultValue(false);
        move(selected, available, selectedElements, availableElements);
    }
    
    /**
	 * Utility method for moving an object from one list (the source list) to
	 * another list (the destination list).
	 * 
	 * @param sourceList
	 *            the list where the elements move from.
	 * 
	 * @param destinationList
	 *            the list where the elements move to.
	 * @param sourceElements
	 *            the set of elements will be moved.
	 * @param destinationElements
	 *            the set of elements which will be used as a destination. The
	 *            source elements will be put as before the first elements of this set.
	 */
    private void move(List sourceList, List destinationList,
			ArrayList<Object> sourceElements, ArrayList<Object> destinationElements) {
    	int[] indices = sourceList.getSelectionIndices();
        if (indices.length != 0) {
            ArrayList<Object> elements = new ArrayList<Object>();
        	
        	for (int index : indices) {
        		elements.add(sourceElements.get(index));
        	}

        	// Remove all selected objects in "from"
        	sourceElements.removeAll(elements);
        	sourceList.remove(indices);

        	// Add to the objects to "to" at the correct index
        	int index = destinationList.getSelectionIndex();
        	
        	if (index < 0) index = destinationList.getItemCount();
        	
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
	 *            an object.
	 * @return the label for the input object.
	 */
    protected abstract String getLabel(Object object);

    /**
     * Creates the Add, Remove, Up, and Down button in the given button box.
     *
     * @param box the box for the buttons
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
     * @param parent the parent control
     * @param key the resource name used to supply the button's label text
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
     * Returns this field editor's selection listener.
     * The listener is created if necessary.
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

        	/* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
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
			final boolean isContiguous = isContiguous(selectedIndices);
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
	 * Returns <code>true</code> if the indices (given in a sorted array) are
	 * contiguous, <code>false</code> otherwise.
	 * 
	 * @param indices
	 *            a sorted array of integers
	 */
	private boolean isContiguous(int[] indices) {
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
	 * <p />
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
		setPresentsDefaultValue(false);
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
		final Object object = selectedElements.get(move);
		selectedElements.remove(move);
		selectedElements.add(target, object);
		selectionChanged();
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
        Control control = getLabelControl();
        ((GridData) control.getLayoutData()).horizontalSpan = numColumns;
        ((GridData) selected.getLayoutData()).horizontalSpan = 1;
        ((GridData) buttonBox.getLayoutData()).horizontalSpan = numColumns-2;
        ((GridData) available.getLayoutData()).horizontalSpan = 1;
	}

    /**
     * Combines the given list of objects into a single string.
     * This method is the converse of <code>parseString</code>. 
     * <p>
     * Subclasses must implement this method.
     * </p>
     *
     * @param objects the list of items
     * @return the combined string
     * @see #parseString
     */
    protected abstract String createList(ArrayList<Object> objects);

    /**
     * Splits the given string into a list of objects.
     * This method is the converse of <code>createList</code>. 
     * <p>
     * Subclasses must implement this method.
     * </p>
     *
     * @param stringList the string
     * @return an array of <code>String</code>
     * @see #createList
     */
    protected abstract ArrayList<Object> parseString(String stringList);
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite,
	 *      int)
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
        Control control = getLabelControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        control.setLayoutData(gd);

        Label availableLabel = new Label(parent, SWT.LEFT);
        availableLabel
				.setText(Messages.preferencepage_twolistselectioneditor_availablelabel);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        availableLabel.setLayoutData(gd);

        Label tmpLabel = new Label(parent, SWT.CENTER);
        gd = new GridData();
        gd.verticalAlignment = GridData.CENTER;
        tmpLabel.setLayoutData(gd);
        
        Label selectedLabel = new Label(parent, SWT.LEFT);
        selectedLabel
				.setText(Messages.preferencepage_twolistselectioneditor_selectedlabel);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        selectedLabel.setLayoutData(gd);

        available = getListControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = 1;
        gd.minimumWidth = minWidth;
        gd.grabExcessHorizontalSpace = true;
        available.setLayoutData(gd);

        buttonBox = getButtonBoxControl(parent);
        gd = new GridData();
        gd.verticalAlignment = GridData.CENTER;
        buttonBox.setLayoutData(gd);

        selected = getListControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = 1;
        gd.minimumWidth = minWidth;
        gd.grabExcessHorizontalSpace = true;
        selected.setLayoutData(gd);

        selectionChanged();
	}
	
	@Override
	protected void doLoad() {
		if (selected != null) {
			if (getPreferenceStore().contains(getPreferenceName())) {
				String s = getPreferenceStore().getString(getPreferenceName());
				setPreference(s);
			}
			else {
				doLoadDefault();
			}
        }
	}

	/**
	 * Utility method for parsing the information from the preference and
	 * display within the two lists.
	 * 
	 * @param preference
	 *            the information from the preference as a string.
	 */
	private void setPreference(String preference) {
		selected.removeAll();
		selectedElements = parseString(preference);
		for (Object object : selectedElements) {
			selected.add(getLabel(object));
		}

		Collection<Object> declaredElements = getDeclaredObjects();
		availableElements.clear();
		available.removeAll();
		for (Object object : declaredElements) {
			if (!selectedElements.contains(object)) {
				availableElements.add(object);
				available.add(getLabel(object));
			}
		}		
	}
	protected abstract Collection<Object> getDeclaredObjects();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	@Override
	protected void doLoadDefault() {
		if (selected != null) {
            String s = getPreferenceStore().getDefaultString(getPreferenceName());
            setPreference(s);
        }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	@Override
	protected void doStore() {
		String s = createList(selectedElements);
        if (s != null) {
			getPreferenceStore().setValue(getPreferenceName(), s);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	@Override
	public int getNumberOfControls() {
		return 3;
	}

	/**
     * Returns this field editor's list control.
     *
     * @param parent the parent control
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

	/**
	 * Gets the list of selected objects.
	 * 
	 * @return the list of selected objects.
	 */
	public ArrayList<Object> getSelectedObjects() {
		return selectedElements;
	}

}
