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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.core.preferences.CachedPreferenceMap;
import org.eventb.core.preferences.IPrefElementTranslator;


/**
 * This class stores the contents of a map as a string.
 */
public class StorablePreferenceMap<T> extends CachedPreferenceMap<T> implements IEventBPreference {

	private final String preference;

	// The current preference store used
	private final IPreferenceStore store;

	public StorablePreferenceMap(IPreferenceStore store, String preference,
			IPrefElementTranslator<T> translator) {
		super(translator);
		this.store = store;
		this.preference = preference;
	}
	
	@Override
	public void store() {
		store.setValue(preference, extract());
	}

	public void storeDefault() {
		store.setDefault(preference, extract());
	}

	public void load() {
		inject(store.getString(preference));
	}

	public void loadDefault() {
		inject(store.getDefaultString(preference));
	}

}
