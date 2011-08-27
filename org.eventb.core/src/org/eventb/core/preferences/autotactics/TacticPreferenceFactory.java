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

import org.eventb.core.preferences.CachedPreferenceMap;
import org.eventb.core.preferences.IPrefElementTranslator;
import org.eventb.core.preferences.IReferenceMaker;
import org.eventb.core.preferences.IXMLPrefSerializer;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.internal.core.preferences.PrefUnitTranslator;
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
