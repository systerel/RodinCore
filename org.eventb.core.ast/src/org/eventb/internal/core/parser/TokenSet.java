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
package org.eventb.internal.core.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Nicolas Beauger
 * 
 */
public class TokenSet {

	public static final int FIRST_KIND = 0;
	public static final int UNKNOWN_KIND = FIRST_KIND - 1;

	// tokens identified by their lexical image
	private final Map<String, Integer> lexTokens;
	
	// reserved tokens, identified by an id
	private final Map<String, Integer> reserved;
	
	// reversed access to both maps, indexed on kinds
	private final String[] images;
	
	private int nextKind = FIRST_KIND;

	public TokenSet() {
		this.lexTokens = new HashMap<String, Integer>();
		this.reserved = new HashMap<String, Integer>();
		this.images = null;
	}
	
	// given kind maps start from FIRST_KIND
	// kinds are contiguous and pairwise distinct
	public TokenSet(Map<String, Integer> lexTokens,
			Map<String, Integer> reserved) {
		this.lexTokens = lexTokens;
		this.reserved = reserved;
		this.images = new String[lexTokens.size() + reserved.size()];
		initImages(lexTokens);
		initImages(reserved);
	}
	
	// called when kinds are stable and contiguous;
	// initializes reversed access to images
	private void initImages(Map<String, Integer> map) {
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

	public int size() {
		return nextKind;
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
		 return reserved.containsValue(kind);
	}

}
