/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.preferences;

import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_AUTOTACTIC_CHOICE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_POSTTACTIC_CHOICE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_TACTICSPROFILES;
import static org.eventb.internal.core.preferences.PreferenceUtils.loopOnAllPending;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eventb.core.IEventBRoot;
import org.eventb.core.preferences.CachedPreferenceMap;
import org.eventb.core.preferences.IPrefElementTranslator;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.preferences.ListPreference;
import org.eventb.core.preferences.autotactics.IAutoPostTacticManager;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.pm.PostTacticPreference;
import org.eventb.internal.core.pom.POMTacticPreference;
import org.eventb.internal.core.preferences.PreferenceUtils.PreferenceException;

/**
 * Facade for the managment of auto and post tactics.
 * 
 * @author "Thomas Muller"
 */
public class AutoPostTacticManager implements IAutoPostTacticManager {

	/**
	 * This string identifies the qualifier used to store tactic preferences by
	 * the UI. It shows a dependency to the UI which is the price to pay if one
	 * wants to use the PreferenceStore API owned by Jface.
	 */
	private static final String PREF_QUALIFIER = "org.eventb.ui";

	private static final IAutoTacticPreference postTacPref = PostTacticPreference
			.getDefault();
	private static final IAutoTacticPreference autoTacPref = POMTacticPreference
			.getDefault();

	private static IAutoPostTacticManager INSTANCE;

	private final IPreferencesService preferencesService;

	// Profiles cache to avoid systematic re-calculation of the profiles.
	private final CachedPreferenceMap<ITacticDescriptor> profilesCache;

	private AutoPostTacticManager() {
		final IPrefElementTranslator<ITacticDescriptor> preferenceElement = new TacticPrefElement();
		profilesCache = new CachedPreferenceMap<ITacticDescriptor>(
				preferenceElement);
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
		final IScopeContext[] contexts = { sc };
		final String profiles = preferencesService.getString(PREF_QUALIFIER,
				P_TACTICSPROFILES, null, contexts);
		// Case where the UI was not initialized (e.g. tests)
		// We return the selected tactics
		if (profiles == null) {
			return auto ? autoTacPref.getSelectedComposedTactic() : postTacPref
					.getSelectedComposedTactic();
		}
		try {
			profilesCache.inject(profiles);
		} catch (PreferenceException e) {
			// backward compatibility: was stored as lists of tactics
			// try to recover
			final boolean success = recover(profiles);
			if (!success) {
				Util.log(e, "while loading tactic preference:\n" + profiles);
			}
		}
		final String choice;
		if (auto) {
			choice = preferencesService.getString(PREF_QUALIFIER,
					P_AUTOTACTIC_CHOICE, null, contexts);
		} else { // (type.equals(POST_TACTICS_TAG))
			choice = preferencesService.getString(PREF_QUALIFIER,
					P_POSTTACTIC_CHOICE, null, contexts);			
		}
		return getCorrespondingTactic(choice, auto);
	}

	private boolean recover(String profiles) {
		final IPrefElementTranslator<List<ITacticDescriptor>> oldPreference = new ListPreference<ITacticDescriptor>(
				new TacticPrefElement());
		final CachedPreferenceMap<List<ITacticDescriptor>> oldCache = new CachedPreferenceMap<List<ITacticDescriptor>>(
				oldPreference);
		try {
			oldCache.inject(profiles);
		} catch (PreferenceException x) {
			Util.log(x, "while trying to recover tactic preference");
			// give up
			return false;
		}
		for (IPrefMapEntry<List<ITacticDescriptor>> entry : oldCache
				.getEntries()) {
			final String id = entry.getKey();
			final List<ITacticDescriptor> value = entry.getValue();
			final ITacticDescriptor tac = loopOnAllPending(value, id);
			profilesCache.add(id, tac);
		}
		return true;
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
