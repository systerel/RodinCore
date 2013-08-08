/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.preferences;

import static org.eventb.core.EventBPlugin.PLUGIN_ID;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eventb.core.preferences.CachedPreferenceMap;
import org.eventb.core.preferences.IReferenceMaker;
import org.eventb.core.preferences.IXMLPrefSerializer;

/**
 * This class stores the contents of a map as a string.
 */
public abstract class StorablePreferenceMap<T> extends CachedPreferenceMap<T> {

	private final IEclipsePreferences preferenceNode;
	private final String preference;

	public StorablePreferenceMap(IEclipsePreferences preferenceNode, String preference,
			IXMLPrefSerializer<T> translator, IReferenceMaker<T> refMaker) {
		super(translator, refMaker);
		this.preferenceNode = preferenceNode;
		this.preference = preference;
	}

	private IEclipsePreferences getDefaultPreferences() {
		return DefaultScope.INSTANCE.getNode(PLUGIN_ID);
	}

	private String getDefaultValue() {
		return getDefaultPreferences().get(preference, null);
	}

	private String getValue() {
		PreferenceUtils.restoreFromUIIfNeeded(preferenceNode, false);
		String defaultPref = getDefaultValue();
		return preferenceNode.get(preference, defaultPref);
	}

	public void store() {
		preferenceNode.put(preference, extract());
	}

	public void storeDefault() {
		final IEclipsePreferences defaultNode = getDefaultPreferences();
		defaultNode.put(preference, extract());
	}

	public void load() {
		inject(getValue());
	}

	public void loadDefault() {
		inject(getDefaultValue());
	}

}
