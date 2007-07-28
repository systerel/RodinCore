package org.eventb.internal.core.pom;

import org.eventb.core.EventBPlugin;
import org.eventb.core.seqprover.autoTacticPreference.AutoTacticPreference;

public class POMTacticPreference extends AutoTacticPreference {

	// The identifier of the extension point (value
	// <code>"org.eventb.core.autoTactics"</code>).
	private final static String AUTOTACTICS_ID = EventBPlugin.PLUGIN_ID
			+ ".pomTactics";	

	private static POMTacticPreference instance;

	private POMTacticPreference() {
		// Singleton: Private default constructor
		super(AUTOTACTICS_ID);
	}

	public static POMTacticPreference getDefault() {
		if (instance == null)
			instance = new POMTacticPreference();
		return instance;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sequenprover.tacticPreference.TacticPreference#getDefaultIDs()
	 */
	@Override
	protected String [] getDefaultIDs() {
		return new String[] {
				"org.eventb.core.seqprover.autoRewriteTac",
		};
	}

}
