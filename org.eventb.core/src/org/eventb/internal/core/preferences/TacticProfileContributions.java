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
package org.eventb.internal.core.preferences;

import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.DEFAULT_AUTO_TACTIC;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.DEFAULT_POST_TACTIC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.EventBPlugin;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.preferences.PreferenceUtils.ReadPrefMapEntry;

/**
 * Loads and manages tactic profiles contributed through extension point
 * org.eventb.core.tacticProfiles.
 * 
 * @author N. Beauger
 */
public class TacticProfileContributions {

	private static final String EXTENSION_POINT_ID = EventBPlugin.PLUGIN_ID
			+ ".tacticProfiles";

	private static final TacticProfileContributions INSTANCE = new TacticProfileContributions();

	private final Map<String, IPrefMapEntry<ITacticDescriptor>> profiles;

	private TacticProfileContributions() {
		// singleton
		this.profiles = loadProfileContributions();
	}

	public static TacticProfileContributions getInstance() {
		return INSTANCE;
	}

	private static String getName(IConfigurationElement profile) {
		final String name = profile.getAttribute("name");
		if (name == null || name.isEmpty()) {
			Util.log(null,
					"missing name in " + profile.getNamespaceIdentifier());
			return null;
		}
		return name;
	}

	private static ITacticDescriptor getTacticDescriptor(
			IConfigurationElement profile) {
		try {
			final Object object = profile.createExecutableExtension("class");
			if (!(object instanceof ITacticDescriptor)) {
				Util.log(
						null,
						"In extension "
								+ profile.getName()
								+ " in plug-in "
								+ profile.getNamespaceIdentifier()
								+ ":\n  given object should implement ITacticProfileContribution and create a ITacticDescriptor,"
								+ "\n  but created object: "
								+ object.getClass());
				return null;
			}
			return (ITacticDescriptor) object;
		} catch (CoreException e) {
			Util.log(
					e,
					"while loading profile in "
							+ profile.getNamespaceIdentifier());
			return null;
		}
	}

	private static Map<String, IPrefMapEntry<ITacticDescriptor>> loadProfileContributions() {
		final Map<String, IPrefMapEntry<ITacticDescriptor>> loadedProfiles = new HashMap<String, IPrefMapEntry<ITacticDescriptor>>();
		final IExtensionRegistry xRegistry = Platform.getExtensionRegistry();
		final IExtensionPoint xPoint = xRegistry
				.getExtensionPoint(EXTENSION_POINT_ID);
		for (IConfigurationElement profile : xPoint.getConfigurationElements()) {
			try {
				final String name = getName(profile);
				if (name == null) {
					continue;
				}
				if (name.equals(DEFAULT_AUTO_TACTIC)
						|| name.equals(DEFAULT_POST_TACTIC)) {
					Util.log(
							null,
							"Illegal name for a contributed profile: " + name
									+ ", profile "
									+ profile.getNamespaceIdentifier()
									+ " has been ignored");
					continue;
				}
				if (loadedProfiles.containsKey(name)) {
					Util.log(null, "Duplicate profile '" + name + "' from "
							+ profile.getNamespaceIdentifier()
							+ " has been ignored");
					continue;
				}
				
				final ITacticDescriptor descriptor = getTacticDescriptor(profile);
				if (descriptor == null) {
					continue;
				}

				loadedProfiles.put(name, new ReadPrefMapEntry<ITacticDescriptor>(name,
						descriptor));
			} catch (Throwable t) {
				// InvalidRegistryObjectException or any other uncaught throwable
				Util.log(t, "while loading extension of point "
						+ EXTENSION_POINT_ID);
				continue;
			}
		}
		return loadedProfiles;
	}

	/**
	 * Returns contributed profiles.
	 * 
	 * @return a mapping of profile names to tactic descriptors
	 */
	public List<IPrefMapEntry<ITacticDescriptor>> getProfiles() {
		return new ArrayList<IPrefMapEntry<ITacticDescriptor>>(profiles.values());
	}
	
	/**
	 * Returns the names of contributed profiles.
	 * 
	 * @return a set of profile names
	 */
	public Set<String> getProfileNames() {
		return new HashSet<String>(profiles.keySet());
	}
}
