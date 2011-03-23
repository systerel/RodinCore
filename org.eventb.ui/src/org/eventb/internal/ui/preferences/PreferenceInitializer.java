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

import static org.eclipse.jface.preference.PreferenceConverter.setDefault;
import static org.eventb.internal.ui.EventBSharedColor.RGB_DARK_GREEN;
import static org.eventb.internal.ui.EventBSharedColor.RGB_RED;
import static org.eventb.internal.ui.EventBSharedColor.RGB_YELLOW;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_BORDER_ENABLE;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_BOX_BORDER_COLOR;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_COMMENT_FOREGROUND;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_CONSIDER_HIDDEN_HYPOTHESES;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_DIRTY_STATE_COLOR;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_EXPAND_SECTIONS;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_REQUIRED_FIELD_BACKGROUND;
import static org.eventb.internal.ui.preferences.PreferenceConstants.P_TEXT_FOREGROUND;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.internal.ui.preferences.tactics.TacticPreferenceUtils;

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

		TacticPreferenceUtils.initializeDefault(store);

		// Default value for editor pages
		MachineEditorPagesPreference.getDefault().setDefault();
		ContextEditorPagesPreference.getDefault().setDefault();

		// Default values for borders and colors and fonts
		store.setDefault(P_BORDER_ENABLE, true);

		// Default value consider hidden hypotheses
		store.setDefault(P_CONSIDER_HIDDEN_HYPOTHESES, false);

		// Default value for section expanding
		store.setDefault(P_EXPAND_SECTIONS, true);

		// Default colors
		setDefault(store, P_TEXT_FOREGROUND, RGB_DARK_GREEN);
		setDefault(store, P_COMMENT_FOREGROUND, RGB_DARK_GREEN);
		setDefault(store, P_REQUIRED_FIELD_BACKGROUND, RGB_YELLOW);
		setDefault(store, P_DIRTY_STATE_COLOR, RGB_YELLOW);
		setDefault(store, P_BOX_BORDER_COLOR, RGB_RED);

		// Set the values for context element prefixes
		PreferenceUtils.setDefaultPreferences(store);
	}

}