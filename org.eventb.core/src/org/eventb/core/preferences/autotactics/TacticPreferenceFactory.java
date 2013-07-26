/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.preferences.autotactics;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eventb.core.preferences.CachedPreferenceMap;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.internal.core.preferences.PrefUnitTranslator;
import org.eventb.internal.core.preferences.PreferenceUtils.UnresolvedPrefMapEntry;
import org.eventb.internal.core.preferences.TacticDescriptorRef;
import org.eventb.internal.core.preferences.TacticReferenceMaker;
import org.eventb.internal.core.preferences.TacticsProfilesCache;

/**
 * API for making tactic preference related instances.
 * 
 * @since 2.1
 */
public class TacticPreferenceFactory {

	/**
	 * Returns a preference map for tactics with combined, parameterized tactics
	 * and references enabled.
	 * 
	 * @return a preference map
	 * @since 2.3
	 */
	public static CachedPreferenceMap<ITacticDescriptor> makeTacticPreferenceMap() {
		return new CachedPreferenceMap<ITacticDescriptor>(
				new PrefUnitTranslator(), TacticReferenceMaker.getInstance());
	}

	/**
	 * Returns a new tactic profile cache instance.
	 * 
	 * @param preferenceNode
	 *            the preference node to load from and store to.
	 * @since 3.0
	 */
	public static ITacticProfileCache makeTacticProfileCache(
			IEclipsePreferences preferenceNode) {
		return new TacticsProfilesCache(preferenceNode);
	}

	/**
	 * Makes a profile reference to be used in another profile.
	 * 
	 * @param profileName
	 *            the name of the referenced profile.
	 * @return a profile reference as tactic descriptor
	 * @since 3.0
	 */
	public static ITacticDescriptor makeProfileReference(String profileName) {
		final IPrefMapEntry<ITacticDescriptor> entry = new UnresolvedPrefMapEntry<ITacticDescriptor>(
				profileName);
		return new TacticDescriptorRef(entry);
	}
}
