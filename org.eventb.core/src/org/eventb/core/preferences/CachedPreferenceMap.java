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
package org.eventb.core.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.preferences.PreferenceMapper;

/**
 * A parameterized cache encapsulating a map of elements of type<code>T</code>
 * to be injected from a string (e.g. typically an eclipse preference) and
 * extracted back to a string. This class is intended to be sub-classed by
 * clients.
 * 
 * @since 2.1
 */
public class CachedPreferenceMap<T> {

	protected Map<String, T> cache;

	protected final IPrefElementTranslator<Map<String, T>> prefMap;

	private final Set<ICacheListener<T>> listeners;

	public CachedPreferenceMap(IPrefElementTranslator<T> translator) {
		this.prefMap = new PreferenceMapper<T>(translator);
		this.cache = new HashMap<String, T>();
		this.listeners = new HashSet<ICacheListener<T>>();
	}

	/**
	 * Loads the cache with elements created from the given string parameter.
	 * 
	 * @param pref
	 *            the information to load the cache with
	 */
	public void inject(String pref) {
		cache = prefMap.inject(pref);
		notifyListeners();
	}

	/**
	 * Returns the string value of the given element. It uses the translator
	 * given at instantiation to typically give a significant string
	 * representation for the storage of the cache in a preference.
	 * 
	 * @return the string value of the given element
	 */
	public String extract() {
		return prefMap.extract(cache);
	}

	/**
	 * Adds a new item given its name and its value to the cache if an element
	 * with this name does not already exists .
	 * 
	 * @param name
	 *            the name of the entry to be added in the cache
	 * @param value
	 *            the value of the entry to be added in the cache
	 */
	public void add(String name, T value) {
		doAddCacheEntry(name, value);
		notifyListeners();
	}

	/**
	 * Adds a list of given {@link IPrefMapEntry} to the cache.
	 * 
	 * @param entries
	 *            the entries to add
	 * @return the entries which were actually added to the cache
	 */
	public List<IPrefMapEntry<T>> addAll(
			List<IPrefMapEntry<T>> entries) {
		final List<IPrefMapEntry<T>> added = new ArrayList<IPrefMapEntry<T>>();
		for (IPrefMapEntry<T> entry : entries) {
			if (doAddCacheEntry(entry.getKey(), entry.getValue())) {
				added.add(entry);
			}
		}
		notifyListeners();
		return added;
	}

	private boolean doAddCacheEntry(String key, T entry) {
		if (exists(key))
			return false;
		cache.put(key, entry);
		return true;
	}

	/**
	 * Tells is an element with the given name is contained by the cache.
	 * 
	 * @param name
	 *            the name of the element to search for
	 * @return <code>true</code> if the element is in the cache,
	 *         <code>false</code> otherwise
	 */
	public boolean exists(String name) {
		return cache.containsKey(name);
	}

	/**
	 * Returns all the entries of the cache.
	 * 
	 * @return all the entries of the cache
	 */
	public List<IPrefMapEntry<T>> getEntries() {
		final List<IPrefMapEntry<T>> entries = new ArrayList<IPrefMapEntry<T>>();
		for (String key : cache.keySet()) {
			entries.add(new MapEntry(key));
		}
		return entries;
	}

	/**
	 * Returns the entry of the cache which has the given name,
	 * <code>null</code> if no entry with this name is in the cache.
	 * 
	 * @param name
	 *            the name of the entry to return
	 * @return the registered entry with the given <code>name</code>,
	 *         <code>null</code> if no entry with this name is in the cache.
	 */
	public IPrefMapEntry<T> getEntry(String name) {
		if (!cache.containsKey(name)) {
			return null;
		}
		return new MapEntry(name);
	}

	/**
	 * Returns all the entries' names in the cache.
	 * 
	 * @return the entries' names in the cache
	 */
	public Set<String> getEntryNames() {
		return new HashSet<String>(cache.keySet());
	}

	/**
	 * Remove the entries with the given names from the cache
	 * 
	 * @param names
	 *            the names of the entries to remove
	 */
	public void remove(String... names) {
		for (String key : names) {
			cache.remove(key);
		}
		notifyListeners();
	}

	private class MapEntry implements IPrefMapEntry<T> {

		private final String name;

		public MapEntry(String name) {
			this.name = name;
		}

		@Override
		public String getKey() {
			return name;
		}

		@Override
		public T getValue() {
			return cache.get(name);
		}

		@Override
		public void setValue(T value) {
			cache.put(name, value);
		}

	}

	/**
	 * Adds the given {@link ICacheListener} to the list of cache listeners.
	 * 
	 * @param listener
	 *            the listener to be added
	 */
	public void addListener(ICacheListener<T> listener) {
		listeners.add(listener);
	}

	protected void notifyListeners() {
		for (ICacheListener<T> listener : listeners) {
			listener.cacheChanged(this);
		}
	}

}