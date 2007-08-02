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
	            "org.eventb.core.seqprover.trueGoalTac",
	            "org.eventb.core.seqprover.falseHypTac",
	            "org.eventb.core.seqprover.goalInHypTac",
	            "org.eventb.core.seqprover.goalDisjInHypTac",
	            "org.eventb.core.seqprover.funGoalTac",
	            "org.eventb.core.seqprover.typeRewriteTac",
	            "org.eventb.core.seqprover.autoRewriteTac",
	            "org.eventb.core.seqprover.existsHypTac",
	            "org.eventb.core.seqprover.findContrHypsTac",
	            "org.eventb.core.seqprover.eqHypTac",
	            "org.eventb.core.seqprover.shrinkImpHypTac",
	            "org.eventb.core.seqprover.shrinkEnumHypTac",
	            "org.eventb.core.seqprover.clarifyGoalTac"
		};

	}

}
