package org.eventb.internal.ui.preferences;

import java.util.List;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.core.EventBPlugin;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
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
		List<ITacticDescriptor> defaultPostTacticDescriptors = EventBPlugin
				.getPostTacticPreference().getDefaultDescriptors();
		String[] postTacticIDs = new String[defaultPostTacticDescriptors.size()];
		int i = 0;
		for (ITacticDescriptor tacticDesc : defaultPostTacticDescriptors) {
			postTacticIDs[i] = tacticDesc.getTacticID();
			++i;
		}
		store.setDefault(PreferenceConstants.P_POSTTACTICS, ProverUIUtils
				.toCommaSeparatedList(postTacticIDs));
		store.setDefault(PreferenceConstants.P_POSTTACTIC_ENABLE, true);

		// Default value for auto-tactic registry			
		List<ITacticDescriptor> defaultAutoTacticDescriptors = EventBPlugin
				.getPOMTacticPreference().getDefaultDescriptors();
		String[] autoTacticIDs = new String[defaultAutoTacticDescriptors.size()];
		i = 0;
		for (ITacticDescriptor tacticDesc : defaultAutoTacticDescriptors) {
			autoTacticIDs[i] = tacticDesc.getTacticID();
			++i;
		}
		store.setDefault(PreferenceConstants.P_AUTOTACTICS, ProverUIUtils
				.toCommaSeparatedList(autoTacticIDs));
		store.setDefault(PreferenceConstants.P_AUTOTACTIC_ENABLE, true);
	}

}
