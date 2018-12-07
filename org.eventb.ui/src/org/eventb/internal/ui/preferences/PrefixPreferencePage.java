/*******************************************************************************
 * Copyright (c) 2010, 2018 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import static java.util.Comparator.comparing;
import static org.eclipse.jface.layout.GridDataFactory.copyData;
import static org.eclipse.jface.layout.GridLayoutFactory.copyLayout;
import static org.eventb.internal.ui.preferences.PreferenceConstants.PREFIX_PREFERENCE_PAGE_ID;
import static org.eventb.internal.ui.preferences.PreferenceUtils.getCtxElementsPrefixes;
import static org.eventb.internal.ui.preferences.PreferenceUtils.getMchElementsPrefixes;
import static org.eventb.internal.ui.preferences.PreferenceUtils.getPrefixPreferenceKey;
import static org.eventb.internal.ui.utils.Messages.preferencepage_prefixSettings_description;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
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
		AbstractFieldPreferenceAndPropertyPage {

	public static final String PAGE_ID = PREFIX_PREFERENCE_PAGE_ID;

	public static final Set<String> keys = new HashSet<String>();

	public PrefixPreferencePage() {
		super(PAGE_ID, EventBUIPlugin.PLUGIN_ID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setDescription(preferencepage_prefixSettings_description);
	}

	/**
	 * Sets the workspace value as default value if there were no project
	 * specific settings
	 */
	@Override
	protected void initializeDefaultProperties() {
		final IPreferenceStore ep = EventBUIPlugin.getDefault()
				.getPreferenceStore();
		if (!(hasProjectSettings())) {
			for (String pref : keys) {
				// we handle values which are strings only
				preferenceStore.setDefault(pref, ep.getString(pref));
				preferenceStore.setToDefault(pref);
			}
		}
	}

	@Override
	protected void createFieldEditors(Composite parent) {
		super.createFieldEditors(parent);
		final GridLayout groupLayout = new GridLayout(2, false);
		final GridData groupData = new GridData();
		groupData.horizontalAlignment = GridData.FILL;
		groupData.grabExcessHorizontalSpace = true;

		final Set<IInternalElementType<?>> rci = getCtxElementsPrefixes();
		final Group ctxGroup = new Group(parent, SWT.SHADOW_OUT);
		ctxGroup.setLayout(groupLayout);
		ctxGroup.setLayoutData(groupData);
		ctxGroup.setText("Context prefixes");
		createFields(ctxGroup, rci);

		final Set<IInternalElementType<?>> rmi = getMchElementsPrefixes();
		final Group mchGroup = new Group(parent, SWT.SHADOW_OUT);
		mchGroup.setText("Machine prefixes");
		mchGroup.setLayout(copyLayout(groupLayout));
		mchGroup.setLayoutData(copyData(groupData));
		createFields(mchGroup, rmi);
	}

	private void createFields(Composite parent, Set<IInternalElementType<?>> items) {
		for (final IInternalElementType<?> item : sortByName(items)) {
			final String name = getPrefixPreferenceKey(item);
			keys.add(name);
			final Label label = new Label(parent, NONE);
			label.setText(item.getName());
			getStringEditor(name, parent, NONE);
		}
	}

	private static List<IInternalElementType<?>> sortByName(Set<IInternalElementType<?>> items) {
		final List<IInternalElementType<?>> result = new ArrayList<>(items);
		result.sort(comparing(IInternalElementType<?>::getName));
		return result;
	}
}
