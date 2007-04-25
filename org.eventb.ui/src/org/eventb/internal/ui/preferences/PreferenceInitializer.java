package org.eventb.internal.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.core.seqprover.SequentProver;
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
		String[] registeredIDs = SequentProver.getTacticRegistry().getRegisteredIDs();
		StringBuffer buffer = new StringBuffer();
		boolean sep = false;
		for (Object item : registeredIDs) {
			if (sep) {
				sep = true;
			}
			else {
				buffer.append(",");
			}
			buffer.append(item);
		}
		store.setDefault(PreferenceConstants.P_PROVINGMODE, buffer.toString());
	}

}
