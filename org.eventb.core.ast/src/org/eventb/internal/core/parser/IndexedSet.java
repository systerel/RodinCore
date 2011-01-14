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

	private static final int FIRST_KIND = 0;
	public static final int UNKNOWN_KIND = FIRST_KIND - 1;

	// tokens identified by their lexical image
	private final Map<String, Integer> lexTokens = new HashMap<String, Integer>();
	
	// reserved tokens, identified by an id
	private final Map<String, Integer> reserved = new HashMap<String, Integer>();
	
	// reversed access to both maps, indexed on kinds
	private String[] images = null;
	
	private int nextKind = FIRST_KIND;

	public IndexedSet() {
		// nothing to do
	}
	
	public int getOrAdd(String lexImage) {
		return getOrAdd(lexImage, lexTokens);
	}
	
	public int reserved(String reservedId) {
		return getOrAdd(reservedId, reserved);
	}

	private int getOrAdd(String key, Map<String, Integer> addTo) {
		final Integer current = addTo.get(key);
		if (current != null) {
			return current;
		}
		final int kind = nextKind;
		addTo.put(key, kind);
		nextKind++;
		return kind;
	}
	
	public int getKind(String lexImage) {
		return getKind(lexImage, lexTokens);
	}

	public int getReserved(String reservedId) {
		return getKind(reservedId, reserved);
	}
	
	private static int getKind(String key, Map<String, Integer> map) {
		final Integer kind = map.get(key);
		if (kind == null) {
			return UNKNOWN_KIND;
		}
		return kind;
	}
	
	public String getImage(int kind) {
		if (images != null) {
			return images[kind];
		}
		return findImage(kind);
	}

	private String findImage(int kind) {
		final String elem = getElem(kind, lexTokens);
		if (elem != null) {
			return elem;
		}
		return getElem(kind, reserved);
	}
	
	public boolean contains(String lexImage) {
		return getKind(lexImage) != UNKNOWN_KIND;
	}
	
	private static String getElem(int kind, Map<String, Integer> map) {
		for (Entry<String, Integer> entry : map.entrySet()) {
			if (entry.getValue().equals(kind)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public Set<Entry<String, Integer>> entrySet() {
		return Collections.unmodifiableSet(lexTokens.entrySet());
	}
	
	public boolean isReserved(int kind) {
		// TODO reserved.containsValue(kind) ?
		return kind >= FIRST_KIND && kind < nextKind
				&& !lexTokens.containsValue(kind);
	}

	public void redistribute(Instantiator<Integer, Integer> opKindInst) {
		final Set<String> conflicts = new HashSet<String>();
		
		final Map<String, Integer> newLexTokens = redistribute(lexTokens,
				opKindInst, conflicts);
		final Map<String, Integer> newReserved = redistribute(reserved,
				opKindInst, conflicts);

		processConflicts(conflicts, newLexTokens, newReserved, opKindInst);
		assert newLexTokens.size() == lexTokens.size();
		assert newReserved.size() == reserved.size();
		lexTokens.clear();
		lexTokens.putAll(newLexTokens);
		reserved.clear();
		reserved.putAll(newReserved);
	}

	private Map<String, Integer> redistribute(Map<String, Integer> from,
			Instantiator<Integer, Integer> opKindInst, Set<String> conflicts) {
		final Map<String, Integer> result = new HashMap<String, Integer>();
		for (Entry<String, Integer> entry : from.entrySet()) {
			final Integer kind = entry.getValue();
			if (!opKindInst.hasInst(kind)) {
				result.put(entry.getKey(), kind);
				continue;
			}
			final Integer newKind = opKindInst.instantiate(kind);
			result.put(entry.getKey(), newKind);
			if (!opKindInst.hasInst(newKind)) {
				final String elemConflict = getImage(newKind);
				if (elemConflict != null) {
					conflicts.add(elemConflict);
				}
			}
		}
		return result;
	}

	private void processConflicts(Set<String> conflicts,
			Map<String, Integer> newLexTokens,
			Map<String, Integer> newReserved,
			Instantiator<Integer, Integer> opKindInst) {
		for (String obj : conflicts) {
			final int conflictKind;
			final int newKind;
			if(newLexTokens.containsKey(obj)) {
				conflictKind = newLexTokens.remove(obj);
				newKind = getOrAdd(obj, newLexTokens);
			} else if (newReserved.containsKey(obj)) {
				conflictKind = newReserved.remove(obj);
				newKind = getOrAdd(obj, newReserved);
			} else {
				assert false;
				conflictKind = Integer.MIN_VALUE;
				newKind = Integer.MIN_VALUE;
			}
			opKindInst.setInst(conflictKind, newKind);
		}
	}

	// called when kinds are stable and contiguous;
	// initializes reversed access to images
	public void compact() {
		images = new String[nextKind];
		compact(lexTokens);
		compact(reserved);
	}

	private void compact(Map<String, Integer> map) {
		for (Entry<String, Integer> entry : map.entrySet()) {
			final int kind = entry.getValue();
			final String image = entry.getKey();
			if (images[kind] != null) {
				throw new IllegalStateException("token kind overriding: kind = "
						+ kind + " images: \"" + images[kind] + "\" and \""
						+ image + "\"");
			}
			images[kind] = image;
		}
	}
}
