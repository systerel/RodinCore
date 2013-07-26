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
package org.eventb.core.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.preferences.autotactics.IInjectLog;
import org.eventb.internal.core.preferences.InjectLog;
import org.eventb.internal.core.preferences.PrefEntryGraph;
import org.eventb.internal.core.preferences.PreferenceCheckResult;
import org.eventb.internal.core.preferences.PreferenceMapper;
import org.eventb.internal.core.preferences.PreferenceUtils.PreferenceException;
import org.eventb.internal.core.preferences.PreferenceUtils.ReadPrefMapEntry;
import org.eventb.internal.core.tool.graph.Node;

/**
 * A parameterized cache encapsulating a map of elements of type<code>T</code>
 * to be injected from a string (e.g. typically an eclipse preference) and
 * extracted back to a string. This class is intended to be sub-classed by
 * clients.
 * 
 * @since 2.1
 */
public class CachedPreferenceMap<T> {

	protected Map<String, T> cache = new HashMap<String, T>();

	// to make references work, it is important to return the same map entry for
	// as long as the corresponding preference exists; that is, even if its name
	// and/or value change
	private final Map<String, MapEntry> accessedEntries = new HashMap<String, MapEntry>();

	private final PreferenceMapper<T> prefMap;

	/**
	 * @since 2.3
	 */
	protected final IReferenceMaker<T> refMaker;

	private final Set<ICacheListener<T>> listeners = new HashSet<ICacheListener<T>>();

	/**
	 * Default entry keys are read only.
	 */
	private final Set<String> defaultKeys = new HashSet<String>();

	/**
	 * Creates a cache for the old serialization format.  Do not use anymore,
	 * except in code for backward compatibility.
	 * 
	 * @param translator
	 *            a preference translator
	 * @deprecated use
	 *             {@link #CachedPreferenceMap(IXMLPrefSerializer, IReferenceMaker)}
	 *             instead
	 */
	@Deprecated
	public CachedPreferenceMap(IPrefElementTranslator<T> translator) {
		this(new PreferenceMapper<T>(translator), null);
	}

	/**
	 * @since 2.3
	 */
	public CachedPreferenceMap(IXMLPrefSerializer<T> translator,
			IReferenceMaker<T> refMaker) {
		this(new PreferenceMapper<T>(translator), refMaker);
	}

	private CachedPreferenceMap(PreferenceMapper<T> prefMap,
			IReferenceMaker<T> refMaker) {
		this.prefMap = prefMap;
		this.refMaker = refMaker;
	}

	/**
	 * Loads the cache with elements created from the given string parameter.
	 * <p>
	 * If inject fails, an IllegalArgumentException is thrown.
	 * </p>
	 * 
	 * @param pref
	 *            the information to load the cache with
	 * @return a log with errors and warnings about encountered problems
	 * @throws IllegalArgumentException
	 *             in case of failure
	 * @since 3.0
	 */
	public IInjectLog inject(String pref) throws IllegalArgumentException {
		final IInjectLog log = new InjectLog();
		// to do before resolving references
		accessedEntries.clear();
		try {
			cache = prefMap.inject(pref, log);
		} catch (PreferenceException e) {
			// failed
			throw new IllegalArgumentException(e);
		}
		prefMap.resolveReferences(this, log);
		notifyListeners();
		return log;
	}

	private void resolveReferencesWithCheck() {
		final IInjectLog log = new InjectLog();
		prefMap.resolveReferences(this, log);
		if (log.hasErrors()) {
			// pre add checks failed to prevent errors
			throw new IllegalStateException("Errors resolving references: "
					+ log.getErrors());
		}
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
		if (doAddCacheEntry(name, value)) {
			resolveReferencesWithCheck();
			notifyListeners();
		}
	}

	/**
	 * Adds a list of given {@link IPrefMapEntry} to the cache.
	 * 
	 * @param entries
	 *            the entries to add
	 * @return the entries which were actually added to the cache
	 */
	public List<IPrefMapEntry<T>> addAll(List<IPrefMapEntry<T>> entries) {
		final List<IPrefMapEntry<T>> added = new ArrayList<IPrefMapEntry<T>>();
		final List<IPrefMapEntry<T>> sorted = checkAndSort(entries);
		for (IPrefMapEntry<T> entry : sorted) {
			if (doAddCacheEntry(entry.getKey(), entry.getValue())) {
				added.add(entry);
			}
		}
		resolveReferencesWithCheck();
		notifyListeners();
		return added;
	}

	private List<IPrefMapEntry<T>> checkAndSort(List<IPrefMapEntry<T>> entries) {
		final PrefEntryGraph<T> graph = new PrefEntryGraph<T>("preference map",
				refMaker);
		graph.addAll(entries);
		final IPreferenceCheckResult checkResult = check(graph);
		assertLegalResult("inconsistent preference entries: ", checkResult);
		final List<Node<IPrefMapEntry<T>>> sorted = graph.getSorted();
		final List<IPrefMapEntry<T>> result = new ArrayList<IPrefMapEntry<T>>(
				sorted.size());
		for (Node<IPrefMapEntry<T>> node : sorted) {
			result.add(node.getObject());
		}
		return result;
	}

	private boolean doAddCacheEntry(String key, T value) {
		if (exists(key))
			return false;
		doPreAddCheck(key, value);
		cache.put(key, value);
		accessedEntries.remove(key);
		return true;
	}

	void doPreAddCheck(String key, T value) {
		final IPreferenceCheckResult checkResult = preAddCheck(key, value);
		assertLegalResult("cannot add " + key + " to preferences because of ",
				checkResult);
	}

	private static void assertLegalResult(String preErrorMessage,
			final IPreferenceCheckResult checkResult) {
		if (checkResult.hasError()) {
			String reason = "unknown reason";
			final Set<String> unres = checkResult.getUnresolvedReferences();
			if (unres != null) {
				reason = "unresolved reference(s) to " + unres;
			} else {
				final List<String> cycle = checkResult.getCycle();
				if (cycle != null) {
					reason = "cyclic references " + cycle;
				}
			}
			throw new IllegalArgumentException(preErrorMessage + reason);
		}
	}

	/**
	 * Checks whether adding given key with given value into given map
	 * introduces cyclic references.
	 * <p>
	 * If the given key already exists, it is considered as a replacement, so
	 * the corresponding entry is not taken into account, only the new one.
	 * </p>
	 * 
	 * @param key
	 *            a new map key
	 * @param value
	 *            a value about to be added
	 * @return a {@link IPreferenceCheckResult}
	 * @since 2.3
	 */
	public IPreferenceCheckResult preAddCheck(String key, T value) {
		if (refMaker == null) {
			return PreferenceCheckResult.getNoError();
		}

		final PrefEntryGraph<T> graph = new PrefEntryGraph<T>("preference map",
				refMaker);
		final List<IPrefMapEntry<T>> entries = getEntries();
		// if key already exists, it is a replacement, remove it
		final Iterator<IPrefMapEntry<T>> iterator = entries.iterator();
		while (iterator.hasNext()) {
			final IPrefMapEntry<T> next = iterator.next();
			if (next.getKey().equals(key)) {
				iterator.remove();
			}
		}

		graph.addAll(entries);

		// corresponds to references to deleted entries out of added entry
		// avoid external reference problems
		// not adding 'key' to avoid duplicating the node below
		graph.addUnresolvedExcept(key);

		final ReadPrefMapEntry<T> newEntry = new ReadPrefMapEntry<T>(key, value);
		graph.add(newEntry);
		return check(graph);
	}

	private IPreferenceCheckResult check(PrefEntryGraph<T> graph) {
		try {
			graph.analyse();
			return PreferenceCheckResult.getNoError();
		} catch (IllegalStateException e) {
			final PreferenceCheckResult result = new PreferenceCheckResult();

			// if there are unresolved references in added entry
			final Set<String> unresRefs = graph.addUnresolvedExcept(null);
			if (!unresRefs.isEmpty()) {
				result.setUnresolvedReferences(unresRefs);
				return result;
			}

			final List<Node<IPrefMapEntry<T>>> nodeCycle = graph.getCycle();
			final List<String> cycle = new ArrayList<String>(nodeCycle.size());
			for (Node<IPrefMapEntry<T>> node : nodeCycle) {
				cycle.add(node.getId());
			}
			result.setCycle(cycle);
			return result;
		}
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

		final ArrayList<String> sortedKeys = new ArrayList<String>(
				cache.keySet());
		Collections.sort(sortedKeys);
		for (String key : sortedKeys) {
			entries.add(getEntry(key));
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
		MapEntry entry = accessedEntries.get(name);
		if (entry == null) {
			entry = new MapEntry(name);
			accessedEntries.put(name, entry);
		}
		return entry;
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
	 * Remove the entries with the given names from the cache.
	 * <p>
	 * Important: don't use <code>remove()</code> then <code>add()</code> to
	 * rename an entry. Instead, use <code>getEntry().setKey()</code>.
	 * </p>
	 * 
	 * @param names
	 *            the names of the entries to remove
	 */
	public void remove(String... names) {
		for (String key : names) {
			cache.remove(key);
			accessedEntries.remove(key);
		}
		notifyListeners();
	}

	/**
	 * Clears all entries from the cache.
	 * 
	 * @since 2.3
	 */
	public void clear() {
		cache.clear();
		accessedEntries.clear();
		notifyListeners();
	}

	private class MapEntry implements IPrefMapEntry<T> {

		private String name;

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
		public void setKey(String key) {
			if (isDefaultEntry(name)) {
				return;
			}
			final T value = cache.remove(name);
			name = key;
			if (value == null) {
				return;
			}
			setValue(value);
		}

		@Override
		public void setValue(T value) {
			// allow for default entries to enable reference resolving
			doPreAddCheck(name, value);
			cache.put(name, value);
			notifyListeners();
		}

		@Override
		public T getReference() {
			return refMaker.makeReference(this);
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

	/**
	 * Notifies listeners that this cache has changed.
	 */
	protected void notifyListeners() {
		for (ICacheListener<T> listener : listeners) {
			listener.cacheChanged(this);
		}
	}

	/**
	 * Returns whether the given name is a key for a default entry in this
	 * preference map.
	 * 
	 * @param name
	 *            an entry name
	 * @return <code>true</code> if default entry, <code>false</code> otherwise
	 * @since 3.0
	 */
	public boolean isDefaultEntry(String name) {
		return defaultKeys.contains(name);
	}

	/**
	 * Adds given default keys to this preference map, which sets corresponding
	 * entries as default entries.
	 * <p>
	 * Corresponding entries need not already exist in this preference map. They
	 * can be added further on.
	 * </p>
	 * <p>
	 * Default entries cannot be modified (name or value).
	 * </p>
	 * 
	 * @param keys
	 *            a set of entry keys
	 * @since 3.0
	 */
	protected final void addDefaultKeys(Set<String> keys) {
		defaultKeys.addAll(keys);
	}
}