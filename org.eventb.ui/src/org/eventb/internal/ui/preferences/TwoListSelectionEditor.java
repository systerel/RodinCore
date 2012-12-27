/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - fixed the movement of a  list of selected items
 *     Systerel - refactored widget and moved in TwoListSelection
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

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

	private TwoListSelection<Object> selection;
	
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
        init(name, labelText);
        createControl(parent);
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

	@Override
	protected void adjustForNumColumns(int numColumns) {
		// Do nothing
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
		final Control control = getLabelControl(parent);
		final GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		control.setLayoutData(gd);

		selection = new TwoListSelection<Object>(parent,
				new TwoListSelection.ILabelProvider() {

					@Override
					public String getLabel(Object object) {
						return TwoListSelectionEditor.this.getLabel(object);
					}
				});
	}
	
	@Override
	protected void doLoad() {
		if (getPreferenceStore().contains(getPreferenceName())) {
			String s = getPreferenceStore().getString(getPreferenceName());
			setPreference(s);
		} else {
			doLoadDefault();
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
		final ArrayList<Object> selectedElements = parseString(preference);
		final Collection<Object> availableElements = getDeclaredObjects();
		availableElements.removeAll(selectedElements);
		selection.setLists(availableElements, selectedElements);
	}
	protected abstract Collection<Object> getDeclaredObjects();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	@Override
	protected void doLoadDefault() {
		final String s = getPreferenceStore().getDefaultString(
				getPreferenceName());
		setPreference(s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	@Override
	protected void doStore() {
		final ArrayList<Object> selectedElements = selection.getSelectedObjects();
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
		return 1;
	}

	@Override
	public void setEnabled(boolean enabled, Composite parent) {
		selection.setEnabled(enabled);
	}

}
