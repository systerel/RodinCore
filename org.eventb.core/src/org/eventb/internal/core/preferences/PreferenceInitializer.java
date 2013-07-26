/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.preferences;

import static org.eventb.core.EventBPlugin.PLUGIN_ID;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.DEFAULT_AUTO_TACTIC;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.DEFAULT_POST_TACTIC;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_AUTOTACTIC_CHOICE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_AUTOTACTIC_ENABLE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_CONSIDER_HIDDEN_HYPOTHESES;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_POSTTACTIC_CHOICE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_POSTTACTIC_ENABLE;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eventb.core.EventBPlugin;
import org.eventb.core.preferences.autotactics.IAutoPostTacticManager;
import org.eventb.core.seqprover.ITacticDescriptor;

/**
 * Initializer for plug-in preferences.
 * 
 * @author beauger
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public static final boolean DEFAULT_POST_ENABLE = true;
	public static final boolean DEFAULT_AUTO_ENABLE = true;

	@Override
	public void initializeDefaultPreferences() {
		final IEclipsePreferences defaultNode = DefaultScope.INSTANCE
				.getNode(PLUGIN_ID);

		// Default value for post-tactic registry
		defaultNode.putBoolean(P_POSTTACTIC_ENABLE, DEFAULT_POST_ENABLE);
		defaultNode.put(P_POSTTACTIC_CHOICE, DEFAULT_POST_TACTIC);

		// Default value for auto-tactic registry
		defaultNode.putBoolean(P_AUTOTACTIC_ENABLE, DEFAULT_AUTO_ENABLE);
		defaultNode.put(P_AUTOTACTIC_CHOICE, DEFAULT_AUTO_TACTIC);

		// Default value consider hidden hypotheses
		defaultNode.putBoolean(P_CONSIDER_HIDDEN_HYPOTHESES, false);

		// Default value for profile list
		final IAutoPostTacticManager manager = EventBPlugin
				.getAutoPostTacticManager();

		final ITacticDescriptor defaultAutoTactic = manager
				.getAutoTacticPreference().getDefaultDescriptor();
		final ITacticDescriptor defaultPostTactic = manager
				.getPostTacticPreference().getDefaultDescriptor();
		final TacticsProfilesCache tacticsCache = new TacticsProfilesCache(
				defaultNode);
		tacticsCache.add(DEFAULT_AUTO_TACTIC, defaultAutoTactic);
		tacticsCache.add(DEFAULT_POST_TACTIC, defaultPostTactic);
		tacticsCache.storeDefault();
	}

}
