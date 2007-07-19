package org.eventb.internal.core.pm;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ITacticContainerRegistry;

public class PostTacticContainerRegistry extends TacticContainerRegistry
		implements ITacticContainerRegistry {

	// The identifier of the extension point (value
	// <code>"org.eventb.core.postTactics"</code>).
	private final static String POSTTACTICS_ID = EventBPlugin.PLUGIN_ID
			+ ".postTactics";	

	// The static instance of this singleton class
	private static ITacticContainerRegistry instance;

	/**
	 * Constructor.
	 * <p>
	 * A private constructor to prevent creating an instance of this class
	 * directly
	 */
	private PostTacticContainerRegistry() {
		// Singleton to hide the constructor
		// Must call super(String registryID)
		super(POSTTACTICS_ID);
	}

	/**
	 * Getting the default instance of this class (create a new instance of it
	 * does not exist before)
	 * <p>
	 * 
	 * @return An instance of this class
	 */
	public static ITacticContainerRegistry getDefault() {
		if (instance == null)
			instance = new PostTacticContainerRegistry();
		return instance;
	}

	public String[] getDefaultTacticIDs() {
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
