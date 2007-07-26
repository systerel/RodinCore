package org.eventb.internal.ui.preferences;

import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eventb.core.EventBPlugin;

/**
 * @author htson
 *         <p>
 */
public class AutoTacticPreferencePage
	extends TacticPreferencePage
	implements
		IWorkbenchPreferencePage {

	public AutoTacticPreferencePage() {
		super("Preferences for the Automatic Tactic",
				PreferenceConstants.P_AUTOTACTIC_ENABLE, "&Enable auto-tactic for proving",
				PreferenceConstants.P_AUTOTACTICS,"&Tactics are run as auto-tactics");
	}

	@Override
	protected void setTacticPreference() {
		tacticPreference = EventBPlugin.getAutoTacticPreference();
	}

}