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
import org.eventb.core.preferences.IReferenceMaker;
import org.eventb.core.preferences.IXMLPrefSerializer;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.internal.core.preferences.PrefUnitTranslator;
import org.eventb.internal.core.preferences.TacticReferenceMaker;
import org.eventb.internal.core.preferences.TacticsProfilesCache;

/**
 * TODO move everything to non published area as preferences get stored in core
 * plug-in.
 * 
 * @since 2.1
 */
public class TacticPreferenceFactory {

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

	/**
	 * Returns a new tactic profile cache instance.
	 * 
	 * @param preferenceNode
	 *            the preference node to load from and store to.
	 * @since 3.0
	 */
	public static ITacticProfileCache makeTacticProfileCache(IEclipsePreferences preferenceNode) {
		return new TacticsProfilesCache(preferenceNode);
	}
}
