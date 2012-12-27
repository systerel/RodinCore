/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser.operators;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AllInOnceMap<K,V> {
	
	private final Map<K,V> map = new HashMap<K, V>();
	
	public AllInOnceMap() {
		// avoid synthetic accessor emulation
	}
	
	public V get(K key) {
		final V value = map.get(key);
		if (value == null) {
			throw new IllegalArgumentException("no value set for key: " + key);
		}
		return value;
	}
	
	public V getNoCheck(K key) {
		return map.get(key);
	}
	
	public void put(K key, V value) {
		final V oldValue = map.put(key, value);
		if (oldValue != null && oldValue != value) {
			throw new IllegalArgumentException(
					"trying to override value for: " + key);
		}
	}
	
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	public Collection<V> values() {
		return Collections.unmodifiableCollection(map.values());
	}
	
	public Set<Entry<K, V>> entrySet() {
		return Collections.unmodifiableSet(map.entrySet());
	}

	// should be called only on bijective maps
	public Map<V, K> invert() {
		final Map<V, K> inverted = new  HashMap<V, K>();
		for (Entry<K, V> entry : map.entrySet()) {
			inverted.put(entry.getValue(), entry.getKey());
		}
		return inverted;
	}
}