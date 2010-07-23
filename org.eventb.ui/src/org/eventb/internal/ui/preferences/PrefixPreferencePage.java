/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel	- initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import static org.eventb.internal.ui.utils.Messages.preferencepage_prefixSettings_description;

import java.util.Set;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IInternalElementType;

/**
 * Class implementing page with field editors to modify prefix settings that
 * behaves both as a property page and a preference page
 * 
 * @author Thomas Muller
 * 
 */
public class PrefixPreferencePage extends
		AbstractFieldPreferenceAndPropertyPage implements
		IWorkbenchPreferencePage {

	public static final String PAGE_ID = PreferenceConstants.PREFIX_PREFERENCE_PAGE_ID;

	public PrefixPreferencePage() {
		super(GRID, PAGE_ID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setDescription(preferencepage_prefixSettings_description);
	}

	@Override
	protected void initializeEditors(boolean reset) {
		final IPreferenceStore ep = EventBUIPlugin.getDefault()
				.getPreferenceStore();
		for (FieldEditor ed : editors) {
			final String pref = ed.getPreferenceName();
			// we handle values which are strings only
			final String value = ep.getString(pref);
			if (!ep.isDefault(pref)) {
				preferenceStore.setValue(pref, value);
			} else if (reset) {
				preferenceStore.setDefault(pref, value);
				preferenceStore.setToDefault(pref);
			} else {
				preferenceStore.setDefault(pref, value);
			}
		}
	}

	@Override
	public void createFieldEditors() {
		// get the contributed machine elements
		final Set<IInternalElementType<?>> rmi = PreferenceUtils
				.getCtxElementsPrefixes();
		createFields(rmi);
		// get the contributed context elements
		final Set<IInternalElementType<?>> rci = PreferenceUtils
				.getMchElementsPrefixes();
		createFields(rci);
	}

	private void createFields(Set<IInternalElementType<?>> rci) {
		for (IInternalElementType<?> item : rci) {
			final String name = PreferenceUtils.getPrefixPreferenceKey(item);
			final StringFieldEditor field = new StringFieldEditor(name, item
					.getName(), getFieldEditorParent());
			addField(field);
		}
	}

}
