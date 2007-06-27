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
		
		// Default value for tactic registry
		String [] tacticIDs = EventBPlugin.getDefault().getPostTacticRegistry()
				.getTacticIDs();
		store.setDefault(PreferenceConstants.P_POSTTACTIC_DETAILS, ProverUIUtils
				.toCommaSeparatedList(tacticIDs));
	}

}
