/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.core.preferences.autotactics;

import static org.eventb.internal.core.preferences.PreferenceUtils.loopOnAllPending;

import java.util.List;

import org.eventb.core.preferences.CachedPreferenceMap;
import org.eventb.core.preferences.IPrefElementTranslator;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.preferences.IReferenceMaker;
import org.eventb.core.preferences.IXMLPrefSerializer;
import org.eventb.core.preferences.ListPreference;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.preferences.PrefUnitTranslator;
import org.eventb.internal.core.preferences.PreferenceUtils.PreferenceException;
import org.eventb.internal.core.preferences.TacticPrefElement;
import org.eventb.internal.core.preferences.TacticReferenceMaker;

/**
 * @since 2.1
 */
public class TacticPreferenceFactory {

	/**
	 * Returns the old preference format translator
	 * 
	 * @return a preference translator
	 * @deprecated use {@link #makeTacticXMLSerializer()} for new format
	 */
	@Deprecated
	public static IPrefElementTranslator<ITacticDescriptor> getTacticPrefElement() {
		return new TacticPrefElement();
	}

	/**
	 * Recovers a preference stored using old format into a preference map using
	 * new format.
	 * 
	 * @param oldPref
	 *            a serialized preference
	 * @return a new preference map, or <code>null</code> if recover failed.
	 * @since 2.3
	 */
	@SuppressWarnings("deprecation")
	public static CachedPreferenceMap<ITacticDescriptor> recoverOldPreference(String oldPref) {
		final IPrefElementTranslator<List<ITacticDescriptor>> oldPreference = new ListPreference<ITacticDescriptor>(
				TacticPreferenceFactory.getTacticPrefElement());
		final CachedPreferenceMap<List<ITacticDescriptor>> oldCache = new CachedPreferenceMap<List<ITacticDescriptor>>(
				oldPreference);
		try {
			oldCache.inject(oldPref);
		} catch (PreferenceException x) {
			Util.log(x, "while trying to recover tactic preference");
			// give up
			return null;
		}
		
		final CachedPreferenceMap<ITacticDescriptor> newPrefMap = makeTacticPreferenceMap();
		// adapt old cache to new cache
		
		for (IPrefMapEntry<List<ITacticDescriptor>> entry : oldCache
				.getEntries()) {
			final String id = entry.getKey();
			final List<ITacticDescriptor> value = entry.getValue();
			final ITacticDescriptor tac = loopOnAllPending(value, id);
			newPrefMap.add(id, tac);
		}
		return newPrefMap;

	}
	
	/**
	 * Returns a xml preference serializer for preference units of tactic
	 * descriptors.
	 * 
	 * @return a xml preference serializer
	 * @since 2.3
	 */
	public static IXMLPrefSerializer<ITacticDescriptor> makeTacticXMLSerializer() {
		return new PrefUnitTranslator();
	}

	/**
	 * Returns a tactic descriptor reference reference maker.
	 * 
	 * @return a tactic descriptor reference maker
	 * @since 2.3
	 */
	public static IReferenceMaker<ITacticDescriptor> makeTacticRefMaker() {
		return TacticReferenceMaker.getInstance();
	}

	/**
	 * Returns a preference map for tactics with combined, parameterized tactics
	 * and references enabled.
	 * 
	 * @return a preference map
	 * @since 2.3
	 */
	public static CachedPreferenceMap<ITacticDescriptor> makeTacticPreferenceMap() {
		return new CachedPreferenceMap<ITacticDescriptor>(
				makeTacticXMLSerializer(), makeTacticRefMaker());
	}

}