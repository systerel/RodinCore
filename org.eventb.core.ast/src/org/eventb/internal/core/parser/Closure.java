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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.extension.CycleError;

public class Closure<T> {// TODO extends Relation<T> ?
	private final Map<T, Set<T>> reachable = new HashMap<T, Set<T>>();
	private final Map<T, Set<T>> reachableReverse = new HashMap<T, Set<T>>();

	public Closure() {
		// avoid synthetic accessor emulation
	}
	
	public Map<T, Set<T>> getRelationMap() {
		return Collections.unmodifiableMap(reachable);
	}
	
	public boolean contains(T a, T b) {
		return contains(reachable, a, b);
	}

	public void add(T a, T b) throws CycleError {
		add(reachable, a, b);
		addAll(reachable, a, get(reachable, b));
		add(reachableReverse, b, a);
		addAll(reachableReverse, b, get(reachableReverse, a));
		if (!a.equals(b) && contains(reachableReverse, a, b)) {
			throw new CycleError("Adding " + a + "|->" + b
					+ " makes a cycle.");
		}
		for (T e : get(reachableReverse, a)) {
			addAll(reachable, e, get(reachable, a));
		}
		for (T e : get(reachable, b)) {
			addAll(reachableReverse, e, get(reachableReverse, b));
		}
	}

	private static <T> void add(Map<T, Set<T>> map, T a, T b) {
		final Set<T> set = get(map, a, true);
		set.add(b);
	}

	private static <T> Set<T> get(Map<T, Set<T>> map, T a, boolean addIfNeeded) {
		Set<T> set = map.get(a);
		if (set == null) {
			set = new HashSet<T>();
			if (addIfNeeded) {
				map.put(a, set);
			}
		}
		return set;
	}

	private static <T> void addAll(Map<T, Set<T>> map, T a, Set<T> s) {
		final Set<T> set = get(map, a, true);
		set.addAll(s);
	}

	private static <T> Set<T> get(Map<T, Set<T>> map, T a) {
		return get(map, a, false);
	}

	private static <T> boolean contains(Map<T, Set<T>> map, T a, T b) {
		return get(map, a).contains(b);
	}
	
	@Override
	public String toString() {
		return reachable.toString();
	}
}