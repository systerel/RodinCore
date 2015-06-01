/*******************************************************************************
 * Copyright (c) 2006, 2015 ETH Zurich and others.
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

import static org.eclipse.jface.preference.PreferenceConverter.setDefault;
import static org.eventb.internal.ui.EventBSharedColor.RGB_RED;
import static org.eventb.internal.ui.EventBSharedColor.RGB_YELLOW;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_BORDER_ENABLE;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_BOX_BORDER_COLOR;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_DIRTY_STATE_COLOR;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_EXPAND_SECTIONS;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_HIGHLIGHT_IN_PROVERUI;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_REQUIRED_FIELD_BACKGROUND;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author htson
 *         <p>
 *         Class used to initialize default preference values.
 *         </p>
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = EventBPreferenceStore
				.getPreferenceStore();

		// Default value for editor pages
		MachineEditorPagesPreference.getDefault().setDefault();
		ContextEditorPagesPreference.getDefault().setDefault();

		// Default values for modelling ui
		store.setDefault(P_BORDER_ENABLE, true);
		store.setDefault(P_EXPAND_SECTIONS, true);

		// Default colors
		setDefault(store, P_REQUIRED_FIELD_BACKGROUND, RGB_YELLOW);
		setDefault(store, P_DIRTY_STATE_COLOR, RGB_YELLOW);
		setDefault(store, P_BOX_BORDER_COLOR, RGB_RED);

		// Set the values for context element prefixes
		PreferenceUtils.setDefaultPreferences(store);
		
		// By default, disables the highlighting in proof UI
		store.setDefault(P_HIGHLIGHT_IN_PROVERUI, false);
	}

}