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
package org.eventb.internal.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eventb.ui.EventBUIPlugin;

public class EnabledComboEditor implements IEventBFieldEditor {

	private static final int NUM_COLUMN = 2;
	private static final int HORIZONTAL_SPACING = 5;
	private static final int VERTICAL_SPACING = 5;

	private final IPreferenceStore preferenceStore;

	private final static IPreferenceStore wsPreferenceStore = EventBUIPlugin
			.getDefault().getPreferenceStore();

	// title of group composite
	private final String title;
	// preference key of enable checkbox
	private final String enablePreferenceKey;
	// description of enable checkbox
	private final String enableDescription;
	// preference key of combo list
	private final String choicePreferenceKey;
	// description of combo list
	private final String choiceDescription;

	private final boolean propertyCase;
	
	private boolean enabledCombo;
	
	private Group comboGroup;

	private Button enableButton;
	private Combo comboList;
	private Label listLabel;

	public EnabledComboEditor(IPreferenceStore preferenceStore, String title,
			String enablePreferenceKey, String enableDescription,
			String choicePreferenceKey, String choiceDescription,
			Composite parent, boolean propertyCase) {
		this.title = title;
		this.enablePreferenceKey = enablePreferenceKey;
		this.enableDescription = enableDescription;
		this.choicePreferenceKey = choicePreferenceKey;
		this.choiceDescription = choiceDescription;
		this.preferenceStore = preferenceStore;
		this.propertyCase = propertyCase;
		createControl(parent);
	}

	private void createControl(Composite parent) {
		comboGroup = getGroup(parent);

		if (!propertyCase) {
			enableButton = new Button(comboGroup, SWT.CHECK);
			enableButton.setText(enableDescription);
			setHorizontalSpan(enableButton, NUM_COLUMN);
			// disable the combo list if the button is not check
			enableButton.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					updateCombo();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					updateCombo();
				}
			});
		}
		
		listLabel = new Label(comboGroup, SWT.NONE);
		listLabel.setText(choiceDescription);
		comboList = new Combo(comboGroup, SWT.READ_ONLY);

	}

	private void setHorizontalSpan(Control control, int span) {
		final GridData gd = new GridData();
		gd.horizontalSpan = span;
		control.setLayoutData(gd);
	}

	private Group getGroup(Composite parent) {
		final Group group = new Group(parent, SWT.SHADOW_OUT);
		group.setFont(parent.getFont());
		group.setText(title);
		final GridLayout layout = new GridLayout();
		layout.horizontalSpacing = HORIZONTAL_SPACING;
		layout.verticalSpacing = VERTICAL_SPACING;
		layout.numColumns = NUM_COLUMN;
		group.setLayout(layout);
		return group;
	}

	/**
	 * Enables the combo list if the enable button is enabled and checked.
	 */
	void updateCombo() {
		final boolean enabled;
		if (enableButton != null) {
			enabled = enableButton.isEnabled() && enableButton.getSelection();
		} else {
			enabled = wsPreferenceStore.getBoolean(enablePreferenceKey)
					&& enabledCombo;
			comboGroup.setEnabled(enabled);
		}
		listLabel.setEnabled(enabled);
		comboList.setEnabled(enabled);
	}
	
	@Override
	public void store() {
		final boolean isEnabled;
		if (!propertyCase) {
			isEnabled = enableButton.getSelection();
		} else {
			isEnabled = wsPreferenceStore.getBoolean(enablePreferenceKey)
					&& enabledCombo;
		}
		preferenceStore.setValue(enablePreferenceKey, isEnabled);
		if (isEnabled) {
			preferenceStore.setValue(choicePreferenceKey, comboList.getText());
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (!propertyCase) {
			enableButton.setEnabled(enabled);
		}
		enabledCombo = enabled;
		updateCombo();
	}

	@Override
	public void load() {
		if (!propertyCase) {
			final boolean enabled = preferenceStore
					.getBoolean(enablePreferenceKey);
			enableButton.setSelection(enabled);
		}
		final String choice = preferenceStore.getString(choicePreferenceKey);		
		setChoice(choice);
		updateCombo();
	}

	@Override
	public void loadDefault() {
		final boolean enabled;
		if (propertyCase) {
			enabled = wsPreferenceStore.getDefaultBoolean(enablePreferenceKey);
		} else {
			enabled = preferenceStore.getDefaultBoolean(enablePreferenceKey);
			enableButton.setSelection(enabled);
		}
		final String choice = preferenceStore
				.getDefaultString(choicePreferenceKey);
		comboList.setEnabled(enabled);
		setChoice(choice);
		updateCombo();
	}

	private void setChoice(String value) {
		for (int i = 0; i < comboList.getItemCount(); i++) {
			if (value.equals(comboList.getItem(i))) {
				comboList.select(i);
				return;
			}
		}
		// the value is not found in the list
		if (comboList.getItemCount() > 0) {
			comboList.select(0);
		} else {
			comboList.deselectAll();
		}
	}

	public void setItems(String[] labels) {
		final String text = comboList.getText();
		comboList.setItems(labels);
		setChoice(text);
	}

}