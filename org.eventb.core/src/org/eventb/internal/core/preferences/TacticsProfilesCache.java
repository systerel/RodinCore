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

import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.DEFAULT_AUTO_TACTIC;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.DEFAULT_POST_TACTIC;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_TACTICSPROFILES;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.preferences.autotactics.IInjectLog;
import org.eventb.core.preferences.autotactics.ITacticProfileCache;
import org.eventb.core.seqprover.ITacticDescriptor;

public class TacticsProfilesCache extends
		StorablePreferenceMap<ITacticDescriptor> implements ITacticProfileCache {

	private static final Set<String> DEFAULT_PROFILES = new HashSet<String>(
			Arrays.asList(DEFAULT_AUTO_TACTIC, DEFAULT_POST_TACTIC));
	
	public TacticsProfilesCache(IEclipsePreferences preferenceNode) {
		super(preferenceNode, P_TACTICSPROFILES, new PrefUnitTranslator(),
				TacticReferenceMaker.getInstance());
	}

	// Adds plug-in contributed tactic profiles to this profile cache.
	// It may happen that some profiles cannot be added.
	// Possible reasons are:
	// * collision with same plug-in contribution serialized (normal situation)
	//   => don't override plug-in profile, set profile name as default key,
	//      don't warn about anything
	// * collision with older version of the plug-in contribution
	//   => TODO guess which is newer, override older with newer (?),
	//      set as default profile, don't warn (?)
	// * collision with user profile
	//   => don't override user profile, FIXME don't set as default profile,
	//      TODO warn (log) in this case only
	private void addProfileContributions() {
		final TacticProfileContributions contribs = TacticProfileContributions
				.getInstance();
		final List<IPrefMapEntry<ITacticDescriptor>> profiles = contribs
				.getProfiles();
		addDefaultKeys(contribs.getProfileNames());
		if (!profiles.isEmpty()) {
			addAll(profiles);
			// TODO process not added profiles using return value of addAll()
		}
	}
	
	@Override
	public IInjectLog inject(String pref) throws IllegalArgumentException {
		final IInjectLog log = super.inject(pref);
		addDefaultKeys(DEFAULT_PROFILES);
		addProfileContributions();
		return log;
	}
}
