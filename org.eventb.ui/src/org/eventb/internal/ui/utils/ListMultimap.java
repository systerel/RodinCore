/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Partial implementation of a multimap, freely inspired from Guava's API. This
 * is an abstract datatype homogeneous to Map(K, List<V>) and implemented with a
 * HashMap and an ArrayList. It is very useful for representing the graph of a
 * relation.
 * 
 * @author Laurent Voisin
 */
public class ListMultimap<K, V> {

	// As a class invariant, the map never contains empty lists of values
	private final Map<K, List<V>> map;

	public ListMultimap() {
		map = new HashMap<K, List<V>>();
	}

	/**
	 * Removes all entries from this multimap.
	 */
	public void clear() {
		map.clear();
	}

	/**
	 * Returns the list of values associated with the given key. Returns
	 * <code>null</code> if the given key is not in this multimap. Never returns
	 * an empty list.
	 * 
	 * @param key
	 *            some key
	 * @return the non-empty list of values associated with the given key or
	 *         <code>null</code>
	 */
	public List<V> get(K key) {
		return map.get(key);
	}

	/**
	 * Returns the list of keys associated with a non-empty list of values.
	 * 
	 * @return the list of keys associated with some value
	 */
	public Set<K> keySet() {
		return map.keySet();
	}

	/**
	 * Associate a new value with the given key. The new value is added at the
	 * end of the list of values for the given key. No attempt is made to ensure
	 * that values in the list are unique.
	 * 
	 * @param key
	 *            some key
	 * @param value
	 *            some value
	 */
	public void put(K key, V value) {
		getValueList(key).add(value);
	}

	/**
	 * Associates the new values with the given key. The new values are added at
	 * the end of the list of values for the given key. No attempt is made to
	 * ensure that values in the list are unique.
	 * 
	 * @param key
	 *            some key
	 * @param values
	 *            some values
	 */
	public void putAll(K key, Collection<V> values) {
		if (values.isEmpty()) {
			return;
		}
		final List<V> list = getValueList(key);
		list.addAll(values);
	}

	/**
	 * Copy all pairs from the given multimap into this map. The new values are
	 * added at the end of the list of values for the same key. No attempt is
	 * made to ensure that values in the lists are unique.
	 * 
	 * @param other
	 *            some other map
	 */
	public void putAll(ListMultimap<K, V> other) {
		for (K key : other.keySet()) {
			putAll(key, other.get(key));
		}
	}

	private List<V> getValueList(K key) {
		List<V> values = map.get(key);
		if (values == null) {
			values = new ArrayList<V>();
			map.put(key, values);
		}
		return values;
	}

}
