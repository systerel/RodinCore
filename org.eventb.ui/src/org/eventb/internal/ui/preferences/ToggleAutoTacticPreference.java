/*******************************************************************************
 * Copyright (c) 2009, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 ******************************************************************************/
package org.eventb.internal.ui.preferences;

import static java.util.Collections.EMPTY_MAP;
import static org.eventb.internal.ui.preferences.EventBPreferenceStore.getPreferenceStore;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_AUTOTACTIC_ENABLE;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

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
		return getPreferenceStore().getBoolean(P_AUTOTACTIC_ENABLE);
	}

	private static void setAutoTacticPreference(boolean enabled) {
		getPreferenceStore().setValue(P_AUTOTACTIC_ENABLE, enabled);
	}

	// Register a listener for updating the UI representation of this command
	// status
	public static void registerListener() {
		getPreferenceStore().addPropertyChangeListener(new ChangeListener());
	}

	static class ChangeListener implements IPropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (P_AUTOTACTIC_ENABLE.equals(event.getProperty())) {
				getCommandService().refreshElements(COMMAND_ID, EMPTY_MAP);
			}
		}

		private ICommandService getCommandService() {
			return (ICommandService) PlatformUI.getWorkbench().getService(
					ICommandService.class);
		}

	}

}
