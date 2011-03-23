/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added "show borders" and "font color" options
 *     Systerel - used EventBPreferenceStore
 *     Systerel - added expand section preference
 *     Systerel - added new prefix preference mechanism support
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import static org.eventb.internal.ui.EventBSharedColor.RGB_DARK_GREEN;
import static org.eventb.internal.ui.EventBSharedColor.RGB_RED;
import static org.eventb.internal.ui.EventBSharedColor.RGB_YELLOW;

import java.util.Set;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eventb.internal.ui.preferences.tactics.TacticPreferenceUtils;
import org.rodinp.core.IInternalElementType;

/**
 * @author htson
 *         <p>
 *         Class used to initialize default preference values.
 *         </p>
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = EventBPreferenceStore
				.getPreferenceStore();

		TacticPreferenceUtils.initializeDefault(store);
		
		// Default value for machine editor pages
		IEditorPagesPreference machinePreference = MachineEditorPagesPreference
				.getDefault(); 
		machinePreference.setDefault();

		// Default value for context editor pages
		IEditorPagesPreference contextPreference = ContextEditorPagesPreference
				.getDefault();
		contextPreference.setDefault();
		
		// Default values for borders and colors and fonts
		store.setDefault(PreferenceConstants.P_BORDER_ENABLE, true);
		
		// Default value consider hidden hypotheses
		store.setDefault(PreferenceConstants.P_CONSIDER_HIDDEN_HYPOTHESES,
				false);
		
		// Default value for section expanding 
		store.setDefault(PreferenceConstants.P_EXPAND_SECTIONS, true);
		
		PreferenceConverter.setDefault(store,
				PreferenceConstants.P_TEXT_FOREGROUND, RGB_DARK_GREEN);
		
		PreferenceConverter.setDefault(store,
				PreferenceConstants.P_COMMENT_FOREGROUND, RGB_DARK_GREEN);
		
		PreferenceConverter
				.setDefault(store,
						PreferenceConstants.P_REQUIRED_FIELD_BACKGROUND,
						RGB_YELLOW);
		
		PreferenceConverter.setDefault(store,
				PreferenceConstants.P_DIRTY_STATE_COLOR, RGB_YELLOW);

		PreferenceConverter.setDefault(store,
				PreferenceConstants.P_BOX_BORDER_COLOR, RGB_RED);
		
		
		// Set the values for context element prefixes		
		final Set<IInternalElementType<?>> registeredContextItems = PreferenceUtils
				.getCtxElementsPrefixes();
		for (IInternalElementType<?> type : registeredContextItems) {
			final String name = PreferenceUtils.getPrefixPreferenceKey(type);
			store.setDefault(name, PreferenceUtils.getAutoNamePrefixFromDesc(type));
		}

		// Set the values for machine element prefixes
		final Set<IInternalElementType<?>> registeredMachineItems = PreferenceUtils
				.getMchElementsPrefixes();
		for (IInternalElementType<?> type : registeredMachineItems) {
			final String name = PreferenceUtils.getPrefixPreferenceKey(type);
			store.setDefault(name, PreferenceUtils.getAutoNamePrefixFromDesc(type));
		}
	}
}