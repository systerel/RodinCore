/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import static java.util.Collections.EMPTY_MAP;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_AUTOTACTIC_ENABLE;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;
import org.eventb.core.EventBPlugin;

/**
 * Implements a command for easily changing the "enable auto-tactic preference"
 * from the UI (i.e., more directly than through a preference page).
 * 
 * @author Aurélien Gilles
 * @author Laurent Voisin
 */
public class ToggleAutoTacticPreference extends AbstractHandler implements
		IElementUpdater {

	public static final String COMMAND_ID = "org.eventb.ui.project.autoTactic";

	// Toggles the auto-tactic enablement preference
	@Override
	public Object execute(ExecutionEvent event) {
		final boolean oldValue = getAutoTacticPreference();
		setAutoTacticPreference(!oldValue);
		return null;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void updateElement(UIElement uiElement, Map parameters) {
		final boolean enabled = getAutoTacticPreference();
		uiElement.setChecked(enabled);
	}

	private static boolean getAutoTacticPreference() {
		return EventBPlugin.getAutoPostTacticManager()
				.getAutoTacticPreference().isEnabled();
	}

	private static void setAutoTacticPreference(boolean enabled) {
		InstanceScope.INSTANCE.getNode(EventBPlugin.PLUGIN_ID).putBoolean(P_AUTOTACTIC_ENABLE, enabled);
	}

	// Register a listener for updating the UI representation of this command
	// status
	public static void registerListener() {
		InstanceScope.INSTANCE.getNode(EventBPlugin.PLUGIN_ID)
				.addPreferenceChangeListener(new ChangeListener());
	}

	static class ChangeListener implements IPreferenceChangeListener {

		@Override
		public void preferenceChange(PreferenceChangeEvent event) {
			if (P_AUTOTACTIC_ENABLE.equals(event.getKey())) {
				getCommandService().refreshElements(COMMAND_ID, EMPTY_MAP);
			}
		}

		private static ICommandService getCommandService() {
			return (ICommandService) PlatformUI.getWorkbench().getService(
					ICommandService.class);
		}

	}

}
