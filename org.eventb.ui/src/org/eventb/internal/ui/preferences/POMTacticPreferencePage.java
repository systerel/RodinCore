/*******************************************************************************
 * Copyright (c) 2007-2008 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.preferences;

import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eventb.core.EventBPlugin;

/**
 * @author htson
 *         <p>
 *         An extension of {@link TacticPreferencePage} to contribute a
 *         preference page for POM Tactics.
 */
public class POMTacticPreferencePage extends TacticPreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * Constructor.
	 * <p>
	 * Calling the super constructor with values for POM-Tactics preference.
	 */
	public POMTacticPreferencePage() {
		super("Preferences for the Automatic Tactic",
				PreferenceConstants.P_AUTOTACTIC_ENABLE, "&Enable auto-tactic for proving",
				PreferenceConstants.P_AUTOTACTICS, "&Tactics are run as auto-tactics");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.preferences.TacticPreferencePage#setTacticPreference()
	 */
	@Override
	protected void setTacticPreference() {
		tacticPreference = EventBPlugin.getPOMTacticPreference();
	}

}