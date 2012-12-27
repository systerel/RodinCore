/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
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

/**
 * Partial implementation of a multimap, freely inspired from Guava's API. This
 * is an abstract datatype homogeneous to Map(K, List<V>) and implemented with a
 * HashMap and an ArrayList. It is very useful for representing the graph of a
 * relation.
 * 
 * @author Laurent Voisin
 */
public class ListMultimap<K, V> {

	private final Map<K, List<V>> map;

	public ListMultimap() {
		map = new HashMap<K, List<V>>();
	}

	/**
	 * Returns the list of values associated with the given key. Returns
	 * <code>null</code> if the given key is not in this multimap.
	 * 
	 * @param key
	 *            some key
	 * @return the list of values associated with the given key or
	 *         <code>null</code>
	 */
	public List<V> get(K key) {
		return map.get(key);
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
		final List<V> list = getValueList(key);
		list.addAll(values);
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
