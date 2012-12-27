/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.preferences;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.preferences.IReferenceMaker;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;

/**
 * @author Nicolas Beauger
 * 
 */
public class TacticReferenceMaker implements IReferenceMaker<ITacticDescriptor> {

	private static final TacticReferenceMaker INSTANCE = new TacticReferenceMaker();

	public static TacticReferenceMaker getInstance() {
		return INSTANCE;
	}

	private TacticReferenceMaker() {
		// singleton
	}

	@Override
	public ITacticDescriptor makeReference(
			IPrefMapEntry<ITacticDescriptor> prefEntry) {
		return new TacticDescriptorRef(prefEntry);
	}

	@Override
	public String[] getReferencedKeys(ITacticDescriptor pref) {
		final Set<String> keys = new HashSet<String>();
		addRefrencedKeys(pref, keys);
		return keys.toArray(new String[keys.size()]);
	}

	private void addRefrencedKeys(ITacticDescriptor pref, Set<String> keys) {
		if (pref instanceof ITacticDescriptorRef) {
			final IPrefMapEntry<ITacticDescriptor> prefEntry = ((ITacticDescriptorRef) pref)
					.getPrefEntry();
			keys.add(prefEntry.getKey());
		} else if (pref instanceof ICombinedTacticDescriptor) {
			final List<ITacticDescriptor> combined = ((ICombinedTacticDescriptor) pref)
					.getCombinedTactics();
			for (ITacticDescriptor comb : combined) {
				addRefrencedKeys(comb, keys);
			}
		}

	}

}
