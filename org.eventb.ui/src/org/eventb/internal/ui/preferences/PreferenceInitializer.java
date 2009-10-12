/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added "show borders" and "font color" options
 *     Systerel - used EventBPreferenceStore
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import java.util.List;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eventb.core.EventBPlugin;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.UIUtils;

/**
 * @author htson
 *         <p>
 *         Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = EventBPreferenceStore.getPreferenceStore();
		
		// Default value for post-tactic registry
		List<ITacticDescriptor> defaultPostTacticDescriptors = EventBPlugin
				.getPostTacticPreference().getDefaultDescriptors();
		String[] postTacticIDs = new String[defaultPostTacticDescriptors.size()];
		int i = 0;
		for (ITacticDescriptor tacticDesc : defaultPostTacticDescriptors) {
			postTacticIDs[i] = tacticDesc.getTacticID();
			++i;
		}
		store.setDefault(PreferenceConstants.P_POSTTACTICS, UIUtils
				.toCommaSeparatedList(postTacticIDs));
		store.setDefault(PreferenceConstants.P_POSTTACTIC_ENABLE, true);

		// Default value for auto-tactic registry
		List<ITacticDescriptor> defaultAutoTacticDescriptors = EventBPlugin
				.getAutoTacticPreference().getDefaultDescriptors();
		String[] autoTacticIDs = new String[defaultAutoTacticDescriptors.size()];
		i = 0;
		for (ITacticDescriptor tacticDesc : defaultAutoTacticDescriptors) {
			autoTacticIDs[i] = tacticDesc.getTacticID();
			++i;
		}
		store.setDefault(PreferenceConstants.P_AUTOTACTICS, UIUtils
				.toCommaSeparatedList(autoTacticIDs));
		store.setDefault(PreferenceConstants.P_AUTOTACTIC_ENABLE, true);

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
		
		PreferenceConverter.setDefault(store,
				PreferenceConstants.P_TEXT_FOREGROUND, EventBSharedColor
						.getSystemColor(SWT.COLOR_DARK_GREEN).getRGB());
		
		PreferenceConverter.setDefault(store,
				PreferenceConstants.P_COMMENT_FOREGROUND, EventBSharedColor
						.getSystemColor(SWT.COLOR_DARK_GREEN).getRGB());
		
		PreferenceConverter
				.setDefault(store,
						PreferenceConstants.P_REQUIRED_FIELD_BACKGROUND,
						EventBSharedColor.getSystemColor(SWT.COLOR_YELLOW)
								.getRGB());
		
		PreferenceConverter.setDefault(store,
				PreferenceConstants.P_DIRTY_STATE_COLOR, EventBSharedColor
						.getSystemColor(SWT.COLOR_YELLOW).getRGB());

		PreferenceConverter.setDefault(store,
				PreferenceConstants.P_BOX_BORDER_COLOR, EventBSharedColor
						.getSystemColor(SWT.COLOR_RED).getRGB());
	}
}