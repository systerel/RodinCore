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

import static org.eventb.core.EventBPlugin.getAutoPostTacticManager;

import org.eventb.core.preferences.IPrefElementTranslator;
import org.eventb.core.preferences.autotactics.IAutoPostTacticManager;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.SequentProver;

/**
 * The preference element translator for tactic descriptors.
 * 
 * @since 2.1
 */
public class TacticPrefElement implements
		IPrefElementTranslator<ITacticDescriptor> {

	@Override
	public String extract(ITacticDescriptor desc) {
		return desc.getTacticID();
	}

	@Override
	public ITacticDescriptor inject(String str) {
		final IAutoTacticRegistry tacticRegistry = SequentProver
				.getAutoTacticRegistry();
		if (!tacticRegistry.isRegistered(str)) {
			printDebug("Trying to inject a tactic which is not registered "
					+ str);
			return null;
		}

		final ITacticDescriptor tacticDescriptor = tacticRegistry
				.getTacticDescriptor(str);
		if (!isDeclared(tacticDescriptor)) {
			printDebug("Tactic is not declared in this scope" + str);
			return null;

		}
		return tacticDescriptor;
	}

	private void printDebug(String msg) {
		if (PreferenceUtils.DEBUG) {
			System.out.println(msg);
		}
	}

	private boolean isDeclared(ITacticDescriptor tacticDesc) {
		final IAutoPostTacticManager manager = getAutoPostTacticManager();
		if (manager.getAutoTacticPreference().isDeclared(tacticDesc))
			return true;
		return manager.getPostTacticPreference().isDeclared(tacticDesc);
	}

}