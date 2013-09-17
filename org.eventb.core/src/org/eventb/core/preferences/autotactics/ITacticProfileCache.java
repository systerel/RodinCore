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
package org.eventb.core.preferences.autotactics;

import java.util.List;
import java.util.Set;

import org.eventb.core.preferences.ICacheListener;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.preferences.IPreferenceCheckResult;
import org.eventb.core.seqprover.ITacticDescriptor;

/**
 * Common protocol for tactics profile caches.
 * <p>
 * Instances can be obtained by calling
 * {@link TacticPreferenceFactory#makeTacticProfileCache(org.eclipse.core.runtime.preferences.IEclipsePreferences)}
 * </p>
 *
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITacticProfileCache {

	/**
	 * Adds a new item given its name and its value to the cache if an element
	 * with this name does not already exists .
	 * 
	 * @param name
	 *            the name of the profile to be added to the cache
	 * @param tactic
	 *            the descriptor of the tactic profile to be added to the cache
	 */
	void add(String name, ITacticDescriptor tactic);

	/**
	 * Adds a list of given {@link IPrefMapEntry} to the cache.
	 * 
	 * @param entries
	 *            the entries to add
	 * @return the entries which were actually added to the cache
	 */
	List<IPrefMapEntry<ITacticDescriptor>> addAll(
			List<IPrefMapEntry<ITacticDescriptor>> entries);

	/**
	 * Adds the given {@link ICacheListener} to the list of cache listeners.
	 * 
	 * @param listener
	 *            the listener to be added
	 */
	void addListener(ICacheListener<ITacticDescriptor> listener);

	/**
	 * Tells whether a profile with the given name is present in the cache.
	 * 
	 * @param name
	 *            the name of the profile to search for
	 * @return <code>true</code> if the profile is in the cache,
	 *         <code>false</code> otherwise
	 */
	boolean exists(String name);

	/**
	 * Returns all profiles of the cache.
	 * 
	 * @return all profiles of the cache
	 */
	List<IPrefMapEntry<ITacticDescriptor>> getEntries();

	/**
	 * Returns the profile of the cache which has the given name.
	 * 
	 * @param name
	 *            the name of the profile to return
	 * @return the registered profile with the given <code>name</code>,
	 *         <code>null</code> if no profile with this name is in the cache.
	 */
	IPrefMapEntry<ITacticDescriptor> getEntry(String name);

	/**
	 * Returns all profile names in the cache.
	 * 
	 * @return the profile names in the cache
	 */
	Set<String> getEntryNames();

	/**
	 * Returns whether the given name is a key for a default entry in this
	 * preference map.
	 * 
	 * @param name
	 *            an entry name
	 * @return <code>true</code> if default entry, <code>false</code> otherwise
	 * @since 3.0
	 */
	boolean isDefaultEntry(String name);

	/**
	 * Loads this cache from preference store.
	 */
	void load();

	/**
	 * Loads the default value from preference store.
	 */
	void loadDefault();

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
	void remove(String... names);

	/**
	 * Stores this cache to preference store.
	 */
	void store();

	/**
	 * Loads the cache with elements created from the given string parameter.
	 * <p>
	 * If inject fails, an IllegalArgumentException is thrown.
	 * </p>
	 * 
	 * @param pref
	 *            the information to load the cache with
	 * @throws IllegalArgumentException
	 *             in case of failure
	 */
	IInjectLog inject(String pref) throws IllegalArgumentException;

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
	 */
	IPreferenceCheckResult preAddCheck(String key, ITacticDescriptor value);

}