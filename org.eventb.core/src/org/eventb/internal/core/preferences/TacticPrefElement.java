/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.preferences;

import org.eventb.core.preferences.IPrefElementTranslator;
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
		return tacticDescriptor;
	}

	private void printDebug(String msg) {
		if (PreferenceUtils.DEBUG) {
			System.out.println(msg);
		}
	}
}