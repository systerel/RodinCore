/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences.tactics;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.core.preferences.CachedPreferenceMap;
import org.eventb.core.preferences.IReferenceMaker;
import org.eventb.core.preferences.IXMLPrefSerializer;


/**
 * This class stores the contents of a map as a string.
 */
public abstract class StorablePreferenceMap<T> extends CachedPreferenceMap<T> implements IEventBPreference {

	private final String preference;

	// The current preference store used
	private final IPreferenceStore store;

	public StorablePreferenceMap(IPreferenceStore store, String preference,
			IXMLPrefSerializer<T> translator, IReferenceMaker<T> refMaker) {
		super(translator, refMaker);
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
		final boolean recovered = load(store.getString(preference));
		if (recovered) {
			store();
		}
	}

	public void loadDefault() {
		final boolean recovered = load(store.getDefaultString(preference));
		if (recovered) {
			storeDefault();
		}
	}

	protected abstract CachedPreferenceMap<T> recover(String pref);
	
	// true if recovered
	private boolean load(String pref) {
		try {
			inject(pref);
			return false;
		} catch (IllegalArgumentException e) {
			// backward compatibility: try to recover
			final CachedPreferenceMap<T> map = recover(pref);
			if(map == null) {
				// problem logged by inject()
				throw e;
			}
			clear();
			addAll(map.getEntries());
			return true;
		}

	}
	
}
