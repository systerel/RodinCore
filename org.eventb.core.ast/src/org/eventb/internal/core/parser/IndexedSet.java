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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eventb.internal.core.parser.ExternalViewUtils.Instantiator;

/**
 * @author Nicolas Beauger
 * 
 */
public class IndexedSet {

	private static final int FIRST_INDEX = 0;
	public static final int NOT_AN_INDEX = FIRST_INDEX - 1;

	private final Map<String, Integer> set = new HashMap<String, Integer>();
	private final Map<String, Integer> reserved = new HashMap<String, Integer>();
	private int nextIndex = FIRST_INDEX;

	public IndexedSet() {
		// nothing to do
	}
	
	public int getOrAdd(String key) {
		return getOrAdd(key, set);
	}
	
	public int reserved(String key) {
		return getOrAdd(key, reserved);
	}

	private int getOrAdd(String key, Map<String, Integer> addTo) {
		final Integer current = addTo.get(key);
		if (current != null) {
			return current;
		}
		final int index = nextIndex;
		addTo.put(key, index);
		nextIndex++;
		return index;
	}
	
	public int getIndex(String key) {
		return getIndex(key, set);
	}

	public int getReserved(String key) {
		return getIndex(key, reserved);
	}
	
	private static int getIndex(String key, Map<String, Integer> map) {
		final Integer index = map.get(key);
		if (index == null) {
			return NOT_AN_INDEX;
		}
		return index;
	}
	
	

	public String getElem(int index) {
		final String elem = getElem(index, set);
		if (elem != null) {
			return elem;
		}
		return getElem(index, reserved);
	}
	
	public boolean contains(String key) {
		return getIndex(key) != NOT_AN_INDEX;
	}
	
	private static String getElem(int index, Map<String, Integer> map) {
		for (Entry<String, Integer> entry : map.entrySet()) {
			if (entry.getValue().equals(index)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public Set<Entry<String, Integer>> entrySet() {
		return Collections.unmodifiableSet(set.entrySet());
	}
	
	public boolean isReserved(int index) {
		// TODO reserved.containsValue(index) ?
		return index >= FIRST_INDEX && index < nextIndex
				&& !set.containsValue(index);
	}

	public void redistribute(Instantiator<Integer, Integer> opKindInst) {
		final Set<String> conflicts = new HashSet<String>();
		
		final Map<String, Integer> newSet = redistribute(set, opKindInst, conflicts);
		final Map<String, Integer> newReserved = redistribute(reserved, opKindInst,
				conflicts);

		processConflicts(conflicts, newSet, newReserved, opKindInst);
		assert newSet.size() == set.size();
		assert newReserved.size() == reserved.size();
		set.clear();
		set.putAll(newSet);
		reserved.clear();
		reserved.putAll(newReserved);
	}

	private Map<String, Integer> redistribute(Map<String, Integer> from,
			Instantiator<Integer, Integer> opKindInst, final Set<String> conflicts) {
		final Map<String, Integer> result = new HashMap<String, Integer>();
		for (Entry<String, Integer> entry : from.entrySet()) {
			final Integer index = entry.getValue();
			if (!opKindInst.hasInst(index)) {
				result.put(entry.getKey(), index);
				continue;
			}
			final Integer newIndex = opKindInst.instantiate(index);
			result.put(entry.getKey(), newIndex);
			if (!opKindInst.hasInst(newIndex)) {
				final String elemConflict = getElem(newIndex);
				if (elemConflict != null) {
					conflicts.add(elemConflict);
				}
			}
		}
		return result;
	}

	private void processConflicts(Set<String> conflicts,
			Map<String, Integer> newSet, Map<String, Integer> newReserved, Instantiator<Integer, Integer> opKindInst) {
		for (String obj : conflicts) {
			final int conflictIndex;
			final int newIndex;
			if(newSet.containsKey(obj)) {
				conflictIndex = newSet.remove(obj);
				newIndex = getOrAdd(obj, newSet);
			} else if (newReserved.containsKey(obj)) {
				conflictIndex = newReserved.remove(obj);
				newIndex = getOrAdd(obj, newReserved);
			} else {
				assert false;
				conflictIndex = Integer.MIN_VALUE;
				newIndex = Integer.MIN_VALUE;
			}
			opKindInst.setInst(conflictIndex, newIndex);
		}
	}

}
