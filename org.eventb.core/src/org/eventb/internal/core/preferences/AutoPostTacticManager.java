/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
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
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_AUTOTACTIC_CHOICE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_POSTTACTIC_CHOICE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_TACTICSPROFILES;
import static org.eventb.core.preferences.autotactics.TacticPreferenceFactory.makeTacticPreferenceMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eventb.core.IEventBRoot;
import org.eventb.core.preferences.CachedPreferenceMap;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.preferences.autotactics.IAutoPostTacticManager;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.pm.PostTacticPreference;
import org.eventb.internal.core.pom.POMTacticPreference;

/**
 * Facade for the managment of auto and post tactics.
 * 
 * @author "Thomas Muller"
 */
public class AutoPostTacticManager implements IAutoPostTacticManager {

	private static final IAutoTacticPreference postTacPref = PostTacticPreference
			.getDefault();
	private static final IAutoTacticPreference autoTacPref = POMTacticPreference
			.getDefault();

	private static IAutoPostTacticManager INSTANCE;

	private final IPreferencesService preferencesService;

	// Profiles cache to avoid systematic re-calculation of the profiles.
	private final CachedPreferenceMap<ITacticDescriptor> profilesCache;

	private AutoPostTacticManager() {
		profilesCache = makeTacticPreferenceMap();
		preferencesService = Platform.getPreferencesService();
		autoTacPref.setSelectedDescriptor(autoTacPref.getDefaultDescriptor());
		postTacPref.setSelectedDescriptor(postTacPref.getDefaultDescriptor());
	}

	public static IAutoPostTacticManager getDefault() {
		if (INSTANCE == null)
			INSTANCE = new AutoPostTacticManager();
		return INSTANCE;
	}

	@Override
	public ITactic getSelectedAutoTactics(IEventBRoot root) {
		final IProject project = root.getRodinProject().getProject();
		return getSelectedComposedTactics(project, true);
	}

	@Override
	public ITactic getSelectedPostTactics(IEventBRoot root) {
		final IProject project = root.getRodinProject().getProject();
		return getSelectedComposedTactics(project, false);
	}

	private ITactic getSelectedComposedTactics(IProject project, boolean auto) {
		final IScopeContext sc = new ProjectScope(project);
		PreferenceUtils.restoreFromUIIfNeeded(InstanceScope.INSTANCE.getNode(PLUGIN_ID), false);
		PreferenceUtils.restoreFromUIIfNeeded(sc.getNode(PLUGIN_ID), false);
		final IScopeContext[] contexts = { sc };
		final String profiles = preferencesService.getString(PLUGIN_ID,
				P_TACTICSPROFILES, null, contexts);
		if (profiles == null) {
			// The preference was not initialized, this should not happen
			// We return the selected tactics
			Util.log(null, "Tactic preference has not been initialized");
			return auto ? autoTacPref.getSelectedComposedTactic() : postTacPref
					.getSelectedComposedTactic();
		}
		profilesCache.inject(profiles);
		final String choice;
		if (auto) {
			choice = preferencesService.getString(PLUGIN_ID,
					P_AUTOTACTIC_CHOICE, null, contexts);
		} else { // (type.equals(POST_TACTICS_TAG))
			choice = preferencesService.getString(PLUGIN_ID,
					P_POSTTACTIC_CHOICE, null, contexts);			
		}
		return getCorrespondingTactic(choice, auto);
	}

	private ITactic getCorrespondingTactic(String choice, boolean auto) {
		final ITactic composedTactic;
		if (auto)
			composedTactic = getTactic(choice, autoTacPref);
		else
			composedTactic = getTactic(choice, postTacPref);
		if (composedTactic == null) {
			final String tacType = (auto) ? "auto" : "post";
			Util.log(null, "Failed to load the tactic profile " + choice
					+ " for the tactic " + tacType + "tactic");
			return BasicTactics.failTac("Could not load the profile " + choice
					+ " from the cache for the tactic " + tacType + "tactic");
		}
		return composedTactic;
	}

	private ITactic getTactic(String choice, IAutoTacticPreference pref) {
		final IPrefMapEntry<ITacticDescriptor> entry = profilesCache
				.getEntry(choice);
		if (entry == null) {
			return null;
		}
		final ITacticDescriptor profileDescriptor = entry.getValue();
		if (profileDescriptor == null) {
			return null;
		}
		pref.setSelectedDescriptor(profileDescriptor);
		return pref.getSelectedComposedTactic();
	}

	@Override
	public IAutoTacticPreference getAutoTacticPreference() {
		return autoTacPref;
	}

	@Override
	public IAutoTacticPreference getPostTacticPreference() {
		return postTacPref;
	}

}
