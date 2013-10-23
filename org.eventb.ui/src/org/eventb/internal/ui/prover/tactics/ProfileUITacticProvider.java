/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_POSTTACTIC_CHOICE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceFactory.makeTacticProfileCache;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOSequent;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.preferences.autotactics.ITacticProfileCache;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.ui.prover.IUIDynTactic;
import org.eventb.ui.prover.IUIDynTacticProvider;
import org.rodinp.core.IRodinElement;

/**
 * UI tactic provider of available profiles, as a dynamic dropdown in proof
 * control.
 * 
 * @author beauger
 */
public class ProfileUITacticProvider implements IUIDynTacticProvider {

	// Name of the preference node that holds the tactic preferences
	private static final String PREF_NODE_NAME = EventBPlugin.PLUGIN_ID;

	// Name of the witness preference
	private static final String PREF_NAME = P_POSTTACTIC_CHOICE;

	@Override
	public Collection<IUIDynTactic> getDynTactics(IProofTreeNode ptNode,
			IPOSequent poSequent) {
		final Collection<IUIDynTactic> profiles = new ArrayList<IUIDynTactic>();

		final IEclipsePreferences preference = getPreference(poSequent);

		final ITacticProfileCache cache = makeTacticProfileCache(preference);
		cache.load();

		for (IPrefMapEntry<ITacticDescriptor> entry : cache.getEntries()) {
			final String name = entry.getKey();
			final ITacticDescriptor desc = entry.getValue();

			profiles.add(new IUIDynTactic() {

				@Override
				public String getName() {
					return name;
				}

				@Override
				public ITacticDescriptor getTacticDescriptor() {
					return desc;
				}
			});
		}
		return profiles;
	}

	private static IEclipsePreferences getPreference(IRodinElement element) {
		final IProject project = element.getRodinProject().getProject();

		final IEclipsePreferences projectPrefNode = new ProjectScope(project)
				.getNode(PREF_NODE_NAME);

		if (hasProjectSpecificSettings(projectPrefNode)) {
			return projectPrefNode;
		} else {
			return InstanceScope.INSTANCE.getNode(PREF_NODE_NAME);
		}
	}

	private static boolean hasProjectSpecificSettings(
			IEclipsePreferences projectPrefNode) {
		return projectPrefNode.get(PREF_NAME, null) != null;
	}

}
