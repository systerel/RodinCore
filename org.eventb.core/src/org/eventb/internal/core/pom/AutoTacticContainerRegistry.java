package org.eventb.internal.core.pom;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ITacticContainerRegistry;
import org.eventb.internal.core.pm.TacticContainerRegistry;

public class AutoTacticContainerRegistry extends TacticContainerRegistry
		implements ITacticContainerRegistry {

	// The identifier of the extension point (value
	// <code>"org.eventb.core.autoTactics"</code>).
	private final static String AUTOTACTICS_ID = EventBPlugin.PLUGIN_ID
			+ ".autoTactics";	

	// The static instance of this singleton class
	private static ITacticContainerRegistry instance;

	/**
	 * Constructor.
	 * <p>
	 * A private constructor to prevent creating an instance of this class
	 * directly
	 */
	private AutoTacticContainerRegistry() {
		// Singleton to hide the constructor
		// Must call super(String registryID)
		super(AUTOTACTICS_ID);
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
			instance = new AutoTacticContainerRegistry();
		return instance;
	}

	public String[] getDefaultTacticIDs() {
		return new String[] {
		            "org.eventb.core.seqprover.autoRewriteTac",
		};
	}
	
}
