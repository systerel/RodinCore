/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Nicolas Beauger
 * 
 */
public class IndexedSet<T> {

	private static final int FIRST_INDEX = 0;
	public static final int NOT_AN_INDEX = FIRST_INDEX - 1;

	private final Map<T, Integer> set = new HashMap<T, Integer>();
	private final Map<T, Integer> reserved = new HashMap<T, Integer>();
	private int nextIndex = FIRST_INDEX;

	public IndexedSet() {
		// nothing to do
	}
	
	public IndexedSet(IndexedSet<T> other) {
		this.set.putAll(other.set);
		this.reserved.putAll(other.reserved);
		this.nextIndex = other.nextIndex;
	}
	
	public int getOrAdd(T key) {
		return getOrAdd(key, set);
	}
	
	public int reserved(T key) {
		return getOrAdd(key, reserved);
	}

	private int getOrAdd(T key, Map<T, Integer> addTo) {
		final Integer current = addTo.get(key);
		if (current != null) {
			return current;
		}
		final int index = nextIndex;
		addTo.put(key, index);
		nextIndex++;
		return index;
	}
	
	public int getIndex(T key) {
		final Integer index = set.get(key);
		if (index == null) {
			return NOT_AN_INDEX;
		}
		return index;
	}

	public T getElem(int index) {
		final T elem = getElem(index, set);
		if (elem != null) {
			return elem;
		}
		return getElem(index, reserved);
	}
	
	public boolean contains(T key) {
		return getIndex(key) != NOT_AN_INDEX;
	}
	
	private static <T> T getElem(int index, Map<T, Integer> map) {
		for (Entry<T, Integer> entry : map.entrySet()) {
			if (entry.getValue().equals(index)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public Set<Entry<T, Integer>> entrySet() {
		return Collections.unmodifiableSet(set.entrySet());
	}
	
	public boolean isReserved(int index) {
		// TODO reserved.containsValue(index) ?
		return index >= FIRST_INDEX && index < nextIndex
				&& !set.containsValue(index);
	}

}
