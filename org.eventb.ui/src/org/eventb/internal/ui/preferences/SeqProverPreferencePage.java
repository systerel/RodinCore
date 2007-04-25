package org.eventb.internal.ui.preferences;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eventb.ui.EventBUIPlugin;

public class SeqProverPreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage {

	TwoListSelectionEditor tacticsEditor;
	
	public SeqProverPreferencePage() {
		super();
		setPreferenceStore(EventBUIPlugin.getDefault().getPreferenceStore());
		setDescription("Preferences for the Sequence Prover");
	}

	@Override
	public void init(IWorkbench workbench) {
		// Do nothing
	}

}