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

import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_TACTICSPROFILES;
import static org.eventb.core.preferences.autotactics.TacticPreferenceFactory.makeTacticRefMaker;
import static org.eventb.core.preferences.autotactics.TacticPreferenceFactory.makeTacticXMLSerializer;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eventb.core.preferences.autotactics.ITacticProfileCache;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;

public class TacticsProfilesCache extends
		StorablePreferenceMap<ITacticDescriptor> implements ITacticProfileCache {

	public TacticsProfilesCache(IEclipsePreferences preferenceNode) {
		super(preferenceNode, P_TACTICSPROFILES, makeTacticXMLSerializer(),
				makeTacticRefMaker());
	}

}
