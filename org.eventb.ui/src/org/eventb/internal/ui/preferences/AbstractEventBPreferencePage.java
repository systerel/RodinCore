/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractEventBPreferencePage extends PreferencePage {

	// Stores all created field editors
	private List<IEventBFieldEditor> editors = new ArrayList<IEventBFieldEditor>();

	@Override
	protected Control createContents(Composite parent) {
		createFieldEditors(parent);
		initialize();
		return parent;
	}

	/**
	 * This allows us to store each field editor added by subclasses in a list
	 * for later processing.
	 */
	protected void addField(IEventBFieldEditor editor) {
		editors.add(editor);
	}

	protected List<IEventBFieldEditor> getEditors() {
		return editors;
	}

	/**
	 * Method that users must implement, in order to define the editors they
	 * intend to use.
	 */
	protected abstract void createFieldEditors(Composite parent);

	/**
	 * Loads all the field editors with their default values.
	 */
	@Override
	protected void performDefaults() {
		for (IEventBFieldEditor ed : editors) {
			ed.loadDefault();
		}
		super.performDefaults();
	}

	/**
	 * Initializes all field editors.
	 */
	protected void initialize() {
		for (IEventBFieldEditor ed : editors) {
			ed.load();
		}
	}

	@Override
	public boolean performOk() {
		performApply();
		return true;
	}

	/**
	 * Saves all field editors by calling {@link IEventBFieldEditor#store()} on
	 * each registered editor.
	 */
	@Override
	protected void performApply() {
		for (IEventBFieldEditor ed : editors) {
			ed.store();
		}
	}

	protected final IEventBFieldEditor getStringEditor(String key,
			Composite parent, int style) {
		final IEventBFieldEditor field = new StringEventBEditor(
				getPreferenceStore(), key, parent, style);
		addField(field);
		return field;
	}

	protected final IEventBFieldEditor getBooleanEditor(String key,
			String text, Composite parent) {
		final IEventBFieldEditor field = new BooleanEventBEditor(
				getPreferenceStore(), key, text, parent);
		addField(field);
		return field;
	}

	protected final Label createLabel(Composite parent, String text) {
		final Label label = new Label(parent, SWT.LEFT);
		label.setText(text);
		label.setFont(parent.getFont());
		return label;
	}

	static protected Composite getComposite(Composite parent, int column) {
		final Composite composite = new Composite(parent, NONE);
		final GridLayout layout = new GridLayout();
		layout.numColumns = column;
		composite.setLayout(layout);
		setFillParent(composite);
		return composite;
	}

	public static final void setFillParent(Control control) {
		final GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		control.setLayoutData(gd);
	}

	static abstract class EventBFieldEditor implements IEventBFieldEditor {
		private final String key;
		private final IPreferenceStore preferenceStore;

		public EventBFieldEditor(IPreferenceStore preferenceStore, String key) {
			this.key = key;
			this.preferenceStore = preferenceStore;
		}

		protected IPreferenceStore getPreferenceStore() {
			return preferenceStore;
		}

		public String getkey() {
			return key;
		}

	}

	static class StringEventBEditor extends EventBFieldEditor {

		final private Text text;

		public StringEventBEditor(IPreferenceStore preferenceStore, String key,
				Composite parent, int style) {
			super(preferenceStore, key);
			text = new Text(parent, SWT.SINGLE | SWT.BORDER);
			text.setFont(parent.getFont());
			final GridData gd = new GridData();
			gd.horizontalAlignment = GridData.FILL;
			gd.grabExcessHorizontalSpace = true;
			text.setLayoutData(gd);
		}

		@Override
		public void store() {
			getPreferenceStore().setValue(getkey(), text.getText());
		}

		@Override
		public void setEnabled(boolean enabled) {
			text.setEnabled(enabled);
		}

		@Override
		public void load() {
			final String value = getPreferenceStore().getString(getkey());
			text.setText(value);
		}

		@Override
		public void loadDefault() {
			final String value = getPreferenceStore()
					.getDefaultString(getkey());
			text.setText(value);
		}

	}

	static class BooleanEventBEditor extends EventBFieldEditor {

		final private Button check;

		public BooleanEventBEditor(IPreferenceStore preferenceStore,
				String key, String text, Composite parent) {
			super(preferenceStore, key);
			check = new Button(parent, SWT.CHECK);
			check.setText(text);
			final GridData gd = new GridData();
			gd.horizontalAlignment = GridData.FILL;
			check.setLayoutData(gd);
		}

		@Override
		public void store() {
			getPreferenceStore().setValue(getkey(), check.getSelection());
		}

		@Override
		public void setEnabled(boolean enabled) {
			check.setEnabled(enabled);
		}

		@Override
		public void load() {
			final boolean selected = getPreferenceStore().getBoolean(getkey());
			check.setSelection(selected);
		}

		@Override
		public void loadDefault() {
			final boolean selected = getPreferenceStore().getDefaultBoolean(
					getkey());
			check.setSelection(selected);
		}

	}

}