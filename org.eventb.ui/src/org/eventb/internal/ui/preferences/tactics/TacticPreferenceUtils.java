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
package org.eventb.internal.ui.preferences.tactics;

import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_AUTOTACTIC_CHOICE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_AUTOTACTIC_ENABLE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_POSTTACTIC_CHOICE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_POSTTACTIC_ENABLE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_TACTICSPROFILES;
import static org.eventb.internal.ui.utils.Messages.preferencepage_autotactic_defaultprofilename;
import static org.eventb.internal.ui.utils.Messages.preferencepage_posttactic_defaultprofilename;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eventb.core.EventBPlugin;
import org.eventb.core.preferences.autotactics.IAutoPostTacticManager;
import org.eventb.core.preferences.autotactics.TacticPreferenceConstants;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.ui.EventBUIPlugin;
import org.osgi.service.prefs.BackingStoreException;
import org.rodinp.core.IRodinElement;

public class TacticPreferenceUtils {

	public static String getDefaultAutoTactics() {
		return preferencepage_autotactic_defaultprofilename;
	}
	
	public static String getDefaultPostTactics() {
		return preferencepage_posttactic_defaultprofilename;
	}

	public static Collection<ITacticDescriptor> getAvailableTactics() {
		final IAutoPostTacticManager manager = EventBPlugin
				.getAutoPostTacticManager();
		final Collection<ITacticDescriptor> postDescriptors = manager
				.getPostTacticPreference().getDeclaredDescriptors();
		final Collection<ITacticDescriptor> autoDescriptors = manager
				.getAutoTacticPreference().getDeclaredDescriptors();
		final Collection<ITacticDescriptor> result = new ArrayList<ITacticDescriptor>(
				autoDescriptors.size());
		result.addAll(postDescriptors);
		for (ITacticDescriptor descriptor : autoDescriptors) {
			if (!result.contains(descriptor)) {
				result.add(descriptor);
			}
		}
		return result;
	}

	public static List<String> getLabels(List<ITacticDescriptor> tactics) {
		final List<String> result = new ArrayList<String>();
		for (ITacticDescriptor tactic : tactics) {
			result.add(tactic.getTacticID());
		}
		return result;
	}

	/**
	 * Set the default value for the tactics preference.
	 * 
	 * @see TacticPreferenceConstants#P_TACTICSPROFILES
	 * @see TacticPreferenceConstants#P_POSTTACTIC_ENABLE
	 * @see TacticPreferenceConstants#P_POSTTACTIC_CHOICE
	 * @see TacticPreferenceConstants#P_AUTOTACTIC_ENABLE
	 * @see TacticPreferenceConstants#P_AUTOTACTIC_CHOICE
	 * */
	public static void initializeDefault(IPreferenceStore store) {
		final IAutoPostTacticManager manager = EventBPlugin
				.getAutoPostTacticManager();
		// Default value for profile list
		final ITacticDescriptor defaultAutoTactic = manager
				.getAutoTacticPreference().getDefaultDescriptor();
		final ITacticDescriptor defaultPostTactic = manager
				.getPostTacticPreference().getDefaultDescriptor();
		final TacticsProfilesCache tacticsCache = new TacticsProfilesCache(
				store);
		tacticsCache.add(getDefaultAutoTactics(), defaultAutoTactic);
		tacticsCache.add(getDefaultPostTactics(), defaultPostTactic);
		tacticsCache.storeDefault();

		// Default value for post-tactic registry
		store.setDefault(P_POSTTACTIC_ENABLE, true);
		store.setDefault(P_POSTTACTIC_CHOICE, getDefaultPostTactics());

		// Default value for auto-tactic registry
		store.setDefault(P_AUTOTACTIC_ENABLE, true);
		store.setDefault(P_AUTOTACTIC_CHOICE, getDefaultAutoTactics());
	}
	

	/**
	 * Resizes the given control and packs the shell. The new width is the
	 * preferred width. The height grows if needed (preferred height), but does
	 * not decrease unless it exceeds client area.
	 * 
	 * @param control
	 */
	public static void packAll(Control control) {
		final Point size = control.getSize();
		final Rectangle clientArea = control.getParent().getClientArea();
		final Point preferredSize = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		preferredSize.y = Math.max(preferredSize.y, size.y);
		preferredSize.y = Math.min(preferredSize.y, clientArea.height);
		if (!preferredSize.equals(size)) {
			control.setSize(preferredSize);
			control.redraw();
		}
		
		control.getShell().pack();
	}

	
	/**
	 * Returns whether the given element has project specific settings
	 * concerning tactic profiles.
	 * 
	 * @param element
	 *            a rodin element
	 * @return <code>true</code> iff given element has project specific tactic
	 *         preferences
	 */
	public static boolean hasProjectSpecificTactics(IRodinElement element) {
		final IProject project = element.getRodinProject().getProject();
		final IScopeContext sc = new ProjectScope(project);
		final IEclipsePreferences node = sc.getNode(EventBUIPlugin.PLUGIN_ID);
		try {
			final String[] keys = node.keys();
			if (keys.length == 0)
				return false;
			return Arrays.asList(keys).contains(P_TACTICSPROFILES);
		} catch (BackingStoreException e) {
			// never happens on ProjectPreferences
			return false;
		}
	}
}
