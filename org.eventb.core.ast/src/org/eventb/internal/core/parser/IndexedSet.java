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

	private final Map<T, Integer> map = new HashMap<T, Integer>();
	private int nextIndex = FIRST_INDEX;

	public int getOrAdd(T key) {
		final Integer current = map.get(key);
		if (current != null) {
			return current;
		}
		final int index = nextIndex;
		map.put(key, index);
		nextIndex++;
		return index;
	}

	public int reserved() {
		final int index = nextIndex;
		nextIndex++;
		return index;
	}
	
	public int getIndex(T key) {
		final Integer index = map.get(key);
		if (index == null) {
			return NOT_AN_INDEX;
		}
		return index;
	}

	public T getKey(int index) {
		for (Entry<T, Integer> entry : map.entrySet()) {
			if (entry.getValue().equals(index)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public Set<Entry<T, Integer>> entrySet() {
		return map.entrySet();
	}
	
	public boolean isReserved(int index) {
		return index >= FIRST_INDEX && index < nextIndex
				&& !map.containsValue(index);
	}
}
