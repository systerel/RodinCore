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
package org.eventb.internal.core.preferences;

import static org.eventb.internal.core.Util.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eventb.core.preferences.IPrefElementTranslator;
import org.eventb.internal.core.preferences.PreferenceUtils.IXMLPref;
import org.eventb.internal.core.preferences.PreferenceUtils.PreferenceException;

/**
 * Maps a preference to a {@link java.util.Map}.
 */
public class PreferenceMapper<T> implements IPrefElementTranslator<Map<String, T>> {

	// FIXME keep as is only for compatibility,
	// but use a single xml string for all map entries (enables references !)
	
	// String separator for elements of a map
	protected static final String SEPARATOR_MAP = ";";
	// String separator between key and values of map entries
	protected static final String SEPARATOR_MAP_ELEMENT = ":";

	private final IPrefElementTranslator<T> translator;

	public PreferenceMapper(IPrefElementTranslator<T> translator) {
		this.translator = translator;
	}

	/**
	 * Returns a map of objects corresponding to the preference.
	 * 
	 * @param pref
	 *            the string of the preference representing the map
	 * @return a object map corresponding to the preference
	 */
	@Override
	public Map<String, T> inject(String pref) {
		if (pref == null) {
			return null;
		}
		final Map<String, T> map = new HashMap<String, T>();
		
		if (translator instanceof IXMLPref) {
			// TODO try to fetch everything as a single xml string, else recover
			// => stop recovering in APTM
		}
		
		final String[] stringMap = PreferenceUtils.parseString(pref,
				SEPARATOR_MAP);
		for (String elt : stringMap) {
			final String[] entry = PreferenceUtils.parseString(elt,
					SEPARATOR_MAP_ELEMENT);
			if (entry.length != 2) {
				log(null, "Invalid entry for the preference element: " + elt);
			} else {
				final String key = entry[0];
				final T value = translator.inject(entry[1]);
				if (value == null) {
					throw PreferenceException.getInstance();
				}
				map.put(key, value);
			}
		}
		return map;
	}

	/**
	 * Extracts the map model to string for serialization.
	 * 
	 * @param map
	 *            the map to extract
	 * @return a string representing the extracted map
	 */
	@Override
	public String extract(Map<String, T> map) {
		// TODO extract everything to a single xml string
		final List<String> strEntries = new ArrayList<String>();
		for (Entry<String, T> entry : map.entrySet()) {
			strEntries.add(mapEntryToString(entry.getKey(), entry.getValue()));
		}
		return PreferenceUtils.flatten(strEntries, SEPARATOR_MAP);
	}

	/**
	 * Returns a string representing the given key and value of an element of a
	 * map.
	 * 
	 * @param key
	 *            key with which the specified value is associated
	 * @param value
	 *            value associated with the specified key
	 */
	private String mapEntryToString(String key, T value) {
		final String val = translator.extract(value);
		final StringBuffer buffer = new StringBuffer(key.length()
				+ val.length() + 1);
		buffer.append(key);
		buffer.append(SEPARATOR_MAP_ELEMENT);
		buffer.append(val);
		return buffer.toString();
	}

}
