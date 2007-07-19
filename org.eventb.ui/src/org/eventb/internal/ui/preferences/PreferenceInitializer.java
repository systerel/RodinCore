package org.eventb.internal.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.core.EventBPlugin;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.ui.EventBUIPlugin;

/**
 * Class used to initialize default preference values.
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
		store.setDefault(PreferenceConstants.P_PROOFPAGE_AUTOLAYOUT, true);
		
		// Default value for post-tactic registry
		String[] postTacticIDs = EventBPlugin.getDefault().getUserSupportManager()
				.getPostTacticContainer().getTacticContainerRegistry()
				.getDefaultTacticIDs();
				
		store.setDefault(PreferenceConstants.P_POSTTACTICS, ProverUIUtils
				.toCommaSeparatedList(postTacticIDs));
		store.setDefault(PreferenceConstants.P_POSTTACTIC_ENABLE, true);

		// Default value for auto-tactic registry
		String[] autoTacticIDs = EventBPlugin.getDefault().getUserSupportManager()
				.getAutoTacticContainer().getTacticContainerRegistry()
				.getDefaultTacticIDs();
				
		store.setDefault(PreferenceConstants.P_AUTOTACTICS, ProverUIUtils
				.toCommaSeparatedList(autoTacticIDs));
		store.setDefault(PreferenceConstants.P_AUTOTACTIC_ENABLE, true);
	}

}
