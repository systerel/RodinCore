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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.internal.core.parser.ExternalViewUtils.Instantiator;

/**
 * @author Nicolas Beauger
 * 
 */
public class TokenSet {

	private static final int FIRST_KIND = 0;
	public static final int UNKNOWN_KIND = FIRST_KIND - 1;

	// tokens identified by their lexical image
	private final Map<String, Integer> lexTokens = new HashMap<String, Integer>();
	
	// reserved tokens, identified by an id
	private final Map<String, Integer> reserved = new HashMap<String, Integer>();
	
	// reversed access to both maps, indexed on kinds
	private String[] images = null;
	
	private int nextKind = FIRST_KIND;

	public TokenSet() {
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

		final List<Integer> freeKinds = findFreeKinds(newLexTokens, newReserved);
		final Map<String, Integer> conflictSolver = makeMap(conflicts, freeKinds);
		
		processConflicts(conflictSolver, newLexTokens, newReserved, opKindInst);
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

	private List<Integer> findFreeKinds(Map<String, Integer> newLexTokens,
			Map<String, Integer> newReserved) {
		final List<Integer> freeKinds = new ArrayList<Integer>();
		for (Integer kind = FIRST_KIND; kind < nextKind; kind++) {
			if (!newLexTokens.containsValue(kind)
					&& !newReserved.containsValue(kind)) {
				freeKinds.add(kind);
			}
		}
		return freeKinds;
	}

	private static Map<String, Integer> makeMap(Set<String> conflicts,
			List<Integer> freeKinds) {
		assert freeKinds.size() == conflicts.size();
		final Map<String, Integer> map = new HashMap<String, Integer>();
		int i=0;
		for (String string : conflicts) {
			map.put(string, freeKinds.get(i));
			i++;
		}
		return map;
	}

	private void processConflicts(Map<String, Integer> conflictSolver,
			Map<String, Integer> newLexTokens,
			Map<String, Integer> newReserved,
			Instantiator<Integer, Integer> opKindInst) {
		for (Entry<String, Integer> entry : conflictSolver.entrySet()) {
			
			final String image = entry.getKey();
			final Integer newKind = entry.getValue();
			final Integer conflictKind;
			if(newLexTokens.containsKey(image)) {
				conflictKind = newLexTokens.put(image, newKind);
			} else if (newReserved.containsKey(image)) {
				conflictKind = newReserved.put(image, newKind);
			} else {
				assert false;
				conflictKind = Integer.MIN_VALUE;
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
