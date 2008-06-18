/*******************************************************************************
 * Copyright (c) 2006-2008 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.preferences;

import java.util.List;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.core.EventBPlugin;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;

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
		IPreferenceStore store = EventBUIPlugin.getDefault()
				.getPreferenceStore();
		
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
		
		store.setDefault(PreferenceConstants.P_BORDER_ENABLE, true);
	}

}
