package org.eventb.internal.ui.preferences;

import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eventb.core.EventBPlugin;

/**
 * @author htson
 *         <p>
 */
public class PostTacticPreferencePage
	extends TacticContainerPreferencePage
	implements
		IWorkbenchPreferencePage {

	public PostTacticPreferencePage() {
		super("Preferences for the Post Tactic apply after every tactic application",
				PreferenceConstants.P_POSTTACTIC_ENABLE, "Enable &post-tactic for proving",
				PreferenceConstants.P_POSTTACTICS,"&Tactics are run as post-tactics");
	}

	@Override
	protected void setTacticContainer() {
		tacticContainer = EventBPlugin.getDefault().getUserSupportManager()
				.getPostTacticContainer();
	}

}