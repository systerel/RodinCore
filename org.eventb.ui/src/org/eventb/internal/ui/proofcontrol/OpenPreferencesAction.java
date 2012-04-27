/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofcontrol;

import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_POSTTACTIC_CHOICE;
import static org.eventb.internal.ui.preferences.PreferenceConstants.AUTO_POST_TACTIC_PREFERENCE_PAGE_ID;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eventb.core.pm.IUserSupport;
import org.eventb.ui.EventBUIPlugin;

/**
 * Action for opening the preferences dialog about proof tactics. The dialog
 * actually opened depends on whether project specific settings have been
 * enabled or not. We also update the label of this action so that the user
 * can see in advance which preferences are in effect (workspace or project
 * specific).
 * <p>
 * To detect whether project specific settings are in place, we listen to
 * the changes to one of the project specific preferences (
 * <code>PREF_NAME</code>) and assume that all tactic preferences behave the
 * same.
 * </p>
 */
public class OpenPreferencesAction extends Action implements
		IPreferenceChangeListener {

	// Name of the preference node that holds the tactic preferences
	private static final String PREF_NODE_NAME = EventBUIPlugin.PLUGIN_ID;

	// Name of the witness preference
	private static final String PREF_NAME = P_POSTTACTIC_CHOICE;

	private final IUserSupport userSupport;
	
	private final IProject project;

	// The preference node holding the project specific preferences
	private final IEclipsePreferences projectPrefNode;

	public OpenPreferencesAction(IUserSupport userSupport) {
		super("Preferences...", IAction.AS_PUSH_BUTTON);
		this.userSupport = userSupport;
		this.project = userSupport.getInput().getRodinProject().getProject();
		projectPrefNode = new ProjectScope(project).getNode(PREF_NODE_NAME);
		projectPrefNode.addPreferenceChangeListener(this);
		updateText();
	}

	public void dispose() {
		projectPrefNode.removePreferenceChangeListener(this);
	}

	public void updateText() {
		updateText(hasProjectSpecificSettings());
	}

	private void updateText(final boolean projectSpecific) {
		final String scope;
		if (projectSpecific) {
			scope = project.getName();
		} else {
			scope = "Workspace";
		}
		setText("Preferences (" + scope + ")...");
	}

	private boolean hasProjectSpecificSettings() {
		return projectPrefNode.get(PREF_NAME, null) != null;
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent event) {
		final String prefName = event.getKey();
		if (PREF_NAME.equals(prefName)) {
			updateText(event.getNewValue() != null);
		}
	}

	@Override
	public void run() {
		final String pageId = AUTO_POST_TACTIC_PREFERENCE_PAGE_ID;
		final String[] displayedIds = new String[] { pageId };
		final Object data;
		if (hasProjectSpecificSettings())
			data = userSupport.getInput();
		else
			data = null;
		final Dialog dialog = createPreferenceDialogOn(null, pageId,
				displayedIds, data);
		dialog.open();
	}

}