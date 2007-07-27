package org.eventb.internal.core.pm;

import org.eventb.core.EventBPlugin;
import org.eventb.core.seqprover.autoTacticPreference.AutoTacticPreference;

public class PostTacticPreference extends AutoTacticPreference {

	// The identifier of the extension point (value
	// <code>"org.eventb.core.postTactics"</code>).
	private final static String POSTTACTICS_ID = EventBPlugin.PLUGIN_ID
			+ ".postTactics";	

	private static PostTacticPreference instance;

	private PostTacticPreference() {
		// Singleton: Private default constructor
		super(POSTTACTICS_ID);
	}

	public static PostTacticPreference getDefault() {
		if (instance == null)
			instance = new PostTacticPreference();
		return instance;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sequenprover.tacticPreference.TacticPreference#getDefaultIDs()
	 */
	@Override
	protected String [] getDefaultIDs() {
		return new String[] {
	            "org.eventb.core.seqprover.autoFalsifyHypTac",
				"org.eventb.core.seqprover.autoHypOrTac",
				"org.eventb.core.seqprover.isFunGoalTac",
				"org.eventb.core.seqprover.autoRewriteTac",
				"org.eventb.core.seqprover.autoExFTac",
				"org.eventb.core.seqprover.autoNegEnumTac",
				"org.eventb.core.seqprover.autoImpAndHypTac",
				"org.eventb.core.seqprover.autoImpOrHypTac",
				"org.eventb.core.seqprover.autoImpETac",
				"org.eventb.core.seqprover.autoEqETac",
				"org.eventb.core.seqprover.normTac"
		};

	}

}
