/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.prover.tests;

import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_AUTOTACTIC_ENABLE;
import static org.eventb.internal.ui.preferences.EventBPreferenceStore.getPreferenceStore;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.commands.IHandler;
import org.eventb.internal.ui.preferences.ToggleAutoTacticPreference;
import org.junit.Test;

/**
 * Acceptance tests for the "org.eventb.ui.project.autoTactic" command handler.
 * 
 * @author Laurent Voisin
 */
public class TestHandler {

	/**
	 * The command handler can enable auto-tactics
	 */
	@Test
	public void testProjectAutoTacticEnable() throws Exception {
		setAutoTacticPreference(false);
		assertFalse(getAutoTacticPreference());
		runHandler();
		assertTrue(getAutoTacticPreference());
	}

	/**
	 * The command handler can disable auto-tactics
	 */
	@Test
	public void testProjectAutoTacticDisable() throws Exception {
		setAutoTacticPreference(true);
		assertTrue(getAutoTacticPreference());
		runHandler();
		assertFalse(getAutoTacticPreference());
	}

	private void runHandler() throws Exception {
		final IHandler handler = new ToggleAutoTacticPreference();
		handler.execute(null);
	}

	private boolean getAutoTacticPreference() {
		return getPreferenceStore().getBoolean(P_AUTOTACTIC_ENABLE);
	}

	private void setAutoTacticPreference(boolean value) {
		getPreferenceStore().setValue(P_AUTOTACTIC_ENABLE, value);
	}

}
