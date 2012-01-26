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
import static org.eventb.internal.core.preferences.PreferenceUtils.getDocument;
import static org.eventb.internal.core.preferences.PreferenceUtils.serializeDocument;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.PREF_UNIT;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.TACTIC_PREF;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.assertName;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.createElement;
import static org.eventb.internal.core.preferences.PreferenceUtils.XMLElementTypes.getElementsByTagName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eventb.core.preferences.CachedPreferenceMap;
import org.eventb.core.preferences.IPrefElementTranslator;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.preferences.IMapRefSolver;
import org.eventb.core.preferences.IXMLPrefSerializer;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.preferences.PreferenceUtils.PreferenceException;
import org.eventb.internal.core.preferences.PreferenceUtils.ReadPrefMapEntry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Maps a preference to a {@link java.util.Map}.
 */
public class PreferenceMapper<T> implements IPrefElementTranslator<Map<String, T>>, IMapRefSolver<T> {

	// String separator for elements of a map
	protected static final String SEPARATOR_MAP = ";";
	// String separator between key and values of map entries
	protected static final String SEPARATOR_MAP_ELEMENT = ":";

	private final IPrefElementTranslator<T> translator;

	private final IXMLPrefSerializer<T> xmlTranslator;
	
	public PreferenceMapper(IPrefElementTranslator<T> translator) {
		this(translator, null);
	}
	
	public PreferenceMapper(IXMLPrefSerializer<T> xmlTranslator) {
		this(null, xmlTranslator);
	}
	
	private PreferenceMapper(IPrefElementTranslator<T> translator,
			IXMLPrefSerializer<T> xmlTranslator) {
		this.translator = translator;
		this.xmlTranslator = xmlTranslator;
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
		
		if (xmlTranslator != null) {
			try {
				final Document doc = PreferenceUtils.makeDocument(pref);
				final Element tacticPref = doc.getDocumentElement();
				assertName(tacticPref, TACTIC_PREF);
				final NodeList units = getElementsByTagName(tacticPref,
						PREF_UNIT);
				for (int i = 0; i < units.getLength(); i++) {
					final IPrefMapEntry<T> unit = loadUnit(units, i, pref);
					if (unit != null) {
						map.put(unit.getKey(), unit.getValue());
					}
				}
			} catch (Exception e) {
				Util.log(e, "error while loading tactic preference");
				throw PreferenceException.getInstance();
			}
		} else {

			final String[] stringMap = PreferenceUtils.parseString(pref,
					SEPARATOR_MAP);
			for (String elt : stringMap) {
				final String[] entry = PreferenceUtils.parseString(elt,
						SEPARATOR_MAP_ELEMENT);
				if (entry.length != 2) {
					log(null, "Invalid entry for the preference element: "
							+ elt);
				} else {
					final String key = entry[0];
					final T value = translator.inject(entry[1]);
					if (value == null) {
						// FIXME may be due to missing plug-in (combinator or
						// parameterizer contribution)
						throw PreferenceException.getInstance();
					}
					map.put(key, value);
				}
			}
		}
		return map;
	}

	private IPrefMapEntry<T> loadUnit(final NodeList units, int i, String pref) {
		final Node unitElem = units.item(i);
		try {
			return xmlTranslator.get(unitElem);
		} catch (PreferenceException e) {
			// failed
			final String message = "error while loading tactic profile";
			Util.log(e, message);
			return null;
		}
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
		if (xmlTranslator != null) {
			try {
				final Document doc = getDocument();
				final Element tacticPref = createElement(doc, TACTIC_PREF);

				for (Entry<String, T> entry : map.entrySet()) {
					final ReadPrefMapEntry<T> prefEntry = new ReadPrefMapEntry<T>(
							entry.getKey(), entry.getValue());
					xmlTranslator.put(prefEntry, doc, tacticPref);
				}
				doc.appendChild(tacticPref);
				return serializeDocument(doc);
			} catch (Exception e) {
				Util.log(e, "while storing tactic preference");
				throw PreferenceException.getInstance();
			}
		}
		// old storage
		final List<String> strEntries = new ArrayList<String>();
		for (Entry<String, T> entry : map.entrySet()) {
			final T element = entry.getValue();
			strEntries.add(mapEntryToString(entry.getKey(), element));
		}
		return PreferenceUtils.flatten(strEntries, SEPARATOR_MAP);
	}

	@Override
	public void resolveReferences(CachedPreferenceMap<T> map) {
		if (xmlTranslator == null) {
			return;
		}
		for (IPrefMapEntry<T> entry : map.getEntries()) {
			xmlTranslator.resolveReferences(entry, map);
		}
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
